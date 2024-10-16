//package com.example.plhi_har.processor
//
//import android.content.Context
//import android.hardware.Sensor
//import android.util.Log
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import org.tensorflow.lite.Interpreter
//import org.tensorflow.lite.support.common.FileUtil
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//import kotlin.math.exp
//
//class SensorDataProcessor(
//    private val context: Context,
//    private val updateActivityState: (String, Float, String) -> Unit
//) {
//    private val windowSize: Int = 128 // Window size for inference
//    private val stepSize = 32 // Step size for sliding window effect
//    private val numFeatures = 6 // Number of features (accelerometer + gyroscope)
//    private val dataBuffer = mutableListOf<FloatArray>()
//    private var currentAccelData: FloatArray? = null
//    private var currentGyroData: FloatArray? = null
//    private var tfliteInterpreter: Interpreter? = null
//
//    init {
//        initTflite()
//    }
//
//    // Initialize TensorFlow Lite interpreter
//    private fun initTflite() {
//        try {
//            val tfliteModel = FileUtil.loadMappedFile(context, "mobilehart_model.tflite")
//            val tfliteOptions = Interpreter.Options()
//            tfliteOptions.setUseXNNPACK(true)  // Disable XNNPACK delegate
//            tfliteInterpreter = Interpreter(tfliteModel, tfliteOptions)
//            Log.d("SensorDataProcessor", "TFLite model loaded successfully")
//        } catch (e: Exception) {
//            Log.e("SensorDataProcessor", "Error initializing TFLite", e)
//            tfliteInterpreter = null // Ensure null if initialization fails
//        }
//    }
//
//    // Process incoming sensor data
//    suspend fun processSensorData(values: FloatArray, sensorType: Int) = withContext(Dispatchers.Default) {
//        try {
//            // Update sensor data based on the type
//            when (sensorType) {
//                Sensor.TYPE_ACCELEROMETER -> currentAccelData = normalize(values)
//                Sensor.TYPE_GYROSCOPE -> currentGyroData = normalize(values)
//            }
//
//            // Check if both accelerometer and gyroscope data are ready
//            if (currentAccelData == null || currentGyroData == null) {
//                Log.e("SensorProcessor", "Sensor data is incomplete: Accel = $currentAccelData, Gyro = $currentGyroData")
//                return@withContext
//            }
//
//            // Combine accelerometer and gyroscope data
//            val combinedData = FloatArray(numFeatures).apply {
//                System.arraycopy(currentAccelData!!, 0, this, 0, 3)
//                System.arraycopy(currentGyroData!!, 0, this, 3, 3)
//            }
//
//            // Add combined data to the buffer
//            dataBuffer.add(combinedData)
//            Log.d("SensorProcessor", "Combined data added: ${combinedData.contentToString()}")
//            Log.d("SensorProcessor", "Current buffer size: ${dataBuffer.size}")
//
//            // Ensure buffer size is sufficient for creating a window
//            if (dataBuffer.size < windowSize) {
//                Log.e("SensorProcessor", "Data buffer size (${dataBuffer.size}) is insufficient for creating a window.")
//                return@withContext
//            }
//
//            if (dataBuffer.size > maxBufferSize) {
//                dataBuffer.removeAt(0)  // Limit the buffer size to avoid excessive memory consumption
//            }
//
//
//            // Create a window of data for inference
//            val window = createWindow()
//            Log.d("SensorProcessor", "Window created for inference: ${window.contentDeepToString()}")
//
//            // Validate the window shape before performing inference
//            if (window.isEmpty() || window[0].isEmpty() || window[0].size != numFeatures) {
//                Log.e("SensorProcessor", "Window creation failed or incorrect shape: [${window.size}, ${if (window.isNotEmpty()) window[0].size else 0}]")
//                return@withContext
//            }
//
//            performInference(window)
//
//            // Maintain sliding window by removing old data
//            repeat(stepSize) {
//                if (dataBuffer.isNotEmpty()) {
//                    dataBuffer.removeAt(0)
//                }
//            }
//        } catch (e: Exception) {
//            Log.e("SensorProcessor", "Unexpected error processing sensor data", e)
//        }
//    }
//
//    // Create a window of data from the buffer
//    private fun createWindow(): Array<FloatArray> {
//        return try {
//            // Always take the latest `windowSize` samples for the window
//            val startIndex = maxOf(0, dataBuffer.size - windowSize)
//            val window = dataBuffer.subList(startIndex, dataBuffer.size).toTypedArray()
//
//            if (window.size < windowSize) {
//                Log.e("SensorProcessor", "Window size insufficient (${window.size} < $windowSize), padding with zeros.")
//                // Padding with zeros if necessary
//                val paddedWindow = Array(windowSize) { FloatArray(numFeatures) }
//                for (i in window.indices) {
//                    paddedWindow[i] = window[i]
//                }
//                paddedWindow
//            } else {
//                window
//            }
//        } catch (e: Exception) {
//            Log.e("SensorProcessor", "Error creating window", e)
//            emptyArray()
//        }
//    }
//
//    // Perform inference using the TensorFlow Lite model
//    private fun performInference(window: Array<FloatArray>) {
//        // Ensure the TensorFlow Lite interpreter is initialized
//        if (tfliteInterpreter == null) {
//            Log.e("SensorProcessor", "TFLite interpreter is not initialized")
//            return
//        }
//
//        val expectedShape = intArrayOf(1, windowSize, numFeatures) // Expected input shape
//        val inputBuffer = ByteBuffer.allocateDirect(4 * expectedShape[1] * expectedShape[2]).order(ByteOrder.nativeOrder())
//
//        try {
//            // Fill input buffer with data and log each step
//            for (row in window) {
//                for (value in row) {
//                    inputBuffer.putFloat(value)
//                }
//            }
//            Log.d("SensorProcessor", "Input buffer ready for inference: ${inputBuffer.array().joinToString()}")
//
//            val outputBuffer = ByteBuffer.allocateDirect(4 * 11).order(ByteOrder.nativeOrder()) // Adjust size for 10 output classes
//            tfliteInterpreter?.run(inputBuffer, outputBuffer)
//            outputBuffer.rewind()
//
//            val output = FloatArray(11) // Adjust size for expected number of classes
//            outputBuffer.asFloatBuffer().get(output)
//
//            // Log raw output values to ensure inference was performed
//            Log.d("SensorProcessor", "Raw inference output: ${output.joinToString()}")
//
//            if (output.any { it.isNaN() }) {
//                Log.e("SensorProcessor", "Inference output contains NaN values: ${output.contentToString()}")
//                return
//            }
//
//            handleOutput(output, window)
//        } catch (e: Exception) {
//            Log.e("SensorProcessor", "Unexpected error during inference", e)
//        }
//    }
//
//    // Normalize sensor data to range [-1, 1]
//    private fun normalize(data: FloatArray): FloatArray {
//        val minVal = -1.0f // Adjust according to your expected input range
//        val maxVal = 1.0f  // Adjust according to your expected input range
//
//        val min = data.minOrNull() ?: 0.0f
//        val max = data.maxOrNull() ?: 1.0f
//
//        val normalizedData = data.map {
//            val scaledValue = if (max - min != 0f) (it - min) / (max - min) else 0f
//            minVal + scaledValue * (maxVal - minVal)
//        }.toFloatArray()
//
//        Log.d("SensorProcessor", "Normalized data: ${normalizedData.contentToString()}")
//        return normalizedData
//    }
//
//    // Handle the model's output after inference
//    private fun handleOutput(output: FloatArray, window: Array<FloatArray>) {
//        try {
//            val (activity, confidence) = decodeActivity(output)
//            Log.d("SensorProcessor", "Decoded activity: $activity, Confidence: $confidence")
//            val sensorDataString = createSensorDataString(window)
//            updateActivityState(activity, confidence, sensorDataString)
//        } catch (e: Exception) {
//            Log.e("SensorProcessor", "Error handling output", e)
//        }
//    }
//
//    // Decode the activity from the model output using softmax
//    private fun decodeActivity(outputArray: FloatArray): Pair<String, Float> {
//        val softmaxOutput = softmax(outputArray)
//        val maxIndex = softmaxOutput.indexOfMax()
//        val maxValue = softmaxOutput[maxIndex]
//
//        val activity = activityNames.getOrElse(maxIndex) { "Unknown Activity" }
//        return Pair(activity, maxValue)
//    }
//
//    // Softmax function to normalize output probabilities
//    private fun softmax(input: FloatArray): FloatArray {
//        val max = input.maxOrNull() ?: 0f
//        val exp = input.map { exp(it - max) }
//        val sum = exp.sum()
//        return exp.map { (it / sum).toFloat() }.toFloatArray()
//    }
//
//    // Create a string representation of the sensor data window
//    private fun createSensorDataString(window: Array<FloatArray>): String {
//        return window.joinToString(separator = "\n") { it.joinToString(separator = ",") }
//    }
//
//    // Find the index of the maximum value in an array
//    private fun FloatArray.indexOfMax(): Int = this.indices.maxByOrNull { this[it] } ?: -1
//
//    companion object {
//        private const val ACTIVITY_THRESHOLD = 0.2f
//        private val activityNames = listOf(
//            "Cycling",
//            "Jogging",
//            "Jump Rope",
//            "Push-up",
//            "Run",
//            "Sit-up",
//            "Table Tennis",
//            "Undetected",
//            "Walk",
//            "Walking Downstairs",
//            "Walking Upstairs"
//        )
//    }
//
//}

