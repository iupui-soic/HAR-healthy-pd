// UploadWorker.kt
package com.example.plhi_har.presentation

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.util.Log
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.client.http.InputStreamContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import com.example.plhi_har.presentation.UploadWorker
import com.example.plhi_har.upload.GoogleDriveService



class UploadWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Initialize Google Drive Service
            val driveService = GoogleDriveService.getDriveService(context)

            // Get logs and sensor data from the database or file location
            val logFile = java.io.File(context.filesDir, "logs.txt") // Change this to your actual file path
            if (!logFile.exists()) {
                Log.e("UploadWorker", "Log file does not exist")
                return@withContext Result.failure()
            }

            // Create a metadata file for Google Drive
            val gDriveFile = File().apply {
                name = "logs_${System.currentTimeMillis()}.txt"
                parents = listOf("appDataFolder") // Or a specific folder ID if needed
            }

            // Upload the file
            FileInputStream(logFile).use { inputStream ->
                driveService.files().create(gDriveFile, InputStreamContent("text/plain", inputStream))
                    .setFields("id")
                    .execute()
            }

            Log.d("UploadWorker", "File uploaded successfully")
            Result.success()

        } catch (e: Exception) {
            Log.e("UploadWorker", "Error uploading file", e)
            Result.failure()
        }
    }
}
