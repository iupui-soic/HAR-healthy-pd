package com.example.plhi_har.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


//@Composable
//fun NavGraph(
//    navController: NavHostController,
//    currentActivity: String,
//    viewModel: ActivityLogViewModel
//) {
//    NavHost(navController = navController, startDestination = "main_screen") {
//        composable("main_screen") {
//            MainScreen(navController, currentActivity, viewModel)
//        }
//        composable("logs_screen") {
//            LogsScreen(viewModel)
//        }
//    }
//}


@Composable
fun NavGraph(
    navController: NavHostController,
    currentActivity: String,
    currentConfidence: Float,
    viewModel: ActivityLogViewModel
) {
    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            MainScreen(
                navController = navController,
                currentActivity = currentActivity,
                currentConfidence = currentConfidence,
                viewModel = viewModel
            )
        }
        composable("logs_screen") {
            LogsScreen(viewModel = viewModel)
        }
    }
}
//
//@Composable
//fun LogsScreen(viewModel: ActivityLogViewModel) {
//    val loggedActivities by viewModel.allLogs.observeAsState(emptyList())
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        Text(
//            text = "Activity Logs",
//            style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        LoggedActivitiesList(loggedActivities)
//
//        ShareButton(viewModel)
//    }
//}

@Composable
fun LoggedActivitiesList(loggedActivities: List<ActivityLog>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(loggedActivities) { activityLog ->
            Text(
                text = activityLog.activityName,
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}
