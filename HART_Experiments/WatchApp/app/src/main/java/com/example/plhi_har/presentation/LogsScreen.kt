package com.example.plhi_har.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LogsScreen(viewModel: ActivityLogViewModel = viewModel()) {
    // Directly observe LiveData using observeAsState
    val logs = viewModel.allLogs.observeAsState(initial = emptyList()).value // Correct observation of LiveData

    LazyColumn {
        items(logs) { log ->
            LogItem(log)
        }
    }
}

@Composable
fun LogItem(log: ActivityLog) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Activity: ${log.activityName}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Confidence: ${String.format("%.2f", log.confidence)}",
            style = MaterialTheme.typography.bodyMedium
        )
//        Text(
//            text = "Timestamp: ${formatTimestamp(log.timestamp)}",
//            style = MaterialTheme.typography.bodySmall
//        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
