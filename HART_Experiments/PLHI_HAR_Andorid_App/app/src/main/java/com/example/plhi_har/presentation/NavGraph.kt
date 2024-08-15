package com.example.plhi_har.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavGraph(
    navController: NavHostController,
    currentActivity: String,
    viewModel: ActivityLogViewModel
) {
    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            MainScreen(navController, currentActivity, viewModel)
        }
        composable("logs_screen") {
            LogsScreen(viewModel)
        }
    }
}
