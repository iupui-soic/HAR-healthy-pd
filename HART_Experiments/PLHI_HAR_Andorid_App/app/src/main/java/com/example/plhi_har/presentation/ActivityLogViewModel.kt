package com.example.plhi_har.presentation
//
//import android.content.Context
//import androidx.lifecycle.*
//import kotlinx.coroutines.launch
//
//class ActivityLogViewModel(private val context: Context) : ViewModel() {
//
//    private val activityLogDao: ActivityLogDao = AppDatabase.getDatabase(context).activityLogDao()
//
//    val allLogs: LiveData<List<ActivityLog>> = liveData {
//        emitSource(activityLogDao.getAllLogs().asLiveData())
//    }
//
//    fun insert(activityLog: ActivityLog) = viewModelScope.launch {
//        activityLogDao.insertLog(activityLog)
//    }
//}
//
//class ActivityLogViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(ActivityLogViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return ActivityLogViewModel(context) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import android.content.Context
import com.example.plhi_har.presentation.ActivityLogDao
import com.example.plhi_har.presentation.ActivityLog
import com.example.plhi_har.presentation.AppDatabase
import com.example.plhi_har.presentation.ActivityLogViewModel



class ActivityLogViewModel(private val activityLogDao: ActivityLogDao) : ViewModel() {

    val allLogs: LiveData<List<ActivityLog>> = activityLogDao.getAllLogs().asLiveData()

    fun insert(activityLog: ActivityLog) {
        viewModelScope.launch {
            activityLogDao.insert(activityLog)
        }
    }
}
