package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Quest
import com.example.data.QuestData
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToQuest: () -> Unit,
    onNavigateToPlayground: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedQuests by viewModel.completedQuestIds.collectAsState()
    val totalQuests = QuestData.quests.size
    val completedCount = completedQuests.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(KidsBg)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- Warm Mascot Header ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(KidsPurple, KidsOrange)
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "🐍",
                        fontSize = 64.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Column {
                        Text(
                            text = "Hi, Future Coder!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Monty the Python is ready to teach you computer magic!",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // --- Progress Tracker Banner ---
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Your Wizard Progress",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$completedCount of $totalQuests Quests Cleared!",
                            fontSize = 14.sp,
                            color = TextLight
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { if (totalQuests > 0) completedCount.toFloat() / totalQuests else 0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(CircleShape),
                            color = KidsGreen,
                            trackColor = KidsBorder
                        )
                    }
                    Text(
                        text = "🏆",
                        fontSize = 36.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        // --- Playground Card ---
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2E6)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToPlayground() }
                    .testTag("playground_button")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(KidsOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "Sandbox",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Free Coding Sandbox 🎈",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "Summon anything! No rules, just pure coding play!",
                            fontSize = 13.sp,
                            color = TextLight
                        )
                    }
                }
            }
        }

        // --- Quests Headline ---
        item {
            Text(
                text = "Your Quest Path",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        // --- Quest List ---
        itemsIndexed(QuestData.quests) { index, quest ->
            val isCompleted = completedQuests.contains(quest.id)
            val isUnlocked = index == 0 || completedQuests.contains(QuestData.quests[index - 1].id)

            QuestItemRow(
                quest = quest,
                isUnlocked = isUnlocked,
                isCompleted = isCompleted,
                onClick = {
                    if (isUnlocked) {
                        viewModel.selectQuest(quest)
                        onNavigateToQuest()
                    }
                }
            )
        }
    }
}

@Composable
fun QuestItemRow(
    quest: Quest,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) MaterialTheme.colorScheme.surface else Color(0xFFF3F0FC)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isUnlocked) 2.dp else 0.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isUnlocked) { onClick() }
            .testTag("quest_card_${quest.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quest Icon Bubble
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (isUnlocked) {
                            if (isCompleted) KidsMint else KidsBorder.copy(alpha = 0.5f)
                        } else {
                            Color(0xFFE4E1EE)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(text = quest.icon, fontSize = 32.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = TextLight,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quest.levelName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) KidsPurple else TextLight
                )
                Text(
                    text = quest.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) TextDark else TextLight.copy(alpha = 0.8f)
                )
                Text(
                    text = quest.description,
                    fontSize = 12.sp,
                    color = TextLight,
                    maxLines = 1
                )
            }

            // Action Badge
            Box(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = KidsGreen,
                        modifier = Modifier.size(32.dp)
                    )
                } else if (isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = KidsPurple,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
