// GoogleDriveService.kt
package com.example.plhi_har.upload

import android.content.Context
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory

object GoogleDriveService {

    private var driveService: Drive? = null
    var credential: GoogleAccountCredential? = null // Make the credential accessible

    fun getDriveService(context: Context): Drive {
        if (driveService == null) {
            // Ensure credential is initialized and has an account selected
            if (credential == null || credential?.selectedAccount == null) {
                throw IllegalStateException("Google Account not selected. Please select an account before using the Drive service.")
            }

            driveService = Drive.Builder(
                NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                credential
            ).setApplicationName("PLHI HAR").build()
        }
        return driveService!!
    }
}
