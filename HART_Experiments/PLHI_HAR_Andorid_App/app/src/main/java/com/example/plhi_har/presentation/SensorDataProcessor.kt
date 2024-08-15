//package com.example.plhi_har.presentation
//
//import android.content.Context
//import android.util.Log
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import org.tensorflow.lite.Interpreter
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//
//class SensorDataProcessor(
//    private val context: Context,
//    private val updateActivityState: (String) -> Unit,
//    private val windowSize: Int = 128
//) {
//    private val dataBuffer = mutableListOf<FloatArray>()
//    var tfliteInterpreter: Interpreter? = null
//
//    fun addToBuffer(values: FloatArray) {
//        if (dataBuffer.size < windowSize) {
//            dataBuffer.add(values)
//        } else {
//            val window = preprocessData(dataBuffer)
//            performInference(window)
//            dataBuffer.clear()
//        }
//    }
//
//    private fun preprocessData(buffer: List<FloatArray>): FloatArray {
//        val expectedSize = 128 * 6
//        val flatData = buffer.flatMap { it.toList() }
//        return if (flatData.size >= expectedSize) {
//            flatData.subList(0, expectedSize).toFloatArray()
//        } else {
//            FloatArray(expectedSize) { index -> if (index < flatData.size) flatData[index] else 0f }
//        }
//    }
//
//    private fun performInference(inputData: FloatArray) {
//        // The model expects 3072 bytes (128 * 6 floats)
//        val inputBufferSize = 128 * 6 * 4
//        val inputBuffer = ByteBuffer.allocateDirect(inputBufferSize).order(ByteOrder.nativeOrder())
//        inputData.forEach { inputBuffer.putFloat(it) }
//        inputBuffer.rewind()
//
//        val output = Array(1) { FloatArray(10) } // The model produces an output of shape [1, 10]
//        Log.d("Inference", "Input buffer size: ${inputBuffer.capacity()}")
//        Log.d("Inference", "Output buffer size: ${output.size * output[0].size * 4}")
//        tfliteInterpreter?.run(inputBuffer, output)
//
//        handleOutput(output)
//    }
//
//    private fun handleOutput(output: Array<FloatArray>) {
//        val activity = decodeActivity(output[0])
//        updateActivityState(activity)
//        logActivity(activity)
//    }
//
//    private fun decodeActivity(outputArray: FloatArray): String {
//        val maxIndex = outputArray.indexOfMax()
//        return activityNames.getOrElse(maxIndex) { "Unknown Activity" }
//    }
//
//    private fun FloatArray.indexOfMax(): Int {
//        var maxIndex = 0
//        var maxValue = Float.NEGATIVE_INFINITY
//        for (i in indices) {
//            if (this[i] > maxValue) {
//                maxValue = this[i]
//                maxIndex = i
//            }
//        }
//        return maxIndex
//    }
//
//    private fun logActivity(activityName: String) {
//        val activityLog = ActivityLog(activityName = activityName)
//        val db = AppDatabase.getDatabase(context)
//        CoroutineScope(Dispatchers.IO).launch {
//            db.activityLogDao().insertLog(activityLog)
//        }
//    }
//
//    fun getLoggedActivities(): Flow<List<ActivityLog>> {
//        val db = AppDatabase.getDatabase(context)
//        return db.activityLogDao().getAllLogs()
//    }
//
//    companion object {
//        private val activityNames = listOf(
//            "Cycling",
//            "Jogging",
//            "Jump Rope",
//            "Push-up",
//            "Run",
//            "Sit-up",
//            "Table Tennis",
//            "Walk",
//            "Walking Downstairs",
//            "Walking Upstairs"
//        )
//    }
//}

