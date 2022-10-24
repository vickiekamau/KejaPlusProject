package com.kejaplus.application.Support

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kejaplus.Repository.PostImageRepository
import com.kejaplus.application.ui.mainui.MainActivity
import com.kejaplus.application.R
import java.io.IOException
import java.net.MalformedURLException

class PostWorker(context: Context, parameters: WorkerParameters) :
    Worker(context, parameters) {

    var mContext:Context = context
    private lateinit var postRemoteRepository: PostImageRepository
    private lateinit var newFarmData:String
    private lateinit var newFarmerData:String
    private val CHANNEL_ID = "channel_id_1"
    private lateinit var storageReference: StorageReference
    private val notificationId = 78
    override fun doWork(): Result {

        try {
            //val uriString = inputData.getString("imageUrl")
            //val imageId = inputData.getString("imageId")
            val notificationString = inputData.getStringArray("notification strings")
            val imageString = inputData.getStringArray("image Strings")
            val a = imageString?.get(0)
            val b = imageString?.get(1)
            val title = notificationString?.get(0)
            val message = notificationString?.get(1)
            val uriImage = stringToURI(a)
            Log.d("image captured ", uriImage.toString())
            Log.d("image position ", b!!)
            Log.d("Title ", title!!)
            Log.d("Message", message!!)

            // initializing the post remote repository that uploads the image to firebase database
            //postRemoteRepository = PostImageRepository()

                storageReference = FirebaseStorage.getInstance().reference
                val ref: StorageReference = storageReference.child(b)
                ref.putFile(uriImage!!).addOnSuccessListener(OnSuccessListener<Any?> {

                    Log.d("download", "success")
                }).addOnFailureListener(OnFailureListener { e ->
                    return@OnFailureListener
                })

                Log.d("download", "success")
                //Return the success with output data

                sendNotification(title!!,message!!)
                return Result.success()

            } catch (e: IOException) {
                e.printStackTrace()
                Log.d("Upload", e.toString())

            }
            Log.d("Upload", "failed")
            return Result.failure()


    }
    //function that sends notification after the sync work is done
    private fun sendNotification(title: String,message:String){
        // Create an explicit intent for an Activity in your app
        val intent = Intent(mContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0)

        val builder = NotificationCompat.Builder(mContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(mContext)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }

    // Custom method to convert string to url
    private fun stringToURI(uriString: String?): Uri? {
        uriString?.let {
            try {

                return uriString.toUri()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
        }

        return null
    }


}