package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quest_progress")
data class QuestProgress(
    @PrimaryKey val questId: String,
    val isCompleted: Boolean,
    val lastCode: String = "",
    val completionTime: Long = System.currentTimeMillis()
)
