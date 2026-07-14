package com.example.ui

import android.app.Application
import android.content.Context
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

data class XpEvent(val amount: Int, val message: String, val id: Long = System.currentTimeMillis())

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: QuestRepository
    private val prefs = application.getSharedPreferences("python_quest_prefs", Context.MODE_PRIVATE)
    
    private val _selectedAgeTier = MutableStateFlow<String?>(prefs.getString("age_tier", null))
    val selectedAgeTier = _selectedAgeTier.asStateFlow()

    private val _xpPoints = MutableStateFlow(prefs.getInt("xp_points", 0))
    val xpPoints = _xpPoints.asStateFlow()

    private val _xpEarnedEvent = MutableStateFlow<XpEvent?>(null)
    val xpEarnedEvent = _xpEarnedEvent.asStateFlow()

    private val _confettiTrigger = MutableStateFlow<Long?>(null)
    val confettiTrigger = _confettiTrigger.asStateFlow()

    private val _streakCount = MutableStateFlow(prefs.getInt("streak_count", 3)) // Defaults to a cute 3-day streak
    val streakCount = _streakCount.asStateFlow()

    private val _selectedAvatar = MutableStateFlow(prefs.getString("selected_avatar", "classic"))
    val selectedAvatar = _selectedAvatar.asStateFlow()

    fun updateSelectedAvatar(avatar: String) {
        prefs.edit().putString("selected_avatar", avatar).apply()
        _selectedAvatar.value = avatar
    }

    fun incrementStreak() {
        val next = _streakCount.value + 1
        prefs.edit().putInt("streak_count", next).apply()
        _streakCount.value = next
        addXp(50, "Streak Multiplier")
        triggerConfetti()
    }

    fun triggerConfetti() {
        _confettiTrigger.value = System.currentTimeMillis()
    }

    fun dismissConfetti() {
        _confettiTrigger.value = null
    }

    fun addXp(points: Int, reason: String = "") {
        val current = _xpPoints.value
        val next = current + points
        prefs.edit().putInt("xp_points", next).apply()
        _xpPoints.value = next
        
        val msg = if (reason.isNotEmpty()) "$reason! +$points XP 🐍✨" else "You earned +$points Python XP! 🐍✨"
        _xpEarnedEvent.value = XpEvent(points, msg)
    }

    fun dismissXpEvent() {
        _xpEarnedEvent.value = null
    }

    fun resetXp() {
        prefs.edit().putInt("xp_points", 0).apply()
        _xpPoints.value = 0
        _xpEarnedEvent.value = null
        prefs.edit().putInt("streak_count", 1).apply()
        _streakCount.value = 1
    }
    
    init {
        val database = AppDatabase.getDatabase(application)
        repository = QuestRepository(database.questProgressDao())
        
        // Setup initial active date for streak tracking
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val todayStr = sdf.format(java.util.Date())
        val lastActive = prefs.getString("last_active_date", null)
        if (lastActive == null) {
            prefs.edit().putString("last_active_date", todayStr).apply()
        } else if (lastActive != todayStr) {
            try {
                val lastDate = sdf.parse(lastActive)
                val todayDate = sdf.parse(todayStr)
                val diffMs = todayDate.time - lastDate.time
                val diffDays = diffMs / (1000 * 60 * 60 * 24)
                if (diffDays == 1L) {
                    val next = _streakCount.value + 1
                    prefs.edit().putString("last_active_date", todayStr).putInt("streak_count", next).apply()
                    _streakCount.value = next
                    addXp(25, "Daily Streak Maintained")
                } else if (diffDays > 1L) {
                    // Reset streak to 1 if they missed a day
                    prefs.edit().putString("last_active_date", todayStr).putInt("streak_count", 1).apply()
                    _streakCount.value = 1
                }
            } catch (e: Exception) {
                prefs.edit().putString("last_active_date", todayStr).apply()
            }
        }
    }

    fun selectAgeTier(tier: String) {
        prefs.edit().putString("age_tier", tier).apply()
        _selectedAgeTier.value = tier
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
        "# Welcome to Cyber Terminal IDE!\n# Write and execute Python scripts below:\n\naccess_token = \"🛡️\"\nprint(access_token)\n\nfor i in range(5):\n    print(\"⚡\")"
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
                    val alreadyCompleted = completedQuestIds.value.contains(quest.id)
                    repository.saveProgress(quest.id, isCompleted = true, lastCode = code)
                    if (!alreadyCompleted) {
                        addXp(100, "Quest Completed")
                    }
                    triggerConfetti()
                }
            }
        }
    }
    
    fun runPlaygroundCode() {
        val code = _playgroundCode.value
        val result = PythonInterpreter.run(code)
        _interpreterResult.value = result
        
        if (result.success && code.isNotBlank()) {
            val lastSandboxCode = prefs.getString("last_sandbox_code", "")
            if (code != lastSandboxCode) {
                prefs.edit().putString("last_sandbox_code", code).apply()
                addXp(15, "Sandbox Experiment")
            }
            triggerConfetti()
        }
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
                    _montyAdvice.value = "System Error: Missing API configuration token (GEMINI_API_KEY). Ensure credentials are fully provisioned within your environment settings panel."
                    _isMontyLoading.value = false
                    return@launch
                }
                
                val prompt = """
                    You are Cyber Terminal AI, a highly advanced developer compiler assistant! 💻
                    
                    Explain concepts with structured clarity, system level insights, and friendly developer tone. Focus on helpful Python standards, readability, and logic flow, avoiding childish words while remaining incredibly encouraging and easy to understand.
                    
                    Here is the learner's Python code they ran:
                    ```python
                    $code
                    ```
                    
                    ${if (quest != null) "They are currently on the quest: '${quest.title}'. The goal is to: ${quest.instruction}." else "They are playing in your free sandbox playground."}
                    
                    Please review their code:
                    1. Keep your reply short (under 4 paragraphs).
                    2. Use a professional, encouraging developer tone with code styling tips.
                    3. If their code has a syntax error or didn't pass the level, gently show them why, and give a clear tip to fix it.
                    4. If their code is correct, celebrate with positive terminal-style output!
                """.trimIndent()
                
                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt))))
                )
                
                val response = GeminiClient.service.generateContent(apiKey, request)
                val advice = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "System Daemon is busy compiling resources. Please try again in a moment."
                _montyAdvice.value = advice
            } catch (e: Exception) {
                _montyAdvice.value = "Network Connection Timeout: Mainframe lost contact with standard secure port. Please check your network adapters and try again."
            } finally {
                _isMontyLoading.value = false
            }
        }
    }
}
