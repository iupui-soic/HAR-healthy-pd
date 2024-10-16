package com.example.plhi_har.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ShareButton(viewModel: ActivityLogViewModel) {
    val context = LocalContext.current

    Button(onClick = { shareLogsFile(context, viewModel) }) {
        Text("Share Logs")
    }
}

private fun shareLogsFile(context: Context, viewModel: ActivityLogViewModel) {
    CoroutineScope(Dispatchers.Main).launch {
        try {
            // Export logs to a JSON file
            val file = viewModel.exportLogsToJson(context)

            // Get URI for the file using FileProvider
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider", // Ensure your authority matches the one in your manifest
                file
            )

            // Create an intent to share the file
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Launch the sharing chooser
            context.startActivity(Intent.createChooser(shareIntent, "Share Logs"))
        } catch (e: Exception) {
            // Handle any errors
            e.printStackTrace()
        }
    }
}
