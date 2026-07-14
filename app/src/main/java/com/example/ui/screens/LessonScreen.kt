package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.Quest
import com.example.data.QuestData
import com.example.interpreter.InterpreterResult
import com.example.interpreter.PythonOutput
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.graphicsLayer

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LessonScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNextQuest: (Quest) -> Unit,
    modifier: Modifier = Modifier
) {
    val quest by viewModel.currentQuest.collectAsState()
    val code by viewModel.editorCode.collectAsState()
    val result by viewModel.interpreterResult.collectAsState()
    val completedQuests by viewModel.completedQuestIds.collectAsState()
    
    val montyAdvice by viewModel.montyAdvice.collectAsState()
    val isMontyLoading by viewModel.isMontyLoading.collectAsState()
    
    var showMontyDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val currentQuest = quest ?: return

    // Auto open completed dialog when quest gets cleared
    val isCurrentQuestCompleted = completedQuests.contains(currentQuest.id)

    Scaffold(
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
                        modifier = Modifier.testTag("back_button")
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
                            text = currentQuest.levelName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = KidsPurple
                        )
                        Text(
                            text = currentQuest.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(KidsBg)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- Story Dialog bubble ---
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "🐍", fontSize = 48.sp)
                        Column {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFF3EEFF))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = currentQuest.story,
                                    fontSize = 14.sp,
                                    color = TextDark,
                                    lineHeight = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "🎯 Goal: " + currentQuest.instruction,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = KidsOrange
                            )
                        }
                    }
                }

                // --- Editor Workspace ---
                Text(
                    text = "Write Your Python Code:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(2.dp, KidsBorder, RoundedCornerShape(24.dp))
                        .background(Color(0xFF282535))
                        .padding(16.dp)
                ) {
                    BasicTextField(
                        value = code,
                        onValueChange = { viewModel.updateCode(it) },
                        textStyle = TextStyle(
                            color = Color(0xFFC9C4E0),
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 24.sp
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("code_editor"),
                        decorationBox = { innerTextField ->
                            if (code.isEmpty()) {
                                Text(
                                    text = "# Tap blocks below to build code...",
                                    color = Color.Gray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 16.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    
                    // Reset Button inside editor
                    IconButton(
                        onClick = { viewModel.updateCode(currentQuest.startingCode) },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // --- Tap Puzzle Blocks ---
                Text(
                    text = "Puzzle Blocks (Tap to write!):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    currentQuest.blocks.forEach { block ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(KidsPurple.copy(alpha = 0.85f), KidsPurple)
                                    )
                                )
                                .clickable {
                                    focusManager.clearFocus()
                                    viewModel.appendCodeBlock(block)
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = block,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // --- Control Buttons Row ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // RUN button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.runCurrentQuestCode()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KidsGreen),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(56.dp)
                            .testTag("run_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Run",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RUN SCRIPT!", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    // ASK MONTY button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.askMontyForHelp(isPlayground = false)
                            showMontyDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KidsPurple),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("ask_monty_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ask Monty", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // --- Visual Execution Output Meadow ---
                Text(
                    text = "The Meadow Output:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                VisualMeadow(result = result)

                // --- Concept Unlocked Card (Success!) ---
                AnimatedVisibility(
                    visible = isCurrentQuestCompleted && result?.success == true,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        border = borderStroke(2.dp, KidsGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🎉 QUEST CLEARED! 🎉", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = KidsGreen)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = currentQuest.conceptTitle,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentQuest.conceptExplanation,
                                fontSize = 13.sp,
                                color = TextDark.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            val nextQuestIndex = QuestData.quests.indexOf(currentQuest) + 1
                            if (nextQuestIndex < QuestData.quests.size) {
                                val nextQuest = QuestData.quests[nextQuestIndex]
                                Button(
                                    onClick = { onNextQuest(nextQuest) },
                                    colors = ButtonDefaults.buttonColors(containerColor = KidsPurple),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("Next Quest!", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Text(
                                    text = "🌟 You have mastered all of Monty's Python Quests! Continue experimenting in the Coding Sandbox!",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KidsPurple,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // --- Monty AI Advice Overlay Dialog ---
            if (showMontyDialog) {
                AlertDialog(
                    onDismissRequest = { showMontyDialog = false },
                    confirmButton = {
                        TextButton(onClick = { showMontyDialog = false }) {
                            Text("Acknowledge", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KidsPurple)
                        }
                    },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_cyber_daemon),
                                contentDescription = "Cyber Daemon Avatar",
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, KidsPurple, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Text("System Daemon Advice...", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = KidsPurple)
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (isMontyLoading) {
                                CircularProgressIndicator(color = KidsPurple)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Monty is thinking with his magic snake brain...", fontSize = 14.sp, color = TextLight, textAlign = TextAlign.Center)
                            } else {
                                Text(
                                    text = montyAdvice ?: "Hmm, Monty seems a bit shy today. Try asking again!",
                                    fontSize = 14.sp,
                                    color = TextDark,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    containerColor = Color.White
                )
            }
        }
    }
}

private fun borderStroke(width: androidx.compose.ui.unit.Dp, color: Color) = 
    androidx.compose.foundation.BorderStroke(width, color)

@Composable
fun VisualMeadow(result: InterpreterResult?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFE3F2FD), KidsMint) // Sky to Meadow grass
                )
            )
            .border(2.dp, KidsBorder, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (result == null) {
            // Idle State
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "💤", fontSize = 32.sp)
                Text(
                    text = "The Meadow is quiet. Run your script!",
                    fontSize = 13.sp,
                    color = TextLight,
                    fontWeight = FontWeight.Bold
                )
            }
        } else if (!result.success) {
            // Error State
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🦖🔍", fontSize = 36.sp)
                Text(
                    text = "Oh no! Let's double check the code!",
                    fontWeight = FontWeight.Bold,
                    color = KidsOrange,
                    fontSize = 14.sp
                )
                Text(
                    text = result.errorMessage ?: "Check your characters!",
                    fontSize = 12.sp,
                    color = TextDark,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        } else {
            // Success State: Render items!
            val spawnedEmojis = result.outputs.filterIsInstance<PythonOutput.SpawnEmoji>()
            val textOutputs = result.outputs.filterIsInstance<PythonOutput.Text>()

            Box(modifier = Modifier.fillMaxSize()) {
                // Background sun
                Text(
                    text = "☀️",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                )

                if (spawnedEmojis.isEmpty() && textOutputs.isEmpty()) {
                    Text(
                        text = "Script ran, but nothing printed!",
                        color = TextLight,
                        fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    // Display speech bubbles for texts
                    if (textOutputs.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = textOutputs.first().text,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }
                    }

                    // Render Emojis with floating animation!
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Display spawned emojis
                        spawnedEmojis.forEachIndexed { i, spawn ->
                            AnimatedFloatingEmoji(emoji = spawn.emoji, delayMs = i * 150)
                        }
                        
                        // Default wizard if no animal spawned but text printed
                        if (spawnedEmojis.isEmpty() && textOutputs.isNotEmpty()) {
                            AnimatedFloatingEmoji(emoji = "🧙‍♂️", delayMs = 0)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedFloatingEmoji(emoji: String, delayMs: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    
    // Jump bounce animation
    val bounceY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -30f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing, delayMillis = delayMs),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    // Gentle rotate
    val rotateAngle by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotate"
    )

    Box(
        modifier = Modifier
            .offset(y = bounceY.dp)
            .graphicsLayer {
                rotationZ = rotateAngle
            }
            .padding(horizontal = 8.dp)
    ) {
        Text(text = emoji, fontSize = 48.sp)
    }
}
