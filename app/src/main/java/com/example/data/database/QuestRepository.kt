package com.example.data.database

import kotlinx.coroutines.flow.Flow

class QuestRepository(private val questProgressDao: QuestProgressDao) {
    val allProgress: Flow<List<QuestProgress>> = questProgressDao.getAllProgress()

    suspend fun saveProgress(questId: String, isCompleted: Boolean, lastCode: String) {
        val progress = QuestProgress(
            questId = questId,
            isCompleted = isCompleted,
            lastCode = lastCode,
            completionTime = System.currentTimeMillis()
        )
        questProgressDao.saveProgress(progress)
    }

    suspend fun getProgressForQuest(questId: String): QuestProgress? {
        return questProgressDao.getProgressForQuest(questId)
    }
}
