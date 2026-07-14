package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.data.QuestData
import com.example.ui.MainViewModel
import com.example.ui.theme.*

data class ExplorerAvatar(
    val id: String,
    val name: String,
    val emoji: String,
    val description: String,
    val startColor: Color,
    val endColor: Color,
    val glowColor: Color
)

val AvatarsList = listOf(
    ExplorerAvatar(
        id = "classic",
        name = "Classic Monty 🧙‍♂️",
        emoji = "🐍",
        description = "A wise young python wizard with a starry violet hat.",
        startColor = Color(0xFF8B5CF6),
        endColor = Color(0xFF6366F1),
        glowColor = Color(0xFFA78BFA)
    ),
    ExplorerAvatar(
        id = "sparky",
        name = "Sparky Lightning ⚡",
        emoji = "🐍",
        description = "Runs at hyper-speed powered by electricity!",
        startColor = Color(0xFFF59E0B),
        endColor = Color(0xFFEF4444),
        glowColor = Color(0xFFFCD34D)
    ),
    ExplorerAvatar(
        id = "glow",
        name = "Neon Sorcerer 🌀",
        emoji = "🐍",
        description = "Glows with celestial indigo starlight.",
        startColor = Color(0xFFEC4899),
        endColor = Color(0xFF8B5CF6),
        glowColor = Color(0xFFF472B6)
    ),
    ExplorerAvatar(
        id = "cosmic",
        name = "Cosmic Archmage 👑",
        emoji = "🐍",
        description = "The ultimate star-traveler of the coding galaxy.",
        startColor = Color(0xFF10B981),
        endColor = Color(0xFF3B82F6),
        glowColor = Color(0xFF6EE7B7)
    )
)

data class PythonBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val badgeBgStart: Color,
    val badgeBgEnd: Color,
    val checkCondition: (xp: Int, streak: Int, completedCount: Int) -> Boolean,
    val lockRequirementText: String
)