package com.example.plhi_har.processor

import android.content.Context
import android.hardware.Sensor
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.exp

class SensorDataProcessor(
    private val context: Context,
    private val updateActivityState: (String, Float, String) -> Unit
) {
    private val windowSize: Int = 128 // Window size for inference
    private val stepSize = 32 // Step size for sliding window effect
    private val numFeatures = 6 // Number of features (accelerometer + gyroscope)
    private val maxBufferSize: Int = 256  // Adjust buffer size as per your requirement
    private val dataBuffer = mutableListOf<FloatArray>()
    private var currentAccelData: FloatArray? = null
    private var currentGyroData: FloatArray? = null
    private var tfliteInterpreter: Interpreter? = null

    // Reusable ByteBuffers for input and output
    private val inputBuffer: ByteBuffer = ByteBuffer.allocateDirect(4 * windowSize * numFeatures).order(ByteOrder.nativeOrder())
    private val outputBuffer: ByteBuffer = ByteBuffer.allocateDirect(4 * 11).order(ByteOrder.nativeOrder()) // Adjust size for 11 output classes

    init {
        initTflite()
    }

    // Initialize TensorFlow Lite interpreter
    private fun initTflite() {
        try {
            val tfliteModel = FileUtil.loadMappedFile(context, "mobilehart_model.tflite")
            val tfliteOptions = Interpreter.Options()
            tfliteOptions.setUseXNNPACK(true)  // Enable XNNPACK delegate for optimized inference
            tfliteInterpreter = Interpreter(tfliteModel, tfliteOptions)
            Log.d("SensorDataProcessor", "TFLite model loaded successfully")
        } catch (e: Exception) {
            Log.e("SensorDataProcessor", "Error initializing TFLite", e)
            tfliteInterpreter = null // Ensure null if initialization fails
        }
    }

    // Process incoming sensor data
    suspend fun processSensorData(values: FloatArray, sensorType: Int) = withContext(Dispatchers.Default) {
        try {
            // Update sensor data based on the type
            when (sensorType) {
                Sensor.TYPE_ACCELEROMETER -> currentAccelData = normalize(values)
                Sensor.TYPE_GYROSCOPE -> currentGyroData = normalize(values)
            }

            // Check if both accelerometer and gyroscope data are ready
            if (currentAccelData == null || currentGyroData == null) {
                Log.e("SensorProcessor", "Sensor data is incomplete: Accel = $currentAccelData, Gyro = $currentGyroData")
                return@withContext
            }

            // Combine accelerometer and gyroscope data
            val combinedData = FloatArray(numFeatures).apply {
                System.arraycopy(currentAccelData!!, 0, this, 0, 3)
                System.arraycopy(currentGyroData!!, 0, this, 3, 3)
            }

            // Add combined data to the buffer
            dataBuffer.add(combinedData)
            Log.d("SensorProcessor", "Combined data added: ${combinedData.contentToString()}")
            Log.d("SensorProcessor", "Current buffer size: ${dataBuffer.size}")

            // Ensure buffer size is sufficient for creating a window
            if (dataBuffer.size < windowSize) {
                Log.e("SensorProcessor", "Data buffer size (${dataBuffer.size}) is insufficient for creating a window.")
                return@withContext
            }

            if (dataBuffer.size > maxBufferSize) {
                dataBuffer.removeAt(0)  // Limit the buffer size to avoid excessive memory consumption
            }

            // Create a window of data for inference
            val window = createWindow()
            Log.d("SensorProcessor", "Window created for inference: ${window.contentDeepToString()}")

            // Validate the window shape before performing inference
            if (window.isEmpty() || window[0].isEmpty() || window[0].size != numFeatures) {
                Log.e("SensorProcessor", "Window creation failed or incorrect shape: [${window.size}, ${if (window.isNotEmpty()) window[0].size else 0}]")
                return@withContext
            }

            performInference(window)

            // Maintain sliding window by removing old data
            if (dataBuffer.size > windowSize) {
                dataBuffer.subList(0, stepSize).clear()  // Efficiently remove the first `stepSize` elements
            }
        } catch (e: Exception) {
            Log.e("SensorProcessor", "Unexpected error processing sensor data", e)
        }
    }

    // Create a window of data from the buffer
    private fun createWindow(): Array<FloatArray> {
        return try {
            // Always take the latest `windowSize` samples for the window
            val startIndex = maxOf(0, dataBuffer.size - windowSize)
            val window = dataBuffer.subList(startIndex, dataBuffer.size).toTypedArray()

            if (window.size < windowSize) {
                Log.e("SensorProcessor", "Window size insufficient (${window.size} < $windowSize), padding with zeros.")
                // Padding with zeros if necessary
                val paddedWindow = Array(windowSize) { FloatArray(numFeatures) }
                for (i in window.indices) {
                    paddedWindow[i] = window[i]
                }
                paddedWindow
            } else {
                window
            }
        } catch (e: Exception) {
            Log.e("SensorProcessor", "Error creating window", e)
            emptyArray()
        }
    }

    // Perform inference using the TensorFlow Lite model
    private fun performInference(window: Array<FloatArray>) {
        // Ensure the TensorFlow Lite interpreter is initialized
        if (tfliteInterpreter == null) {
            Log.e("SensorProcessor", "TFLite interpreter is not initialized")
            return
        }

        val expectedShape = intArrayOf(1, windowSize, numFeatures) // Expected input shape
        inputBuffer.clear()  // Clear buffer before reuse

        try {
            // Fill input buffer with data
            for (row in window) {
                for (value in row) {
                    inputBuffer.putFloat(value)
                }
            }
            Log.d("SensorProcessor", "Input buffer ready for inference")

            outputBuffer.clear()  // Clear output buffer before reuse
            tfliteInterpreter?.run(inputBuffer, outputBuffer)
            outputBuffer.rewind()

            val output = FloatArray(11) // Adjust size for expected number of classes
            outputBuffer.asFloatBuffer().get(output)

            // Log raw output values to ensure inference was performed
            Log.d("SensorProcessor", "Raw inference output: ${output.joinToString()}")

            if (output.any { it.isNaN() }) {
                Log.e("SensorProcessor", "Inference output contains NaN values: ${output.contentToString()}")
                return
            }

            handleOutput(output, window)
        } catch (e: Exception) {
            Log.e("SensorProcessor", "Unexpected error during inference", e)
        }
    }

    // Normalize sensor data to range [-1, 1]
    private fun normalize(data: FloatArray): FloatArray {
        val minVal = -1.0f // Adjust according to your expected input range
        val maxVal = 1.0f  // Adjust according to your expected input range

        val min = data.minOrNull() ?: 0.0f
        val max = data.maxOrNull() ?: 1.0f

        val normalizedData = data.map {
            val scaledValue = if (max - min != 0f) (it - min) / (max - min) else 0f
            minVal + scaledValue * (maxVal - minVal)
        }.toFloatArray()

        Log.d("SensorProcessor", "Normalized data: ${normalizedData.contentToString()}")
        return normalizedData
    }

    // Handle the model's output after inference
    private fun handleOutput(output: FloatArray, window: Array<FloatArray>) {
        try {
            val (activity, confidence) = decodeActivity(output)
            Log.d("SensorProcessor", "Decoded activity: $activity, Confidence: $confidence")
            val sensorDataString = createSensorDataString(window)
            updateActivityState(activity, confidence, sensorDataString)
        } catch (e: Exception) {
            Log.e("SensorProcessor", "Error handling output", e)
        }
    }

    // Decode the activity from the model output using softmax
    private fun decodeActivity(outputArray: FloatArray): Pair<String, Float> {
        val softmaxOutput = softmax(outputArray)
        val maxIndex = softmaxOutput.indexOfMax()
        val maxValue = softmaxOutput[maxIndex]

        val activity = activityNames.getOrElse(maxIndex) { "Unknown Activity" }
        return Pair(activity, maxValue)
    }

    // Softmax function to normalize output probabilities
    private fun softmax(input: FloatArray): FloatArray {
        val max = input.maxOrNull() ?: 0f
        val exp = input.map { exp(it - max) }
        val sum = exp.sum()
        return exp.map { (it / sum).toFloat() }.toFloatArray()
    }

    // Create a string representation of the sensor data window
    private fun createSensorDataString(window: Array<FloatArray>): String {
        return window.joinToString(separator = "\n") { it.joinToString(separator = ",") }
    }

    // Find the index of the maximum value in an array
    private fun FloatArray.indexOfMax(): Int = this.indices.maxByOrNull { this[it] } ?: -1

    companion object {
        private const val ACTIVITY_THRESHOLD = 0.2f
        private val activityNames = listOf(
            "Cycling",
            "Jogging",
            "Jump Rope",
            "Push-up",
            "Run",
            "Sit-up",
            "Table Tennis",
            "Undetected",
            "Walk",
            "Walking Downstairs",
            "Walking Upstairs"
        )
    }
}
