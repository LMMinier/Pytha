package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestProgressDao {
    @Query("SELECT * FROM quest_progress")
    fun getAllProgress(): Flow<List<QuestProgress>>

    @Query("SELECT * FROM quest_progress WHERE questId = :questId LIMIT 1")
    suspend fun getProgressForQuest(questId: String): QuestProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: QuestProgress)

    @Query("DELETE FROM quest_progress")
    suspend fun clearAll()
}
