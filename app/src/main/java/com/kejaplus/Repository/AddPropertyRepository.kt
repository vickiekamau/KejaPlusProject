package com.kejaplus.Repository

import android.annotation.SuppressLint
import android.app.Application
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.kejaplus.Model.SaveProperty
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kejaplus.application.MainActivity
import com.kejaplus.application.Model.Property
import com.kejaplus.application.db.AppDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddPropertyRepository(application: Application)  {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var propertyArrayList :List<SaveProperty>
    private lateinit var storageReference: StorageReference
    private val db: AppDatabase = AppDatabase. getDB(application)




    fun saveProperty(saveProperty: Property): String{
        storageReference = FirebaseStorage.getInstance().reference

        databaseReference = FirebaseDatabase.getInstance().getReference("property")

        val propertyId = databaseReference.push().key
        val sProperty = SaveProperty(propertyId!!,saveProperty.property_category,saveProperty.property_type,saveProperty.no_bedroom,saveProperty.location,saveProperty.property_name,saveProperty.condition,saveProperty.price,saveProperty.contact_no,saveProperty.property_desc,saveProperty.imageId,saveProperty.timeStamp)

        var success = false

        databaseReference.child(propertyId).setValue(sProperty).addOnCompleteListener{task ->
            success = task.isSuccessful
        }
        val ref: StorageReference = storageReference.child(saveProperty.imageId)
        ref.putFile(saveProperty.imagePath).addOnSuccessListener(OnSuccessListener<Any?> {

        }).addOnFailureListener(OnFailureListener { e ->
            return@OnFailureListener
        })
      return if (success) propertyId.toString() else ""
    }
}