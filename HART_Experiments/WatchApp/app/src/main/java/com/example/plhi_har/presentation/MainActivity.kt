package com.example.plhi_har.presentation

import android.Manifest
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.plhi_har.presentation.theme.PLHI_HARTheme
import com.example.plhi_har.processor.SensorDataProcessor
import com.example.plhi_har.upload.GoogleDriveService
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.drive.DriveScopes
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import androidx.work.WorkManager
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.Constraints
import androidx.work.WorkRequest


class MainActivity : ComponentActivity(), SensorEventListener {

    companion object {
        private const val BODY_SENSORS_PERMISSION_CODE = 100
        private const val REQUEST_ACCOUNT_PICKER = 1001
    }

    private lateinit var viewModel: ActivityLogViewModel
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private val activityState = mutableStateOf("Initializing...")
    private val confidenceState = mutableStateOf(0f)
    private lateinit var sensorDataProcessor: SensorDataProcessor
    private lateinit var credential: GoogleAccountCredential

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
//        setContentView(R.layout.activity_main)

        // Initialize Google Account Credential
        credential = GoogleAccountCredential.usingOAuth2(this, listOf(DriveScopes.DRIVE_FILE))
        requestAccountSelection()

        // Initialize ViewModel with AndroidViewModelFactory using Application context
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(ActivityLogViewModel::class.java)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        setContent {
            PLHI_HARTheme {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    currentActivity = activityState.value,
                    currentConfidence = confidenceState.value,
                    viewModel = viewModel
                )
            }
        }

        checkSensorPermissions()
    }

    // Method to request the user to select a Google account
    private fun requestAccountSelection() {
        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER)
    }

    // Check and request sensor permissions if necessary
    private fun checkSensorPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BODY_SENSORS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BODY_SENSORS),
                BODY_SENSORS_PERMISSION_CODE
            )
        } else {
            setupSensors()
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == BODY_SENSORS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupSensors()
                Toast.makeText(this, "Sensor permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Sensor permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSensors() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val sensorDelay = SensorManager.SENSOR_DELAY_GAME

        sensorDataProcessor = SensorDataProcessor(
            context = this,
            updateActivityState = { activity, confidence, sensorData ->
                runOnUiThread {
                    activityState.value = activity
                    confidenceState.value = confidence
                    Log.d(
                        "ActivityRecognition",
                        "New activity detected: $activity, Confidence: $confidence"
                    )
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.insert(
                            ActivityLog(
                                activityName = activity,
                                timestamp = System.currentTimeMillis(),
                                sensorData = sensorData,
                                confidence = confidence
                            )
                        )
                    }
                }
            }
        )

        // Register listeners if sensors are available
        accelerometer?.let { sensor ->
            sensorManager.registerListener(this, sensor, sensorDelay)
        }
        gyroscope?.let { sensor ->
            sensorManager.registerListener(this, sensor, sensorDelay)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            lifecycleScope.launch(Dispatchers.Default) {
                sensorDataProcessor.processSensorData(it.values, it.sensor.type)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("SensorAccuracy", "Sensor: ${sensor?.name}, New accuracy: $accuracy")
    }

    override fun onResume() {
        super.onResume()
        // Re-register sensors on resume if permissions are granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BODY_SENSORS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            accelerometer?.let { sensor ->
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
            }
            gyroscope?.let { sensor ->
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ACCOUNT_PICKER && resultCode == RESULT_OK && data != null && data.extras != null) {
            val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            if (accountName != null) {
                // Set the selected account
                credential.selectedAccountName = accountName
                // Pass the credential to the GoogleDriveService
                GoogleDriveService.credential = credential
                // Now you can use GoogleDriveService to upload files
                scheduleLogUpload()
            }
        }
    }

    // Function to schedule log upload
    private fun scheduleLogUpload() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(true)
            .build()

        val uploadRequest = PeriodicWorkRequestBuilder<UploadWorker>(30, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(uploadRequest)
    }
}
