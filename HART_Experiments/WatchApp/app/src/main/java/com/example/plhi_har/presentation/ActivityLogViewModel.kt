//package com.example.plhi_har.presentation
//
//import android.app.Application
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.io.File
//import java.io.FileWriter
//import com.google.gson.GsonBuilder
//
//class ActivityLogViewModel(application: Application) : AndroidViewModel(application) {
//    private val activityLogDao: ActivityLogDao
//    private val _allLogs = MutableLiveData<List<ActivityLog>>() // Changed to LiveData
//    val allLogs: LiveData<List<ActivityLog>> get() = _allLogs
//
//    init {
//        val database = AppDatabase.getInstance(application)
//        activityLogDao = database.activityLogDao()
//        fetchLogs() // Fetch logs from the database
//    }
//
//    private fun fetchLogs() {
//        viewModelScope.launch(Dispatchers.IO) { // Launch within IO dispatcher
//            val logs = activityLogDao.getAllLogsSync() // Correctly fetch logs synchronously
//            _allLogs.postValue(logs) // Post value to LiveData
//        }
//    }
//
//    fun insert(activityLog: ActivityLog) {
//        viewModelScope.launch(Dispatchers.IO) { // Ensure launch within the correct coroutine context
//            activityLogDao.insert(activityLog)
//            fetchLogs() // Update logs after insertion
//        }
//    }
//
//    suspend fun exportLogsToJson(context: android.content.Context): File = withContext(Dispatchers.IO) {
//        val logs = activityLogDao.getAllLogsSync()
//        val gson = GsonBuilder().setPrettyPrinting().create()
//        val jsonLogs = gson.toJson(logs)
//
//        val file = File(context.cacheDir, "activity_logs.json")
//        FileWriter(file).use { it.write(jsonLogs) }
//        file
//    }
//}

package com.example.plhi_har.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import com.google.gson.GsonBuilder

class ActivityLogViewModel(application: Application) : AndroidViewModel(application) {

    private val activityLogDao: ActivityLogDao
    private val _allLogs = MutableLiveData<List<ActivityLog>>()
    val allLogs: LiveData<List<ActivityLog>> get() = _allLogs

    init {
        // Initialize the database and DAO properly
        val database = AppDatabase.getInstance(application)
        activityLogDao = database.activityLogDao()
        fetchLogs()
    }

    private fun fetchLogs() {
        viewModelScope.launch {
            _allLogs.postValue(activityLogDao.getAllLogsSync())
        }
    }

    fun insert(activityLog: ActivityLog) {
        viewModelScope.launch {
            activityLogDao.insert(activityLog)
            fetchLogs()
        }
    }

    suspend fun exportLogsToJson(context: android.content.Context): File = withContext(Dispatchers.IO) {
        val logs = activityLogDao.getAllLogsSync()
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonLogs = gson.toJson(logs)

        val file = File(context.cacheDir, "activity_logs.json")
        FileWriter(file).use { it.write(jsonLogs) }
        file
    }
}
