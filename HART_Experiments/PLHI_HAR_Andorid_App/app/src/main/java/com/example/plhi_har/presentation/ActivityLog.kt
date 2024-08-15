package com.example.plhi_har.presentation

import androidx.room.Entity
import androidx.room.PrimaryKey


//@Entity(tableName = "activity_log")
//data class ActivityLog(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    val activityName: String,
//    val timestamp: Long = System.currentTimeMillis()
//)



@Entity(tableName = "activity_log")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val activityName: String,
    val timestamp: Long
)
