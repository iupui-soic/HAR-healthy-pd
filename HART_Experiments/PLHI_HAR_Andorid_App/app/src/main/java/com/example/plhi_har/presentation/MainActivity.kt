////package com.example.plhi_har.presentation
////
////import android.content.Context
////import android.content.res.AssetFileDescriptor
////import android.hardware.Sensor
////import android.hardware.SensorEvent
////import android.hardware.SensorEventListener
////import android.hardware.SensorManager
////import android.os.Bundle
////import androidx.activity.ComponentActivity
////import androidx.activity.compose.setContent
////import androidx.activity.viewModels
////import androidx.compose.foundation.background
////import androidx.compose.foundation.layout.*
////import androidx.compose.material3.MaterialTheme
////import androidx.compose.material3.Text
////import androidx.compose.runtime.*
////import androidx.compose.ui.Alignment
////import androidx.compose.ui.Modifier
////import androidx.compose.ui.text.TextStyle
////import androidx.compose.ui.text.font.FontWeight
////import androidx.compose.ui.text.style.TextAlign
////import androidx.compose.ui.unit.dp
////import androidx.compose.ui.unit.sp
////import com.example.plhi_har.presentation.theme.PLHI_HARTheme
////import kotlinx.coroutines.delay
////import org.tensorflow.lite.Interpreter
////import java.io.FileInputStream
////import java.io.IOException
////import java.nio.ByteBuffer
////import java.nio.MappedByteBuffer
////import java.nio.channels.FileChannel
////
////class MainActivity : ComponentActivity(), SensorEventListener {
////    private lateinit var sensorManager: SensorManager
////    private var accelerometer: Sensor? = null
////    private var gyroscope: Sensor? = null
////    private val activityState = mutableStateOf("Initializing...")
////    private lateinit var sensorDataProcessor: SensorDataProcessor
////    private val activityLogViewModel: ActivityLogViewModel by viewModels {
////        ActivityLogViewModelFactory(this)
////    }
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setTheme(android.R.style.Theme_DeviceDefault)
////        setContent {
////            WearApp(activityState.value)
////        }
////
////        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
////        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
////        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
////        val sensorDelay = SensorManager.SENSOR_DELAY_GAME
////
////        sensorDataProcessor = SensorDataProcessor(
////            context = this,
////            updateActivityState = { activity ->
////                runOnUiThread { activityState.value = activity }
////            }
////        )
////
////        sensorManager.registerListener(this, accelerometer, sensorDelay)
////        sensorManager.registerListener(this, gyroscope, sensorDelay)
////
////        try {
////            val model = loadModelFile("model/mobilehart_model.tflite")
////            sensorDataProcessor.tfliteInterpreter = Interpreter(model)
////        } catch (e: IOException) {
////            e.printStackTrace()
////        }
////    }
////
////    @Throws(IOException::class)
////    private fun loadModelFile(modelPath: String): MappedByteBuffer {
////        val fileDescriptor: AssetFileDescriptor = assets.openFd(modelPath)
////        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
////        val fileChannel = inputStream.channel
////        val startOffset = fileDescriptor.startOffset
////        val declaredLength = fileDescriptor.declaredLength
////        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
////    }
////
////    override fun onSensorChanged(event: SensorEvent?) {
////        event?.let {
////            sensorDataProcessor.addToBuffer(it.values)
////        }
////    }
////
////    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
////        // Handle sensor accuracy changes here
////    }
////
////    override fun onDestroy() {
////        super.onDestroy()
////        sensorManager.unregisterListener(this)
////        sensorDataProcessor.tfliteInterpreter?.close()
////    }
////}
////
////@Composable
////fun WearApp(activity: String) {
////    PLHI_HARTheme {
////        Box(
////            modifier = Modifier
////                .fillMaxSize()
////                .background(MaterialTheme.colorScheme.background),
////            contentAlignment = Alignment.Center
////        ) {
////            Column(
////                horizontalAlignment = Alignment.CenterHorizontally,
////                verticalArrangement = Arrangement.Center
////            ) {
////                ActivityDisplay(activity)
////                TimeDisplay()
////            }
////        }
////    }
////}
////
////@Composable
////fun ActivityDisplay(activity: String) {
////    Text(
////        text = activity,
////        style = TextStyle(
////            fontWeight = FontWeight.Bold,
////            fontSize = 30.sp,
////            letterSpacing = 0.15.sp
////        ),
////        textAlign = TextAlign.Center,
////        color = MaterialTheme.colorScheme.onSurface
////    )
////}
////
////@Composable
////fun TimeDisplay() {
////    var elapsedTime by remember { mutableStateOf(0L) }
////
////    LaunchedEffect(Unit) {
////        while (true) {
////            delay(1000L)
////            elapsedTime += 1
////        }
////    }
////
////    Text(
////        text = formatElapsedTime(elapsedTime),
////        style = TextStyle(
////            fontWeight = FontWeight.Normal,
////            fontSize = 25.sp,
////            letterSpacing = 0.1.sp
////        ),
////        modifier = Modifier
////            .background(MaterialTheme.colorScheme.background)
////            .padding(8.dp)
////    )
////}
////
////private fun formatElapsedTime(seconds: Long): String {
////    val hours = (seconds / 3600).toInt()
////    val minutes = ((seconds % 3600) / 60).toInt()
////    val remainingSeconds = (seconds % 60).toInt()
////    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
////}
//
//
//package com.example.plhi_har.presentation
//
//import android.content.Context
//import android.content.res.AssetFileDescriptor
//import android.hardware.Sensor
//import android.hardware.SensorEvent
//import android.hardware.SensorEventListener
//import android.hardware.SensorManager
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.viewModels
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.compose.rememberNavController
//import com.example.plhi_har.presentation.theme.PLHI_HARTheme
//import kotlinx.coroutines.delay
//import org.tensorflow.lite.Interpreter
//import java.io.FileInputStream
//import java.io.IOException
//import java.nio.MappedByteBuffer
//import java.nio.channels.FileChannel
//
////class MainActivity : ComponentActivity(), SensorEventListener {
////    private lateinit var sensorManager: SensorManager
////    private var accelerometer: Sensor? = null
////    private var gyroscope: Sensor? = null
////    private val activityState = mutableStateOf("Initializing...")
////    private lateinit var sensorDataProcessor: SensorDataProcessor
////    private lateinit var activityLogViewModel: ActivityLogViewModel
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setTheme(android.R.style.Theme_DeviceDefault)
////        activityLogViewModel = ActivityLogViewModel(AppDatabase.getInstance(this).activityLogDao())
////
////        setContent {
////            PLHI_HARTheme {
////                MainScreen(activityState.value, activityLogViewModel)
////            }
////        }
////
////        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
////        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
////        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
////        val sensorDelay = SensorManager.SENSOR_DELAY_GAME
////
////        sensorDataProcessor = SensorDataProcessor(
////            context = this,
////            updateActivityState = { activity ->
////                runOnUiThread { activityState.value = activity }
////            }
////        )
////
////        sensorManager.registerListener(this, accelerometer, sensorDelay)
////        sensorManager.registerListener(this, gyroscope, sensorDelay)
////
////        try {
////            val model = loadModelFile("model/mobilehart_model.tflite")
////            sensorDataProcessor.tfliteInterpreter = Interpreter(model)
////        } catch (e: IOException) {
////            e.printStackTrace()
////        }
////    }
////
////    @Throws(IOException::class)
////    private fun loadModelFile(modelPath: String): MappedByteBuffer {
////        val fileDescriptor: AssetFileDescriptor = assets.openFd(modelPath)
////        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
////        val fileChannel = inputStream.channel
////        val startOffset = fileDescriptor.startOffset
////        val declaredLength = fileDescriptor.declaredLength
////        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
////    }
////
////    override fun onSensorChanged(event: SensorEvent?) {
////        event?.let {
////            sensorDataProcessor.addToBuffer(it.values)
////        }
////    }
////
////    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
////        // Handle sensor accuracy changes here
////    }
////
////    override fun onDestroy() {
////        super.onDestroy()
////        sensorManager.unregisterListener(this)
////        sensorDataProcessor.tfliteInterpreter?.close()
////    }
////}
////
////@Composable
////fun MainScreen(currentActivity: String, viewModel: ActivityLogViewModel) {
////    val loggedActivities by viewModel.allLogs.observeAsState(emptyList())
////
////    Column(
////        modifier = Modifier
////            .fillMaxSize()
////            .background(MaterialTheme.colorScheme.background),
////        horizontalAlignment = Alignment.CenterHorizontally,
////        verticalArrangement = Arrangement.Center
////    ) {
////        ActivityDisplay(currentActivity)
////        Spacer(modifier = Modifier.height(16.dp))
////        TimeDisplay()
////        Spacer(modifier = Modifier.height(16.dp))
////        LoggedActivitiesList(loggedActivities)
////    }
////}
////
////@Composable
////fun ActivityDisplay(activity: String) {
////    Text(
////        text = activity,
////        style = TextStyle(
////            fontWeight = FontWeight.Bold,
////            fontSize = 30.sp,
////            letterSpacing = 0.15.sp
////        ),
////        textAlign = TextAlign.Center,
////        color = MaterialTheme.colorScheme.onSurface
////    )
////}
////
////@Composable
////fun TimeDisplay() {
////    var elapsedTime by remember { mutableStateOf(0L) }
////
////    LaunchedEffect(Unit) {
////        while (true) {
////            delay(1000L)
////            elapsedTime += 1
////        }
////    }
////
////    Text(
////        text = formatElapsedTime(elapsedTime),
////        style = TextStyle(
////            fontWeight = FontWeight.Normal,
////            fontSize = 25.sp,
////            letterSpacing = 0.1.sp
////        ),
////        modifier = Modifier
////            .background(MaterialTheme.colorScheme.background)
////            .padding(8.dp)
////    )
////}
////
////@Composable
////fun LoggedActivitiesList(loggedActivities: List<ActivityLog>) {
////    LazyColumn(
////        modifier = Modifier
////            .fillMaxWidth()
////            .padding(16.dp)
////    ) {
////        items(loggedActivities) { activityLog ->
////            Text(
////                text = activityLog.activityName,
////                style = TextStyle(
////                    fontSize = 20.sp,
////                    textAlign = TextAlign.Center,
////                    color = MaterialTheme.colorScheme.onSurface
////                ),
////                modifier = Modifier
////                    .fillMaxWidth()
////                    .padding(vertical = 8.dp)
////            )
////        }
////    }
////}
////
////private fun formatElapsedTime(seconds: Long): String {
////    val hours = (seconds / 3600).toInt()
////    val minutes = ((seconds % 3600) / 60).toInt()
////    val remainingSeconds = (seconds % 60).toInt()
////    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
////}
//
//
////class MainActivity : ComponentActivity(), SensorEventListener {
////    private lateinit var sensorManager: SensorManager
////    private var accelerometer: Sensor? = null
////    private var gyroscope: Sensor? = null
////    private val activityState = mutableStateOf("Initializing...")
////    private lateinit var sensorDataProcessor: SensorDataProcessor
////    private lateinit var activityLogViewModel: ActivityLogViewModel
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setTheme(android.R.style.Theme_DeviceDefault)
////
////        // Initialize the database
////        val database = DatabaseClient.getDatabase(this)
////
////        activityLogViewModel = ActivityLogViewModel(database.activityLogDao())
////
////        setContent {
////            PLHI_HARTheme {
////                MainScreen(activityState.value, activityLogViewModel)
////            }
////        }
////
////        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
////        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
////        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
////        val sensorDelay = SensorManager.SENSOR_DELAY_GAME
////
////        sensorDataProcessor = SensorDataProcessor(
////            context = this,
////            updateActivityState = { activity ->
////                runOnUiThread { activityState.value = activity }
////            }
////        )
////
////        sensorManager.registerListener(this, accelerometer, sensorDelay)
////        sensorManager.registerListener(this, gyroscope, sensorDelay)
////
////        try {
////            val model = loadModelFile("model/mobilehart_model.tflite")
////            sensorDataProcessor.tfliteInterpreter = Interpreter(model)
////        } catch (e: IOException) {
////            e.printStackTrace()
////        }
////    }
////
////    @Throws(IOException::class)
////    private fun loadModelFile(modelPath: String): MappedByteBuffer {
////        val fileDescriptor: AssetFileDescriptor = assets.openFd(modelPath)
////        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
////        val fileChannel = inputStream.channel
////        val startOffset = fileDescriptor.startOffset
////        val declaredLength = fileDescriptor.declaredLength
////        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
////    }
////
////    override fun onSensorChanged(event: SensorEvent?) {
////        event?.let {
////            sensorDataProcessor.addToBuffer(it.values)
////        }
////    }
////
////    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
////        // Handle sensor accuracy changes here
////    }
////
////    override fun onDestroy() {
////        super.onDestroy()
////        sensorManager.unregisterListener(this)
////        sensorDataProcessor.tfliteInterpreter?.close()
////    }
////}
//
//class MainActivity : ComponentActivity(), SensorEventListener {
//    private lateinit var sensorManager: SensorManager
//    private var accelerometer: Sensor? = null
//    private var gyroscope: Sensor? = null
//    private val activityState = mutableStateOf("Initializing...")
//    private lateinit var sensorDataProcessor: SensorDataProcessor
//    private lateinit var activityLogViewModel: ActivityLogViewModel
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setTheme(android.R.style.Theme_DeviceDefault)
//
//        // Initialize the database
//        val database = AppDatabase.getInstance(this)
//
//        activityLogViewModel = ActivityLogViewModel(database.activityLogDao())
//
//        setContent {
//            PLHI_HARTheme {
//                val navController = rememberNavController()
//                NavGraph(navController = navController)
//            }
//        }
//
//        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
//        val sensorDelay = SensorManager.SENSOR_DELAY_GAME
//
//        sensorDataProcessor = SensorDataProcessor(
//            context = this,
//            updateActivityState = { activity ->
//                runOnUiThread { activityState.value = activity }
//            }
//        )
//
//        sensorManager.registerListener(this, accelerometer, sensorDelay)
//        sensorManager.registerListener(this, gyroscope, sensorDelay)
//
//        try {
//            val model = loadModelFile("model/mobilehart_model.tflite")
//            sensorDataProcessor.tfliteInterpreter = Interpreter(model)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }
//
//    @Throws(IOException::class)
//    private fun loadModelFile(modelPath: String): MappedByteBuffer {
//        val fileDescriptor: AssetFileDescriptor = assets.openFd(modelPath)
//        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
//        val fileChannel = inputStream.channel
//        val startOffset = fileDescriptor.startOffset
//        val declaredLength = fileDescriptor.declaredLength
//        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
//    }
//
//    override fun onSensorChanged(event: SensorEvent?) {
//        event?.let {
//            sensorDataProcessor.addToBuffer(it.values)
//        }
//    }
//
//    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//        // Handle sensor accuracy changes here
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        sensorManager.unregisterListener(this)
//        sensorDataProcessor.tfliteInterpreter?.close()
//    }
//}
//
////@Composable
////fun MainScreen(currentActivity: String, viewModel: ActivityLogViewModel) {
////    val loggedActivities by viewModel.allLogs.observeAsState(emptyList())
////
////    Column(
////        modifier = Modifier
////            .fillMaxSize()
////            .background(MaterialTheme.colorScheme.background),
////        horizontalAlignment = Alignment.CenterHorizontally,
////        verticalArrangement = Arrangement.Center
////    ) {
////        ActivityDisplay(currentActivity)
////        Spacer(modifier = Modifier.height(16.dp))
////        TimeDisplay()
////        Spacer(modifier = Modifier.height(16.dp))
////        LoggedActivitiesList(loggedActivities)
////    }
////}
////
////@Composable
////fun ActivityDisplay(activity: String) {
////    Text(
////        text = activity,
////        style = TextStyle(
////            fontWeight = FontWeight.Bold,
////            fontSize = 30.sp,
////            letterSpacing = 0.15.sp
////        ),
////        textAlign = TextAlign.Center,
////        color = MaterialTheme.colorScheme.onSurface
////    )
////}
//
//@Composable
//fun MainScreen(navController: NavHostController, currentActivity: String, viewModel: ActivityLogViewModel) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        ActivityDisplay(currentActivity)
//        Spacer(modifier = Modifier.height(16.dp))
//        TimeDisplay()
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(onClick = { navController.navigate("logs_screen") }) {
//            Text(text = "View Logs")
//        }
//    }
//}
//
//@Composable
//fun ActivityDisplay(activity: String) {
//    Text(
//        text = activity,
//        style = TextStyle(
//            fontWeight = FontWeight.Bold,
//            fontSize = 30.sp,
//            letterSpacing = 0.15.sp
//        ),
//        textAlign = TextAlign.Center,
//        color = MaterialTheme.colorScheme.onSurface
//    )
//}
//
//@Composable
//fun LogsScreen(viewModel: ActivityLogViewModel = viewModel()) {
//    val loggedActivities by viewModel.allLogs.observeAsState(emptyList())
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Top
//    ) {
//        Text(
//            text = "Logged Activities",
//            style = TextStyle(
//                fontWeight = FontWeight.Bold,
//                fontSize = 24.sp,
//                textAlign = TextAlign.Center,
//                color = MaterialTheme.colorScheme.onSurface
//            ),
//            modifier = Modifier.padding(16.dp)
//        )
//        LoggedActivitiesList(loggedActivities)
//    }
//}
//
//@Composable
//fun LoggedActivitiesList(loggedActivities: List<ActivityLog>) {
//    LazyColumn(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//    ) {
//        items(loggedActivities) { activityLog ->
//            Text(
//                text = activityLog.activityName,
//                style = TextStyle(
//                    fontSize = 20.sp,
//                    textAlign = TextAlign.Center,
//                    color = MaterialTheme.colorScheme.onSurface
//                ),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 8.dp)
//            )
//        }
//    }
//}
//
//@Composable
//fun TimeDisplay() {
//    var elapsedTime by remember { mutableStateOf(0L) }
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            delay(1000L)
//            elapsedTime += 1
//        }
//    }
//
//    Text(
//        text = formatElapsedTime(elapsedTime),
//        style = TextStyle(
//            fontWeight = FontWeight.Normal,
//            fontSize = 25.sp,
//            letterSpacing = 0.1.sp
//        ),
//        modifier = Modifier
//            .background(MaterialTheme.colorScheme.background)
//            .padding(8.dp)
//    )
//}
//
////@Composable
////fun LoggedActivitiesList(loggedActivities: List<ActivityLog>) {
////    LazyColumn(
////        modifier = Modifier
////            .fillMaxWidth()
////            .padding(16.dp)
////    ) {
////        items(loggedActivities) { activityLog ->
////            Text(
////                text = activityLog.activityName,
////                style = TextStyle(
////                    fontSize = 20.sp,
////                    textAlign = TextAlign.Center,
////                    color = MaterialTheme.colorScheme.onSurface
////                ),
////                modifier = Modifier
////                    .fillMaxWidth()
////                    .padding(vertical = 8.dp)
////            )
////        }
////    }
////}
//
//private fun formatElapsedTime(seconds: Long): String {
//    val hours = (seconds / 3600).toInt()
//    val minutes = ((seconds % 3600) / 60).toInt()
//    val remainingSeconds = (seconds % 60).toInt()
//    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
//}


package com.example.plhi_har.presentation

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.plhi_har.presentation.theme.PLHI_HARTheme
import kotlinx.coroutines.delay
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import androidx.navigation.NavHostController
import androidx.compose.runtime.livedata.observeAsState


class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private val activityState = mutableStateOf("Initializing...")
    private lateinit var sensorDataProcessor: SensorDataProcessor
    private lateinit var activityLogViewModel: ActivityLogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        val database = AppDatabase.getInstance(this)

        activityLogViewModel = ActivityLogViewModel(database.activityLogDao())

        setContent {
            PLHI_HARTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController, currentActivity = activityState.value, viewModel = activityLogViewModel)
            }
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val sensorDelay = SensorManager.SENSOR_DELAY_GAME

        sensorDataProcessor = SensorDataProcessor(
            context = this,
            updateActivityState = { activity ->
                runOnUiThread { activityState.value = activity }
            }
        )

        sensorManager.registerListener(this, accelerometer, sensorDelay)
        sensorManager.registerListener(this, gyroscope, sensorDelay)

        try {
            val model = loadModelFile("model/mobilehart_model.tflite")
            sensorDataProcessor.tfliteInterpreter = Interpreter(model)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun loadModelFile(modelPath: String): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            sensorDataProcessor.addToBuffer(it.values)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle sensor accuracy changes here
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        sensorDataProcessor.tfliteInterpreter?.close()
    }
}

@Composable
fun MainScreen(navController: NavHostController, currentActivity: String, viewModel: ActivityLogViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ActivityDisplay(currentActivity)
        Spacer(modifier = Modifier.height(16.dp))
        TimeDisplay()
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("logs_screen") }) {
            Text(text = "View Logs")
        }
    }
}

@Composable
fun ActivityDisplay(activity: String) {
    Text(
        text = activity,
        style = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            letterSpacing = 0.15.sp
        ),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun LogsScreen(viewModel: ActivityLogViewModel = viewModel()) {
    val loggedActivities by viewModel.allLogs.observeAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Logged Activities",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.padding(16.dp)
        )
        LoggedActivitiesList(loggedActivities)
    }
}

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
                style = TextStyle(
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun TimeDisplay() {
    var elapsedTime by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            elapsedTime += 1
        }
    }

    Text(
        text = formatElapsedTime(elapsedTime),
        style = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 25.sp,
            letterSpacing = 0.1.sp
        ),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    )
}

private fun formatElapsedTime(seconds: Long): String {
    val hours = (seconds / 3600).toInt()
    val minutes = ((seconds % 3600) / 60).toInt()
    val remainingSeconds = (seconds % 60).toInt()
    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}
