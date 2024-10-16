package com.example.plhi_har.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay


@Composable
fun MainScreen(
    navController: NavHostController,
    currentActivity: String,
    currentConfidence: Float,
    viewModel: ActivityLogViewModel
) {
    var elapsedTime by remember { mutableStateOf(0L) }

    // Timer functionality
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            elapsedTime += 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Current Activity: $currentActivity",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Confidence: ${String.format("%.2f", currentConfidence)}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = formatElapsedTime(elapsedTime),
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { navController.navigate("logs_screen") },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(text = "View Logs")
        }

        ShareButton(viewModel)
    }
}

// Function to format elapsed time
private fun formatElapsedTime(seconds: Long): String {
    val hours = (seconds / 3600).toInt()
    val minutes = ((seconds % 3600) / 60).toInt()
    val remainingSeconds = (seconds % 60).toInt()
    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}