val PythonBadgesList = listOf(
    PythonBadge(
        id = "hatchling",
        title = "Hatchling Badge 🥚",
        description = "You took your first magical steps in Python Quest!",
        iconEmoji = "🐣",
        badgeBgStart = Color(0xFF4ADE80),
        badgeBgEnd = Color(0xFF22C55E),
        checkCondition = { _, _, _ -> true },
        lockRequirementText = ""
    ),
    PythonBadge(
        id = "sandbox",
        title = "Sandbox Maestro 🎈",
        description = "Unlocked by experimenting inside the Free Coding Sandbox!",
        iconEmoji = "🧪",
        badgeBgStart = Color(0xFFFBBF24),
        badgeBgEnd = Color(0xFFF59E0B),
        checkCondition = { xp, _, _ -> xp >= 15 },
        lockRequirementText = "Run any code in Sandbox (+15 XP)"
    ),
    PythonBadge(
        id = "first_spell",
        title = "First Spell 🔮",
        description = "Casted your first successfully compiled program!",
        iconEmoji = "✨",
        badgeBgStart = Color(0xFF818CF8),
        badgeBgEnd = Color(0xFF6366F1),
        checkCondition = { _, _, completedCount -> completedCount >= 1 },
        lockRequirementText = "Complete 1 Quest (+100 XP)"
    ),
    PythonBadge(
        id = "streak_warrior",
        title = "Streak Wizard ⭐",
        description = "Kept your magical learning streak burning for 3+ days!",
        iconEmoji = "🔥",
        badgeBgStart = Color(0xFFF87171),
        badgeBgEnd = Color(0xFFEF4444),
        checkCondition = { _, streak, _ -> streak >= 3 },
        lockRequirementText = "Reach a 3-Day Streak"
    ),
    PythonBadge(
        id = "spellslinger",
        title = "Spellslinger Elite ⚡",
        description = "Accumulated over 500 magical Python XP points!",
        iconEmoji = "⚡",
        badgeBgStart = Color(0xFFA78BFA),
        badgeBgEnd = Color(0xFF8B5CF6),
        checkCondition = { xp, _, _ -> xp >= 500 },
        lockRequirementText = "Earn 500 Total XP"
    ),
    PythonBadge(
        id = "archmage",
        title = "Grand Archmage 👑",
        description = "The ultimate honor! Complete 4+ quests to join the Council.",
        iconEmoji = "👑",
        badgeBgStart = Color(0xFFF472B6),
        badgeBgEnd = Color(0xFFEC4899),
        checkCondition = { _, _, completedCount -> completedCount >= 4 },
        lockRequirementText = "Complete 4 Quests"
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PythonExplorerScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val xpPoints by viewModel.xpPoints.collectAsState()
    val streakCount by viewModel.streakCount.collectAsState()
    val selectedAvatarId by viewModel.selectedAvatar.collectAsState()
    val completedQuests by viewModel.completedQuestIds.collectAsState()
    
    val currentAvatar = AvatarsList.firstOrNull { it.id == selectedAvatarId } ?: AvatarsList[0]
    val completedCount = completedQuests.size
    
    // Level Calculations
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

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("profile_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go Back",
                            tint = TextDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "PYTHON EXPLORER PROFILE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = KidsPurple
                        )
                        Text(
                            text = "Wizard Registry 🌟",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextDark
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(KidsBg)
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            
            // --- AVATAR VIEW CARD ---
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(2.dp, KidsBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Big circular avatar preview with matching dynamic gradient background
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(currentAvatar.startColor, currentAvatar.endColor)))
                            .border(4.dp, Color.White, CircleShape),
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
                                // Add subtle color tint filter to customize companion look dynamically
                                ColorFilter.tint(currentAvatar.glowColor.copy(alpha = 0.35f), BlendMode.SrcAtop)
                            } else null
                        )
                        
                        // Floating Badge/Emoji indicator for custom avatar type
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(2.dp, currentAvatar.startColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (currentAvatar.id) {
                                    "classic" -> "🔮"
                                    "sparky" -> "⚡"
                                    "glow" -> "🌀"
                                    else -> "👑"
                                },
                                fontSize = 18.sp
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentAvatar.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentAvatar.description,
                            fontSize = 13.sp,
                            color = TextLight,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }

                    // --- CHOOSE YOUR COMPANION AVATAR ---
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Summon Your Code Companion 🐍",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AvatarsList.forEach { avatar ->
                                val isSelected = avatar.id == selectedAvatarId
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.updateSelectedAvatar(avatar.id) },
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) avatar.startColor else KidsBorder
                                    ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) avatar.glowColor.copy(alpha = 0.15f) else Color.White
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.linearGradient(
                                                        listOf(avatar.startColor, avatar.endColor)
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("🐍", fontSize = 20.sp)
                                        }
                                        Text(
                                            text = avatar.name.split(" ")[0], // Just show first name
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextDark,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- PROGRESS & XP LEVEL TRACKER CARD ---
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(2.dp, KidsBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Python XP Magical Reservoir ✨",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Text(
                                text = levelTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = KidsOrange
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(KidsOrange.copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "LVL $level",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = KidsOrange
                            )
                        }
                    }

                    // Progress bar
                    LinearProgressIndicator(
                        progress = { levelProgress.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(18.dp)
                            .clip(CircleShape),
                        color = KidsOrange,
                        trackColor = KidsBorder
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Current: $xpPoints XP",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                        Text(
                            text = if (level < 5) "Next Level: $maxXp XP" else "Max Level achieved! 👑",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                    }

                    if (level < 5) {
                        Text(
                            text = "🔮 Earn ${maxXp - xpPoints} more XP to level up into a stronger Python Wizard!",
                            fontSize = 12.sp,
                            color = KidsPurple,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // --- STREAK CALENDAR & BOOSTER CARD ---
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(2.dp, KidsBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Magical Streak Flame 🔥",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Text(
                                text = "Learn daily to keep the fire lit!",
                                fontSize = 13.sp,
                                color = TextLight
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFF2E6))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = "🔥", fontSize = 18.sp)
                            Text(
                                text = "$streakCount Days",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = KidsOrange
                            )
                        }
                    }

                    // Habit Tracker Calendar representation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                        days.forEachIndexed { index, day ->
                            // Simulate checked days. Current active day will match streak
                            val isCompletedDay = index < (streakCount % 8) || streakCount >= 5
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = day,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextLight
                                )
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isCompletedDay) KidsOrange else KidsBorder.copy(alpha = 0.4f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompletedDay) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Day active",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Text(text = "💤", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Interactive Streak Booster button
                    Button(
                        onClick = { viewModel.incrementStreak() },
                        colors = ButtonDefaults.buttonColors(containerColor = KidsPurple),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("boost_streak_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Star icon",
                                tint = Color.White
                            )
                            Text(
                                text = "Simulate Daily Quest (+50 XP!) 🚀",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // --- BADGES UNLOCKED REGISTRY ---
            Text(
                text = "My Unlocked Badges 🏆",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )

            // Custom flow/column layout for badge items (as grid inside Column can have scrolling conflicts)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                PythonBadgesList.forEach { badge ->
                    val isUnlocked = badge.checkCondition(xpPoints, streakCount, completedCount)

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUnlocked) Color.White else Color(0xFFF3F1F8)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (isUnlocked) KidsBorder else KidsBorder.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Badge Icon with color gradient when unlocked
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isUnlocked) {
                                            Brush.linearGradient(
                                                listOf(badge.badgeBgStart, badge.badgeBgEnd)
                                            )
                                        } else {
                                            Brush.linearGradient(
                                                listOf(Color(0xFFD1D5DB), Color(0xFF9CA3AF))
                                            )
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isUnlocked) {
                                    Text(text = badge.iconEmoji, fontSize = 32.sp)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked Badge",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = badge.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isUnlocked) TextDark else TextLight
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isUnlocked) badge.description else "Unlocks when: ${badge.lockRequirementText}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isUnlocked) TextDark.copy(alpha = 0.8f) else TextLight.copy(alpha = 0.7f),
                                    lineHeight = 16.sp
                                )
                            }

                            // Tiny check/lock visual indicator
                            if (isUnlocked) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(KidsGreen.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Unlocked badge symbol",
                                        tint = KidsGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Safe space at bottom
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
