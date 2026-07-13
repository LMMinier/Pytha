package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlaygroundScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val code by viewModel.playgroundCode.collectAsState()
    val result by viewModel.interpreterResult.collectAsState()
    val montyAdvice by viewModel.montyAdvice.collectAsState()
    val isMontyLoading by viewModel.isMontyLoading.collectAsState()
    
    var showMontyDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Interactive script shortcut presets
    val presets = listOf(
        PresetScript("🎈 Balloon Rain", "# Rain of balloons!\nfor i in range(5):\n    print(\"🎈\")"),
        PresetScript("🍕 Pizza Party", "# Pizza and burgers!\nprint(\"🍕\")\nprint(\"🍔\")\nprint(\"🍟\")"),
        PresetScript("🚀 Rocket Launch", "# Fly to the stars!\nship = \"🚀\"\nprint(\"Heading to... \")\nfor i in range(3):\n    print(\"⭐\")\nprint(ship)"),
        PresetScript("🦁 Zoo Day", "# Spawn wild animals!\nprint(\"🦁\")\nprint(\"🐯\")\nprint(\"🐻\")\nprint(\"🐼\")")
    )

    // Standard helper blocks for speedy coding on mobile
    val helperBlocks = listOf(
        "print(", "\"🦄\"", "\"🦖\"", "=", "for i in range(", "if", "else:", "toy", ")", "  "
    )

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
                            text = "Interactive Sandbox",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = KidsOrange
                        )
                        Text(
                            text = "Monty's Coding Workshop 🎈",
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
                // --- Preset templates row ---
                Text(
                    text = "Load Magic Templates:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    presets.forEach { preset ->
                        AssistChip(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.updatePlaygroundCode(preset.code)
                                viewModel.resetResult()
                            },
                            label = { Text(preset.name, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color.White,
                                labelColor = TextDark
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, KidsBorder)
                        )
                    }
                }

                // --- Sandbox Editor Text area ---
                Text(
                    text = "Type or Build Your Code:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(2.dp, KidsBorder, RoundedCornerShape(24.dp))
                        .background(Color(0xFF282535))
                        .padding(16.dp)
                ) {
                    BasicTextField(
                        value = code,
                        onValueChange = { viewModel.updatePlaygroundCode(it) },
                        textStyle = TextStyle(
                            color = Color(0xFFC9C4E0),
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 24.sp
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("sandbox_code_editor"),
                        decorationBox = { innerTextField ->
                            if (code.isEmpty()) {
                                Text(
                                    text = "# Code anything here...",
                                    color = Color.Gray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 16.sp
                                )
                            }
                            innerTextField()
                        }
                    )

                    // Clear button
                    IconButton(
                        onClick = { viewModel.updatePlaygroundCode("") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Clear",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // --- Helper coding keys ---
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    helperBlocks.forEach { key ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEDE9FE))
                                .clickable {
                                    focusManager.clearFocus()
                                    viewModel.appendPlaygroundBlock(key)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = key,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = KidsPurple
                            )
                        }
                    }
                }

                // --- Action buttons ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // RUN button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.runPlaygroundCode()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KidsGreen),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(56.dp)
                            .testTag("sandbox_run_button")
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

                    // ASK MONTY AI help
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.askMontyForHelp(isPlayground = true)
                            showMontyDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KidsPurple),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("sandbox_ask_monty")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Help",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ask Monty", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // --- Visual Playground Meadow ---
                Text(
                    text = "Your Meadow Output:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                VisualMeadow(result = result)
            }

            // --- Monty Dialogue ---
            if (showMontyDialog) {
                AlertDialog(
                    onDismissRequest = { showMontyDialog = false },
                    confirmButton = {
                        TextButton(onClick = { showMontyDialog = false }) {
                            Text("Awesome!", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KidsPurple)
                        }
                    },
                    title = {
                        Text("🐍 Monty's Code Review", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = KidsPurple)
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
                                Text("Monty is reading your amazing script... 🐍✨", fontSize = 14.sp, color = TextLight)
                            } else {
                                Text(
                                    text = montyAdvice ?: "Nothing found here. Monty wants to see more magic!",
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

data class PresetScript(val name: String, val code: String)
