package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.MenuBook
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.Quest
import com.example.data.QuestData
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToQuest: () -> Unit,
    onNavigateToPlayground: () -> Unit,
    onNavigateToBooks: () -> Unit,
    onNavigateToAgeSelection: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedQuests by viewModel.completedQuestIds.collectAsState()
    val selectedTier by viewModel.selectedAgeTier.collectAsState()
    val selectedAvatarId by viewModel.selectedAvatar.collectAsState()
    val currentAvatar = AvatarsList.firstOrNull { it.id == selectedAvatarId } ?: AvatarsList[0]
    
    val totalQuests = QuestData.quests.size
    val completedCount = completedQuests.size

    val tierFriendlyName = when (selectedTier) {
        "8_10" -> "Young Wizard 🎈"
        "11_13" -> "Junior Coder 🚀"
        "14_17" -> "Apprentice ⚙️"
        else -> "Curious Mind 💡"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(KidsBg)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- Visual Hero Banner ---
        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, KidsBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_home_banner),
                    contentDescription = "Python Quest Coding Adventure",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // --- Warm Mascot Header ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White)
                    .border(1.dp, KidsBorder, RoundedCornerShape(28.dp))
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(2.dp, currentAvatar.startColor, CircleShape)
                            .clickable { onNavigateToProfile() }
                            .testTag("avatar_profile_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_monty_avatar),
                            contentDescription = currentAvatar.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            colorFilter = if (currentAvatar.id != "classic") {
                                ColorFilter.tint(currentAvatar.glowColor.copy(alpha = 0.35f), BlendMode.SrcAtop)
                            } else null
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hi, Future Coder! 👋",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(KidsPurple.copy(alpha = 0.1f))
                                    .clickable { onNavigateToAgeSelection() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = tierFriendlyName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KidsPurple
                                )
                            }
                            Text(
                                text = "Change",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = KidsOrange,
                                modifier = Modifier
                                    .clickable { onNavigateToAgeSelection() }
                                    .padding(4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Meet ${currentAvatar.name.split(" ")[0]}! View explorer profile 👤",
                            fontSize = 13.sp,
                            color = KidsPurple,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToProfile() }
                        )
                    }
                }
            }
        }

        // --- Progress Tracker Banner ---
        item {
            val xpPoints by viewModel.xpPoints.collectAsState()
            
            val level = when {
                xpPoints < 200 -> 1
                xpPoints < 500 -> 2
                xpPoints < 900 -> 3
                xpPoints < 1400 -> 4
                else -> 5
            }
            
            val levelTitle = when (level) {
                1 -> "Python Hatchling 🥚"
                2 -> "Snakelet Wizard 🐍"
                3 -> "Spellslinger ⚡"
                4 -> "Mage of Code 🔮"
                else -> "Python Archmage 👑"
            }
            
            val (minXp, maxXp) = when (level) {
                1 -> Pair(0, 200)
                2 -> Pair(200, 500)
                3 -> Pair(500, 900)
                4 -> Pair(900, 1400)
                else -> Pair(1400, 3000)
            }
            
            val levelProgress = if (level < 5) {
                (xpPoints - minXp).toFloat() / (maxXp - minXp).toFloat()
            } else {
                1.0f
            }

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, KidsBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToProfile() }
                    .testTag("progress_banner_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title block
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Wizard Progress 🏆",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Text(
                                text = "$completedCount of $totalQuests Quests Cleared!",
                                fontSize = 13.sp,
                                color = TextLight
                            )
                        }
                        
                        // Age tier or Badge indicator
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(KidsPurple.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "LEVEL $level",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = KidsPurple
                            )
                        }
                    }

                    // Quest bar
                    LinearProgressIndicator(
                        progress = { if (totalQuests > 0) completedCount.toFloat() / totalQuests else 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = KidsGreen,
                        trackColor = KidsBorder
                    )

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(KidsBorder.copy(alpha = 0.5f))
                    )

                    // XP and Level Progression Block
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(KidsOrange.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✨", fontSize = 28.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = levelTitle,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Text(
                                    text = "$xpPoints / $maxXp XP",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = KidsOrange
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            // XP Progress bar
                            LinearProgressIndicator(
                                progress = { levelProgress.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(CircleShape),
                                color = KidsOrange,
                                trackColor = KidsBorder
                            )
                        }
                    }
                    
                    // Fun motivator message
                    Text(
                        text = when (level) {
                            1 -> "Great start! Earn 200 XP to level up into a Snakelet Wizard! 🐍"
                            2 -> "Wow, you're learning fast! 300 more XP to unlock Spellslinger! ⚡"
                            3 -> "Magic is flowing! 400 more XP to become Mage of Code! 🔮"
                            4 -> "Almost there! 500 more XP to achieve Python Archmage! 👑"
                            else -> "Incredible! You are a Python Archmage! Keep casting code spells! 👑⭐"
                        },
                        fontSize = 12.sp,
                        color = TextLight,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 16.sp
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

        // --- Curated Bookshelf Card ---
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEBF5FF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToBooks() }
                    .testTag("bookshelf_button")
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
                            .background(Color(0xFF3B82F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Bookshelf",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Interactive Library 📚",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "Read open-source books & test concepts instantly in the Sandbox!",
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
