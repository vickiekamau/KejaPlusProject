package com.kejaplus.Repository

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.work.*
import com.example.kejaplus.Model.SaveProperty
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kejaplus.application.Model.Notification
import com.kejaplus.application.Model.Property
import com.kejaplus.application.Support.Constants
import com.kejaplus.application.Support.NotificationWorker
import com.kejaplus.application.Support.PostWorker
import com.kejaplus.application.db.AppDatabase
import kotlinx.coroutines.tasks.await
import java.net.MalformedURLException

class AddPropertyRepository(context: Context,
                            private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance(),
                            private val productRef: CollectionReference = rootRef.collection(Constants.PRODUCTS_REF)
){
    private lateinit var databaseReference: DatabaseReference
    private lateinit var propertyArrayList :List<SaveProperty>
    private lateinit var storageReference: StorageReference
    private val db: AppDatabase = AppDatabase.getDB(context)
    private val mContext = context


    suspend fun addProperty(saveProperty: Property):String{
        val property = mapOf(
            "property_category" to saveProperty.property_category,
            "property_type" to saveProperty.property_type,
            "no_bedroom" to saveProperty.no_bedroom,
            "location" to saveProperty.location,
            "property_name" to saveProperty.property_name,
            "condition" to saveProperty.condition,
            "price" to saveProperty.price,
            "contact_no" to saveProperty.contact_no,
            "property_desc" to saveProperty.property_desc,
            "image" to saveProperty.imageId,
            "timeStamp" to saveProperty.timeStamp
        )
        val docReference = productRef.add(property).await()
        val uriImage = uriToString(saveProperty.imagePath)
        try{
            if (uriImage != null) {
                val title = "Property Image"
                val message = "Property Image Saved Successfully"
                val ts = saveProperty.timeStamp
                val notification = Notification(0,title,message,ts)
                uploadImageWorker(mContext,uriImage,saveProperty.imageId,notification)
                Log.d("Worker class","Worker class called")
              } else {
                Toast.makeText(mContext,"Image not found",Toast.LENGTH_LONG).show()
             }
          } catch(e:Exception){
               Toast.makeText(mContext,"${e.message}",Toast.LENGTH_LONG).show()
            }

        return docReference.id
    }


    //method that call background worker  and set constrain to Network of type connected
    private fun uploadImageWorker(context: Context, imageUri: String, imageId: String, notification: Notification){
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val data = Data.Builder()
        val imageStringArray = arrayOf(imageUri,imageId)
        val notificationStringArray = arrayOf(notification.title,notification.message.toString())

        data.putStringArray("notification strings",notificationStringArray)
        data.putStringArray("image Strings",imageStringArray)

        //insert notification to db
        db.notificationDao().insertNotification(notification)


        val uploadWorkRequest = OneTimeWorkRequest
            .Builder(PostWorker::class.java)
            .setInputData(data.build())
            .setConstraints(constraint)//i added constraints
            .build()

        WorkManager.getInstance(context).enqueue((uploadWorkRequest))


}



    private fun uriToString(uri: Uri?): String? {
        uri?.let {
            try {

                return uri.toString()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
        }

        return null
    }

    }

