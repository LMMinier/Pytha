package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.Content
import com.example.api.GeminiClient
import com.example.api.GenerateContentRequest
import com.example.api.Part
import com.example.data.Quest
import com.example.data.QuestData
import com.example.data.database.AppDatabase
import com.example.data.database.QuestProgress
import com.example.data.database.QuestRepository
import com.example.interpreter.InterpreterResult
import com.example.interpreter.PythonInterpreter
import com.example.interpreter.PythonOutput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: QuestRepository
    
    init {
        val database = AppDatabase.getDatabase(application)
        repository = QuestRepository(database.questProgressDao())
    }

    val completedQuestIds: StateFlow<Set<String>> = repository.allProgress
        .combine(MutableStateFlow(QuestData.quests)) { progressList, _ ->
            progressList.filter { it.isCompleted }.map { it.questId }.toSet()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )
        
    private val _currentQuest = MutableStateFlow<Quest?>(null)
    val currentQuest = _currentQuest.asStateFlow()
    
    private val _editorCode = MutableStateFlow("")
    val editorCode = _editorCode.asStateFlow()
    
    private val _interpreterResult = MutableStateFlow<InterpreterResult?>(null)
    val interpreterResult = _interpreterResult.asStateFlow()
    
    // Playground Code
    private val _playgroundCode = MutableStateFlow(
        "# Welcome to Monty's Playground! 🐍\n# Write any code you like here!\n\ntoy_box = \"🦄\"\nprint(toy_box)\n\nfor i in range(5):\n    print(\"🎈\")"
    )
    val playgroundCode = _playgroundCode.asStateFlow()

    // Monty's AI chat states
    private val _montyAdvice = MutableStateFlow<String?>(null)
    val montyAdvice = _montyAdvice.asStateFlow()
    
    private val _isMontyLoading = MutableStateFlow(false)
    val isMontyLoading = _isMontyLoading.asStateFlow()
    
    fun selectQuest(quest: Quest) {
        _currentQuest.value = quest
        viewModelScope.launch {
            val progress = repository.getProgressForQuest(quest.id)
            _editorCode.value = progress?.lastCode?.ifEmpty { quest.startingCode } ?: quest.startingCode
            _interpreterResult.value = null
            _montyAdvice.value = null
        }
    }
    
    fun updateCode(newCode: String) {
        _editorCode.value = newCode
    }
    
    fun updatePlaygroundCode(newCode: String) {
        _playgroundCode.value = newCode
    }
    
    fun appendCodeBlock(block: String) {
        val current = _editorCode.value
        val updated = if (current.endsWith(" ") || current.endsWith("\n") || current.isEmpty()) {
            current + block
        } else {
            current + " " + block
        }
        _editorCode.value = updated
    }
    
    fun appendPlaygroundBlock(block: String) {
        val current = _playgroundCode.value
        val updated = if (current.endsWith(" ") || current.endsWith("\n") || current.isEmpty()) {
            current + block
        } else {
            current + " " + block
        }
        _playgroundCode.value = updated
    }

    fun runCurrentQuestCode() {
        val quest = _currentQuest.value ?: return
        val code = _editorCode.value
        val result = PythonInterpreter.run(code)
        _interpreterResult.value = result
        
        if (result.success) {
            val codeContainsKeyword = code.contains(quest.targetKeyword)
            val outputContainsEmoji = quest.targetEmoji == null || result.outputs.any { 
                it is PythonOutput.SpawnEmoji && it.emoji == quest.targetEmoji
            }
            
            if (codeContainsKeyword && outputContainsEmoji) {
                viewModelScope.launch {
                    repository.saveProgress(quest.id, isCompleted = true, lastCode = code)
                }
            }
        }
    }
    
    fun runPlaygroundCode() {
        val code = _playgroundCode.value
        val result = PythonInterpreter.run(code)
        _interpreterResult.value = result
    }
    
    fun resetResult() {
        _interpreterResult.value = null
    }

    fun askMontyForHelp(isPlayground: Boolean = false) {
        val code = if (isPlayground) _playgroundCode.value else _editorCode.value
        val quest = if (isPlayground) null else _currentQuest.value
        
        _isMontyLoading.value = true
        _montyAdvice.value = null
        
        viewModelScope.launch {
            try {
                val apiKey = com.example.BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _montyAdvice.value = "Oops! Monty is missing his AI thinking cap (API key). Instruct your teacher to configure the API key in the AI Studio Secrets panel! 🐍🔑"
                    _isMontyLoading.value = false
                    return@launch
                }
                
                val prompt = """
                    You are Monty, a cute, extremely friendly, and cheerful Python snake mascot who teaches kids how to code in Python! 🐍
                    Your audience is 5 to 10 years old. Speak simply, with lots of enthusiasm, emojis, and sweet encouragement. 
                    Avoid using complex words or dry computer science jargon. Instead, use simple analogies like comparing "variables" to "toy boxes" or "print()" to a "magic summoning spell".
                    
                    Here is the child's Python code they ran:
                    ```python
                    $code
                    ```
                    
                    ${if (quest != null) "They are currently on the quest: '${quest.title}'. The goal is to: ${quest.instruction}." else "They are playing in your free sandbox playground."}
                    
                    Please review their code:
                    1. Keep your reply short (under 4 paragraphs).
                    2. Use a fun, loving tone with many emojis.
                    3. If their code has a syntax error or didn't pass the level, gently show them why with an analogy, and give a tiny hint to fix it.
                    4. If their code is correct, shower them with praise and celebrate with starry, happy emojis!
                """.trimIndent()
                
                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt))))
                )
                
                val response = GeminiClient.service.generateContent(apiKey, request)
                val advice = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Monty is a little sleepy right now. Try asking him again in a second! 🐍💤"
                _montyAdvice.value = advice
            } catch (e: Exception) {
                _montyAdvice.value = "Oh no! Monty's internet mail got lost in the jungle! 🌴 Let's check our internet connection and try again."
            } finally {
                _isMontyLoading.value = false
            }
        }
    }
}
