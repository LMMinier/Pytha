package com.example.interpreter

import android.util.Log

sealed interface PythonOutput {
    data class Text(val text: String) : PythonOutput
    data class SpawnEmoji(val emoji: String, val count: Int = 1) : PythonOutput
    data class Speech(val text: String) : PythonOutput
}

data class InterpreterResult(
    val success: Boolean,
    val outputs: List<PythonOutput>,
    val finalVariables: Map<String, Any>,
    val errorMessage: String? = null
)

object PythonInterpreter {

    private val emojiRegex = "[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+".toRegex()

    fun run(code: String): InterpreterResult {
        val outputs = mutableListOf<PythonOutput>()
        val variables = mutableMapOf<String, Any>()
        
        val lines = code.lines()
        var index = 0
        
        try {
            executeBlock(lines, 0, lines.size, variables, outputs, currentIndent = 0)
            return InterpreterResult(
                success = true,
                outputs = outputs,
                finalVariables = variables
            )
        } catch (e: InterpreterException) {
            return InterpreterResult(
                success = false,
                outputs = outputs,
                finalVariables = variables,
                errorMessage = e.message
            )
        } catch (e: Exception) {
            return InterpreterResult(
                success = false,
                outputs = outputs,
                finalVariables = variables,
                errorMessage = "SyntaxError: Compilation error. Verify indentation and operators."
            )
        }
    }

