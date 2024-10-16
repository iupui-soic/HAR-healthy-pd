package com.example.plhi_har.presentation
//
////import androidx.room.Dao
////import androidx.room.Insert
////import androidx.room.OnConflictStrategy
////import androidx.room.Query
////import kotlinx.coroutines.flow.Flow
//
////@Dao
////interface ActivityLogDao {
////
////    @Query("SELECT * FROM activity_log ORDER BY timestamp DESC")
////    fun getAllLogs(): Flow<List<ActivityLog>>
////
////    @Insert(onConflict = OnConflictStrategy.IGNORE)
////    suspend fun insert(activityLog: ActivityLog)
////}
//
//
//
//import androidx.lifecycle.*
//import kotlinx.coroutines.launch
//import android.content.Context
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.io.File
//import java.io.FileWriter
//import com.google.gson.GsonBuilder
//
//class ActivityLogViewModel(private val activityLogDao: ActivityLogDao) : ViewModel() {
//
//    // Use Flow and convert it to LiveData
//    val allLogs: LiveData<List<ActivityLog>> = activityLogDao.getAllLogs().asLiveData()
//
//    fun insert(activityLog: ActivityLog) {
//        viewModelScope.launch {
//            activityLogDao.insert(activityLog)
//        }
//    }
//
//    suspend fun exportLogsToJson(context: Context): File = withContext(Dispatchers.IO) {
//        val logs = activityLogDao.getAllLogsSync()
//        val gson = GsonBuilder().setPrettyPrinting().create()
//        val jsonLogs = gson.toJson(logs)
//
//        val file = File(context.cacheDir, "activity_logs.json")
//        FileWriter(file).use { it.write(jsonLogs) }
//        file
//    }
//}


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {

    // Asynchronous function to get logs as a Flow
    @Query("SELECT * FROM activity_log ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<ActivityLog>>

    // Synchronous function to get logs, useful for operations needing immediate access
    @Query("SELECT * FROM activity_log ORDER BY timestamp DESC")
    suspend fun getAllLogsSync(): List<ActivityLog>

    @Query("SELECT * FROM activity_log ORDER BY timestamp DESC")
    fun getAllActivityLogs(): Flow<List<ActivityLog>>

    // Insert a new log entry, ignoring conflicts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activityLog: ActivityLog)
}




