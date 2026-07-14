package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.ui.MainViewModel
import com.example.ui.screens.AgeSelectionScreen
import com.example.ui.screens.BooksScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LessonScreen
import com.example.ui.screens.PlaygroundScreen
import com.example.ui.screens.ConfettiOverlay
import com.example.ui.screens.PythonExplorerScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val mainViewModel: MainViewModel = viewModel()
                val selectedAgeTier by mainViewModel.selectedAgeTier.collectAsState()
                val xpEarnedEvent by mainViewModel.xpEarnedEvent.collectAsState()
                val confettiTrigger by mainViewModel.confettiTrigger.collectAsState()

                Box(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = if (selectedAgeTier == null) "age_selection" else "home"
                    ) {
                        composable("age_selection") {
                            AgeSelectionScreen(
                                viewModel = mainViewModel,
                                onOnboardingComplete = {
                                    navController.navigate("home") {
                                        popUpTo("age_selection") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("home") {
                            HomeScreen(
                                viewModel = mainViewModel,
                                onNavigateToQuest = { navController.navigate("quest") },
                                onNavigateToPlayground = { navController.navigate("playground") },
                                onNavigateToBooks = { navController.navigate("books") },
                                onNavigateToAgeSelection = { navController.navigate("age_selection") },
                                onNavigateToProfile = { navController.navigate("profile") }
                            )
                        }
                        composable("quest") {
                            LessonScreen(
                                viewModel = mainViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNextQuest = { nextQuest ->
                                    mainViewModel.selectQuest(nextQuest)
                                }
                            )
                        }
                        composable("playground") {
                            PlaygroundScreen(
                                viewModel = mainViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("books") {
                            BooksScreen(
                                viewModel = mainViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToSandbox = { navController.navigate("playground") }
                            )
                        }
                        composable("profile") {
                            PythonExplorerScreen(
                                viewModel = mainViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }

                    xpEarnedEvent?.let { event ->
                        XpEarnedToast(
                            event = event,
                            onDismiss = { mainViewModel.dismissXpEvent() }
                        )
                    }

                    ConfettiOverlay(
                        trigger = confettiTrigger,
                        onFinished = { mainViewModel.dismissConfetti() }
                    )
                }
            }
        }
    }
}

@Composable
fun XpEarnedToast(
    event: com.example.ui.XpEvent,
    onDismiss: () -> Unit
) {
    LaunchedEffect(event.id) {
        delay(3200)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 20.dp, end = 20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    2.dp,
                    Brush.linearGradient(listOf(Color(0xFFFCD34D), Color(0xFFF59E0B))),
                    RoundedCornerShape(24.dp)
                )
                .clickable { onDismiss() }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF8B5CF6), // KidsPurple
                                Color(0xFFEC4899)  // Vibrant Pink/Orange
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✨", fontSize = 28.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CORE COMPILATION COMPLETE! ⚡",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFDE047)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = event.message,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "🎉",
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}