    private fun executeBlock(
        lines: List<String>,
        start: Int,
        end: Int,
        variables: MutableMap<String, Any>,
        outputs: MutableList<PythonOutput>,
        currentIndent: Int
    ): Int {
        var i = start
        while (i < end) {
            val rawLine = lines[i]
            
            // Skip empty lines
            if (rawLine.trim().isEmpty()) {
                i++
                continue
            }
            
            // Check indent level
            val indent = getIndentLevel(rawLine)
            if (indent < currentIndent) {
                // Return control back to parent block
                return i
            }
            
            val line = rawLine.trim()
            
            // Skip comments
            if (line.startsWith("#")) {
                i++
                continue
            }
            
            when {
                // --- For Loop parsing ---
                // Pattern: for i in range(5): or for x in range(count):
                line.startsWith("for ") -> {
                    val colonIndex = line.lastIndexOf(":")
                    if (colonIndex == -1) {
                        throw InterpreterException(i + 1, "Missing ':' at the end of your loop line!")
                    }
                    val loopHeader = line.substring(0, colonIndex).trim()
                    // Extract loop variable and range
                    val parts = loopHeader.split("\\s+".toRegex())
                    if (parts.size < 4 || parts[2] != "in" || !parts[3].startsWith("range(")) {
                        throw InterpreterException(i + 1, "Loop header must look like: for i in range(5):")
                    }
                    
                    val loopVar = parts[1]
                    val rangeContent = parts[3].substringAfter("range(").substringBeforeLast(")")
                    val iterations = evaluateIntExpression(rangeContent, variables, i + 1)
                    
                    // Collect loop block lines (lines with higher indentation)
                    val blockStart = i + 1
                    var blockEnd = blockStart
                    while (blockEnd < end) {
                        val nextLine = lines[blockEnd]
                        if (nextLine.trim().isEmpty()) {
                            blockEnd++
                            continue
                        }
                        if (getIndentLevel(nextLine) <= indent) {
                            break
                        }
                        blockEnd++
                    }
                    
                    if (blockStart == blockEnd) {
                        throw InterpreterException(i + 1, "Your loop has nothing inside! Add some indented code below it.")
                    }
                    
                    // Run the loop body recursively
                    for (count in 0 until iterations) {
                        variables[loopVar] = count
                        executeBlock(lines, blockStart, blockEnd, variables, outputs, currentIndent = indent + 1)
                    }
                    
                    // Resume execution after the loop block
                    i = blockEnd
                }
                
                // --- If Statement parsing ---
                // Pattern: if score == 5: or if weather == "sunny":
                line.startsWith("if ") -> {
                    val colonIndex = line.lastIndexOf(":")
                    if (colonIndex == -1) {
                        throw InterpreterException(i + 1, "Missing ':' at the end of your if statement!")
                    }
                    val conditionStr = line.substring(3, colonIndex).trim()
                    val conditionMet = evaluateCondition(conditionStr, variables, i + 1)
                    
                    // Collect 'if' block lines
                    val ifBlockStart = i + 1
                    var ifBlockEnd = ifBlockStart
                    while (ifBlockEnd < end) {
                        val nextLine = lines[ifBlockEnd]
                        if (nextLine.trim().isEmpty()) {
                            ifBlockEnd++
                            continue
                        }
                        if (getIndentLevel(nextLine) <= indent) {
                            break
                        }
                        ifBlockEnd++
                    }
                    
                    // Check if there's an 'else:' block right after
                    var elseBlockStart = -1
                    var elseBlockEnd = -1
                    
                    var checkElseIndex = ifBlockEnd
                    while (checkElseIndex < end && lines[checkElseIndex].trim().isEmpty()) {
                        checkElseIndex++
                    }
                    
                    if (checkElseIndex < end && lines[checkElseIndex].trim().startsWith("else:")) {
                        elseBlockStart = checkElseIndex + 1
                        elseBlockEnd = elseBlockStart
                        while (elseBlockEnd < end) {
                            val nextLine = lines[elseBlockEnd]
                            if (nextLine.trim().isEmpty()) {
                                elseBlockEnd++
                                continue
                            }
                            if (getIndentLevel(nextLine) <= indent) {
                                break
                            }
                            elseBlockEnd++
                        }
                        // Advance index to skip the else block as well
                        i = elseBlockEnd
                    } else {
                        i = ifBlockEnd
                    }
                    
                    // Run the correct block
                    if (conditionMet) {
                        executeBlock(lines, ifBlockStart, ifBlockEnd, variables, outputs, currentIndent = indent + 1)
                    } else if (elseBlockStart != -1) {
                        executeBlock(lines, elseBlockStart, elseBlockEnd, variables, outputs, currentIndent = indent + 1)
                    }
                }
                
                // --- Print statement parsing ---
                // Pattern: print("hello") or print(my_variable)
                line.startsWith("print(") -> {
                    if (!line.endsWith(")")) {
                        throw InterpreterException(i + 1, "Make sure to close your print with a ')' at the end!")
                    }
                    val expression = line.substringAfter("print(").substringBeforeLast(")")
                    val outputVal = evaluateExpression(expression, variables, i + 1)
                    val outputStr = outputVal.toString()
                    
                    outputs.add(PythonOutput.Text(outputStr))
                    
                    // Check if printed output has emojis to spawn them in the graphic meadow!
                    val emojisInText = emojiRegex.findAll(outputStr).map { it.value }.toList()
                    if (emojisInText.isNotEmpty()) {
                        emojisInText.forEach { emo ->
                            outputs.add(PythonOutput.SpawnEmoji(emo))
                        }
                    } else {
                        // Special child phrases can also spawn things
                        val lowercase = outputStr.lowercase()
                        when {
                            "cat" in lowercase || "kitty" in lowercase -> outputs.add(PythonOutput.SpawnEmoji("🐱"))
                            "dog" in lowercase || "puppy" in lowercase -> outputs.add(PythonOutput.SpawnEmoji("🐶"))
                            "pizza" in lowercase -> outputs.add(PythonOutput.SpawnEmoji("🍕"))
                            "balloon" in lowercase -> outputs.add(PythonOutput.SpawnEmoji("🎈"))
                            "rocket" in lowercase -> outputs.add(PythonOutput.SpawnEmoji("🚀"))
                            "star" in lowercase -> outputs.add(PythonOutput.SpawnEmoji("⭐"))
                            "dinosaur" in lowercase || "dino" in lowercase -> outputs.add(PythonOutput.SpawnEmoji("🦖"))
                            "lion" in lowercase -> outputs.add(PythonOutput.SpawnEmoji("🦁"))
                            "unicorn" in lowercase -> outputs.add(PythonOutput.SpawnEmoji("🦄"))
                        }
                    }
                    i++
                }
                
                // --- Variable assignment parsing ---
                // Pattern: x = 5 or name = "Rex"
                line.contains("=") -> {
                    val parts = line.split("=", limit = 2)
                    val varName = parts[0].trim()
                    val expression = parts[1].trim()
                    
                    if (!isValidIdentifier(varName)) {
                        throw InterpreterException(i + 1, "NameError: '$varName' is not a valid identifier name. Use letters or underscores only.")
                    }
                    
                    val value = evaluateExpression(expression, variables, i + 1)
                    variables[varName] = value
                    i++
                }
                
                else -> {
                    throw InterpreterException(i + 1, "SyntaxError: Unexpected instruction block: '$line'. Verify Python syntax conventions.")
                }
            }
        }
        return i
    }

