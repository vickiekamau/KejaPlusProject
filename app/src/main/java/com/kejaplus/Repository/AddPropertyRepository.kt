package com.kejaplus.Repository

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.work.*
import com.example.kejaplus.Model.SaveProperty
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kejaplus.application.Model.Property
import com.kejaplus.application.Support.PostWorker
import com.kejaplus.application.db.AppDatabase
import kotlinx.coroutines.tasks.await
import java.net.MalformedURLException

class AddPropertyRepository(context: Context)   {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var propertyArrayList :List<SaveProperty>
    private lateinit var storageReference: StorageReference
    private val mContext = context
    val CHANNEL_ID = "channel_id_1"



    /**fun saveProperty(saveProperty: Property): String{
        storageReference = FirebaseStorage.getInstance().reference

        databaseReference = FirebaseDatabase.getInstance().getReference("property")

        val propertyId = databaseReference.push().key
        val sProperty = SaveProperty(propertyId!!,saveProperty.property_category,saveProperty.property_type,saveProperty.no_bedroom,saveProperty.location,saveProperty.property_name,saveProperty.condition,saveProperty.price,saveProperty.contact_no,saveProperty.property_desc,saveProperty.imageId,saveProperty.timeStamp)

        var success = false

        databaseReference.child(propertyId).setValue(sProperty).addOnCompleteListener{task ->
           if(task.isSuccessful){
               Log.d("save success","Save data to DB is successful")
               val uriImage = uriToString(saveProperty.imagePath)
               val imageId = saveProperty.imageId
               if (uriImage != null) {
                   uploadImageWorker(mContext,uriImage,imageId)
                   Log.d("Worker class","Worker class called")
               } else {
                   Toast.makeText(mContext,"Image not found",Toast.LENGTH_LONG).show()
               }

           }
        }

      return propertyId.toString()
    }*/

    suspend fun addProperty(saveProperty: Property):String{
        val db = FirebaseFirestore.getInstance()
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
            "imageId" to saveProperty.imageId,
            "timeStamp" to saveProperty.timeStamp
        )
        val docReference =  db.collection("property").add(property).await()
        val uriImage = uriToString(saveProperty.imagePath)
        try{
            if (uriImage != null) {
                uploadImageWorker(mContext,uriImage,saveProperty.imageId)
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
    private fun uploadImageWorker(context: Context, imageUri: String, imageId: String){
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val data = Data.Builder()
        //data.putString("imageUrl", imageUri)
        //data.putString("imageID", imageId)
        val imageStringArray = arrayOf(imageUri,imageId)
        data.putStringArray("image Strings",imageStringArray)
        Log.d("uploadedWImage",imageStringArray.toString() )

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

