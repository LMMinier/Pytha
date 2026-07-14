package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun AgeSelectionScreen(
    viewModel: MainViewModel,
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTier by viewModel.selectedAgeTier.collectAsState()

    val options = listOf(
        AgeTierOption("8_10", "Cyberpunk Novice 💻", "Root Shell Access", "No prior coding experience. Learn Python via high-level visual analogies and fundamental terminal logic."),
        AgeTierOption("11_13", "Scripting Apprentice ⚙️", "Automation Specialist", "Focus on practical automation: how variables, loops, and scripts can automate your daily workflows."),
        AgeTierOption("14_17", "AI Apprentice 🧠", "Neural Net Developer", "Explore code logic, branches, and lists with an eye toward AI inputs, prompt chains, and datasets."),
        AgeTierOption("18_plus", "Mainframe Architect 💡", "System Engineer", "Dive deep into clean programming workflows, structured backend concepts, and clean coding practices.")
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(KidsBg)
            .systemBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 40.dp, bottom = 100.dp)
        ) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_cyber_onboarding),
                        contentDescription = "Onboarding Cyber Core",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .border(2.dp, KidsBorder, RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Python Quest: Cyber Terminal",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Initialize Access Protocol: Select your specialization track",
                        fontSize = 15.sp,
                        color = TextLight,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            options.forEach { option ->
                item {
                    val isSelected = currentTier == option.id
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFF3F0FC) else Color.White
                        ),
                        border = if (isSelected) {
                            androidx.compose.foundation.BorderStroke(3.dp, KidsPurple)
                        } else {
                            androidx.compose.foundation.BorderStroke(1.dp, KidsBorder)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectAgeTier(option.id)
                            }
                            .testTag("age_tier_card_${option.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = option.title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) KidsPurple else TextDark
                                )
                                Text(
                                    text = option.subtitle,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KidsOrange
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = option.description,
                                    fontSize = 13.sp,
                                    color = TextLight
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "🔒 Privacy First: In compliance with COPPA/GDPR, we do not collect or upload any age or personal data. This preference is stored safely on your device.",
                    fontSize = 11.sp,
                    color = TextLight.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }

        // Action continue button locked at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, KidsBg, KidsBg)
                    )
                )
                .padding(24.dp)
        ) {
            Button(
                onClick = onOnboardingComplete,
                enabled = currentTier != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = KidsPurple,
                    disabledContainerColor = KidsBorder
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("onboarding_continue_button")
            ) {
                Text(
                    text = if (currentTier != null) "INITIALIZE CORE TERMINAL 🔓" else "Select Access Specialization",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

data class AgeTierOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String
)
