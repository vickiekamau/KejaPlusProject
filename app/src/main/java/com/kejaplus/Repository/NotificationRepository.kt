package com.kejaplus.Repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.room.Query
import androidx.room.Transaction
import androidx.work.*
import com.kejaplus.application.Model.Notification
import com.kejaplus.application.Model.NotificationDao
import com.kejaplus.application.Support.NotificationWorker
import com.kejaplus.application.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationRepository(context: Context) {
    private val db: AppDatabase = AppDatabase.getDB(context)
    private val mContext = context

    private val notificationDao: NotificationDao by lazy {
        db.notificationDao()
    }


    val newNotifications = notificationDao.count()

    fun addNotification(notification: Notification){
        val title = notification.title
        Log.d("title inserted", notification.title)
        Log.d("message", notification.message)

        uploadNotification(mContext,title)
         notificationDao.insertNotification(notification)
    }

    private fun uploadNotification(context: Context, title: String){

        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val data = Data.Builder()
        data.putString("notification title",title)

        val notificationWorkRequest = OneTimeWorkRequest
            .Builder(NotificationWorker::class.java)
            .setInputData(data.build())
            .setConstraints(constraint)//i added constraints
            .build()

        WorkManager.getInstance(context).enqueue((notificationWorkRequest))


    }

    fun getNotification(): List<Notification> {
        return notificationDao.getAll()
    }

    fun getUnreadNotifications():LiveData<Int>{
        return notificationDao.count()
    }

    suspend fun clearAllNotifications() {
        withContext(Dispatchers.IO) {
            notificationDao.clear()
        }
    }

    suspend fun readNotification(vararg notification: Notification) {
        withContext(Dispatchers.IO) {
            notificationDao.markAsRead(*notification)
        }
    }


    suspend fun markAllNotificationsAsRead() {
        withContext(Dispatchers.IO) {
            notificationDao.markAllAsRead()
        }
    }

    suspend fun deleteNotifications(vararg notification: Notification) {
        withContext(Dispatchers.IO) {
            notificationDao.delete(*notification)
        }
    }



    val notifications = Pager(
        config = PagingConfig(pageSize = 10, maxSize = 40, enablePlaceholders = false),
        pagingSourceFactory = {
            notificationDao.all()
        }
    ).flow
}