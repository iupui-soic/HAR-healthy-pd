package com.example.plhi_har.presentation

import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity(tableName = "activity_log")
//data class ActivityLog(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    val activityName: String,
//    val timestamp: Long
//)



//@Entity(tableName = "activity_log")
//data class ActivityLog(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    val activityName: String,
//    val timestamp: Long,
//    val sensorData: String  // Storing the sensor data as a JSON string
//)


@Entity(tableName = "activity_log")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val activityName: String,
    val timestamp: Long,
    val sensorData: String,  // Storing the sensor data as a JSON string
    val confidence: Float
)

