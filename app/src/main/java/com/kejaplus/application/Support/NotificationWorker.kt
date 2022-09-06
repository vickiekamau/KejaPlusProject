package com.kejaplus.application.Support

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kejaplus.Repository.PostImageRepository
import com.kejaplus.application.MainActivity
import com.kejaplus.application.R
import java.io.IOException

class NotificationWorker (context: Context, parameters: WorkerParameters) :
    Worker(context, parameters) {

    var mContext: Context = context
    private val CHANNEL_ID = "channel_id_1"
    private val notificationId = 78
    override fun doWork(): Result {

        try {
            val titleString = inputData.getString("notification title")

            // initializing the notification relay
            sendNotification(titleString!!)

            return Result.success()

        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("Notification", e.toString())

        }
        Log.d("Notification", "failed")
        return Result.failure()


    }
    //function that sends notification after the sync work is done
    private fun sendNotification(title: String){
        // Create an explicit intent for an Activity in your app
        val intent = Intent(mContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0)

        val builder = NotificationCompat.Builder(mContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("KejaPlus")
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(mContext)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }

}