//package com.example.plhi_har.presentation
//
//import android.content.Context
//import android.util.Log
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.launch
//import org.tensorflow.lite.Interpreter
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//
//class SensorDataProcessor(
//    private val context: Context,
//    private val updateActivityState: (String) -> Unit,
//    private val windowSize: Int = 128
//) {
//    private val dataBuffer = mutableListOf<FloatArray>()
//    var tfliteInterpreter: Interpreter? = null
//
//    fun addToBuffer(values: FloatArray) {
//        if (dataBuffer.size < windowSize) {
//            dataBuffer.add(values)
//        } else {
//            val window = preprocessData(dataBuffer)
//            performInference(window)
//            dataBuffer.clear()
//        }
//    }
//
//    private fun preprocessData(buffer: List<FloatArray>): FloatArray {
//        val expectedSize = 128 * 6
//        val flatData = buffer.flatMap { it.toList() }
//        return if (flatData.size >= expectedSize) {
//            flatData.subList(0, expectedSize).toFloatArray()
//        } else {
//            FloatArray(expectedSize) { index -> if (index < flatData.size) flatData[index] else 0f }
//        }
//    }
//
//    private fun performInference(inputData: FloatArray) {
//        // The model expects 3072 bytes (128 * 6 floats)
//        val inputBufferSize = 128 * 6 * 4
//        val inputBuffer = ByteBuffer.allocateDirect(inputBufferSize).order(ByteOrder.nativeOrder())
//        inputData.forEach { inputBuffer.putFloat(it) }
//        inputBuffer.rewind()
//
//        val output = Array(1) { FloatArray(10) } // The model produces an output of shape [1, 10]
//        Log.d("Inference", "Input buffer size: ${inputBuffer.capacity()}")
//        Log.d("Inference", "Output buffer size: ${output.size * output[0].size * 4}")
//        tfliteInterpreter?.run(inputBuffer, output)
//
//        handleOutput(output)
//    }
//
//    private fun handleOutput(output: Array<FloatArray>) {
//        val activity = decodeActivity(output[0])
//        updateActivityState(activity)
//        logActivity(activity)
//    }
//
//    private fun decodeActivity(outputArray: FloatArray): String {
//        val maxIndex = outputArray.indexOfMax()
//        return activityNames.getOrElse(maxIndex) { "Unknown Activity" }
//    }
//
//    private fun FloatArray.indexOfMax(): Int {
//        var maxIndex = 0
//        var maxValue = Float.NEGATIVE_INFINITY
//        for (i in indices) {
//            if (this[i] > maxValue) {
//                maxValue = this[i]
//                maxIndex = i
//            }
//        }
//        return maxIndex
//    }
//
//    private fun logActivity(activityName: String) {
//        val activityLog = ActivityLog(activityName = activityName)
//        val db = AppDatabase.getInstance(context)
//        CoroutineScope(Dispatchers.IO).launch {
//            db.activityLogDao().insertLog(activityLog)
//        }
//    }
//
//    fun getLoggedActivities(): Flow<List<ActivityLog>> {
//        val db = AppDatabase.getInstance(context)
//        return db.activityLogDao().getAllLogs()
//    }
//
//    companion object {
//        private val activityNames = listOf(
//            "Cycling",
//            "Jogging",
//            "Jump Rope",
//            "Push-up",
//            "Run",
//            "Sit-up",
//            "Table Tennis",
//            "Walk",
//            "Walking Downstairs",
//            "Walking Upstairs"
//        )
//    }
//}
//
//

package com.example.plhi_har.presentation

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class SensorDataProcessor(
    private val context: Context,
    private val updateActivityState: (String) -> Unit,
    private val windowSize: Int = 128
) {
    private val dataBuffer = mutableListOf<FloatArray>()
    var tfliteInterpreter: Interpreter? = null

    fun addToBuffer(values: FloatArray) {
        if (dataBuffer.size < windowSize) {
            dataBuffer.add(values)
        } else {
            val window = preprocessData(dataBuffer)
            performInference(window)
            dataBuffer.clear()
        }
    }

    private fun preprocessData(buffer: List<FloatArray>): FloatArray {
        val expectedSize = 128 * 6
        val flatData = buffer.flatMap { it.toList() }
        return if (flatData.size >= expectedSize) {
            flatData.subList(0, expectedSize).toFloatArray()
        } else {
            FloatArray(expectedSize) { index -> if (index < flatData.size) flatData[index] else 0f }
        }
    }

    private fun performInference(inputData: FloatArray) {
        val inputBufferSize = 128 * 6 * 4
        val inputBuffer = ByteBuffer.allocateDirect(inputBufferSize).order(ByteOrder.nativeOrder())
        inputData.forEach { inputBuffer.putFloat(it) }
        inputBuffer.rewind()

        val output = Array(1) { FloatArray(10) }
        Log.d("Inference", "Input buffer size: ${inputBuffer.capacity()}")
        Log.d("Inference", "Output buffer size: ${output.size * output[0].size * 4}")
        tfliteInterpreter?.run(inputBuffer, output)

        handleOutput(output)
    }

    private fun handleOutput(output: Array<FloatArray>) {
        val activity = decodeActivity(output[0])
        updateActivityState(activity)
        logActivity(activity)
    }

    private fun decodeActivity(outputArray: FloatArray): String {
        val maxIndex = outputArray.indexOfMax()
        return activityNames.getOrElse(maxIndex) { "Unknown Activity" }
    }

    private fun FloatArray.indexOfMax(): Int {
        var maxIndex = 0
        var maxValue = Float.NEGATIVE_INFINITY
        for (i in indices) {
            if (this[i] > maxValue) {
                maxValue = this[i]
                maxIndex = i
            }
        }
        return maxIndex
    }

    private fun logActivity(activityName: String) {
        val activityLog = ActivityLog(activityName = activityName, timestamp = System.currentTimeMillis())
        val db = AppDatabase.getInstance(context)
        CoroutineScope(Dispatchers.IO).launch {
            db.activityLogDao().insert(activityLog) // Ensure this matches your DAO method name!
        }
    }

    fun getLoggedActivities(): Flow<List<ActivityLog>> {
        val db = AppDatabase.getInstance(context)
        return db.activityLogDao().getAllLogs()
    }

    companion object {
        private val activityNames = listOf(
            "Cycling",
            "Jogging",
            "Jump Rope",
            "Push-up",
            "Run",
            "Sit-up",
            "Table Tennis",
            "Walk",
            "Walking Downstairs",
            "Walking Upstairs"
        )
    }
}
