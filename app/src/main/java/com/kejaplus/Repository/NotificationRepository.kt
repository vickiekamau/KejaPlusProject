package com.kejaplus.Repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.*
import com.kejaplus.application.Model.Notification
import com.kejaplus.application.Support.NotificationWorker
import com.kejaplus.application.db.AppDatabase

class NotificationRepository(context: Context) {
    private val db: AppDatabase = AppDatabase.getDB(context)
    private val mContext = context

    fun addNotification(notification: Notification){
        val title = notification.title
        Log.d("title inserted", notification.title)
        Log.d("message", notification.message)

        uploadNotification(mContext,title)
         db.notificationDao().insertNotification(notification)
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
        return db.notificationDao().getAll()
    }
}