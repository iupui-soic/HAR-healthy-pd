package com.example.plhi_har.presentation

//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import kotlinx.coroutines.flow.Flow

//@Dao
//interface ActivityLogDao {
//    @Insert
//    fun insertLog(activityLog: ActivityLog)
//
//    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC")
//    fun getAllLogs(): List<ActivityLog>
//}
//


//@Dao
//interface ActivityLogDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertLog(activityLog: ActivityLog)
//
//    @Query("SELECT * FROM ActivityLog")
//    fun getAllLogs(): Flow<List<ActivityLog>>
//}

//
//@Dao
//interface ActivityLogDao {
//    @Insert
//    suspend fun insertLog(log: ActivityLog)
//
//    @Query("SELECT * FROM activity_log ORDER BY id DESC")
//    fun getAllLogs(): Flow<List<ActivityLog>>
//}



//package com.example.plhi_har.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {

    @Query("SELECT * FROM activity_log ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<ActivityLog>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(activityLog: ActivityLog)
}
