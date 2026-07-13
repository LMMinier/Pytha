package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.MainViewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LessonScreen
import com.example.ui.screens.PlaygroundScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val mainViewModel: MainViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(
                            viewModel = mainViewModel,
                            onNavigateToQuest = { navController.navigate("quest") },
                            onNavigateToPlayground = { navController.navigate("playground") }
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
                }
            }
        }
    }
}