    private fun getIndentLevel(line: String): Int {
        var spaces = 0
        for (char in line) {
            if (char == ' ') {
                spaces++
            } else if (char == '\t') {
                spaces += 4
            } else {
                break
            }
        }
        return spaces / 4
    }

    private fun isValidIdentifier(name: String): Boolean {
        if (name.isEmpty()) return false
        val first = name[0]
        if (!first.isLetter() && first != '_') return false
        return name.all { it.isLetterOrDigit() || it == '_' }
    }

    private fun evaluateExpression(expr: String, variables: Map<String, Any>, lineNum: Int): Any {
        val trimmed = expr.trim()
        if (trimmed.isEmpty()) {
            throw InterpreterException(lineNum, "Empty value assigned!")
        }
        
        // String literal
        if ((trimmed.startsWith("\"") && trimmed.endsWith("\"")) || 
            (trimmed.startsWith("'") && trimmed.endsWith("'"))) {
            return trimmed.substring(1, trimmed.length - 1)
        }
        
        // Integer literal
        val intVal = trimmed.toIntOrNull()
        if (intVal != null) {
            return intVal
        }
        
        // Boolean literal
        if (trimmed == "True") return true
        if (trimmed == "False") return false
        
        // Variable lookup
        if (variables.containsKey(trimmed)) {
            return variables[trimmed]!!
        }
        
        // Basic arithmetic (e.g. x + 1)
        if (trimmed.contains("+")) {
            val parts = trimmed.split("+", limit = 2)
            val left = evaluateExpression(parts[0], variables, lineNum)
            val right = evaluateExpression(parts[1], variables, lineNum)
            if (left is Int && right is Int) {
                return left + right
            }
            if (left is String || right is String) {
                return left.toString() + right.toString()
            }
        }
        
        throw InterpreterException(lineNum, "NameError: name '$trimmed' is not defined. Ensure variables are declared prior to execution.")
    }

    private fun evaluateIntExpression(expr: String, variables: Map<String, Any>, lineNum: Int): Int {
        val value = evaluateExpression(expr, variables, lineNum)
        if (value is Int) return value
        val intAttempt = value.toString().toIntOrNull()
        if (intAttempt != null) return intAttempt
        throw InterpreterException(lineNum, "Expected a number, but got '$value'")
    }

    private fun evaluateCondition(cond: String, variables: Map<String, Any>, lineNum: Int): Boolean {
        val operators = listOf("==", ">=", "<=", ">", "<", "!=")
        var selectedOp: String? = null
        for (op in operators) {
            if (cond.contains(op)) {
                selectedOp = op
                break
            }
        }
        
        if (selectedOp == null) {
            // Check if condition is a boolean variable
            val exprVal = evaluateExpression(cond, variables, lineNum)
            if (exprVal is Boolean) return exprVal
            return exprVal.toString().isNotEmpty()
        }
        
        val parts = cond.split(selectedOp, limit = 2)
        val left = evaluateExpression(parts[0], variables, lineNum)
        val right = evaluateExpression(parts[1], variables, lineNum)
        
        return when (selectedOp) {
            "==" -> left == right
            "!=" -> left != right
            ">" -> {
                val leftInt = left as? Int ?: left.toString().toIntOrNull() ?: 0
                val rightInt = right as? Int ?: right.toString().toIntOrNull() ?: 0
                leftInt > rightInt
            }
            "<" -> {
                val leftInt = left as? Int ?: left.toString().toIntOrNull() ?: 0
                val rightInt = right as? Int ?: right.toString().toIntOrNull() ?: 0
                leftInt < rightInt
            }
            ">=" -> {
                val leftInt = left as? Int ?: left.toString().toIntOrNull() ?: 0
                val rightInt = right as? Int ?: right.toString().toIntOrNull() ?: 0
                leftInt >= rightInt
            }
            "<=" -> {
                val leftInt = left as? Int ?: left.toString().toIntOrNull() ?: 0
                val rightInt = right as? Int ?: right.toString().toIntOrNull() ?: 0
                leftInt <= rightInt
            }
            else -> false
        }
    }
}

class InterpreterException(val line: Int, message: String) : Exception("Line $line: $message")
