package com.kejaplus.Repository

import android.net.Uri
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class PostImageRepository {
    private lateinit var storageReference: StorageReference
    fun postImage(imageUrl: Uri, imageId: String){
        storageReference = FirebaseStorage.getInstance().reference

        val ref: StorageReference = storageReference.child(imageId)
        ref.putFile(imageUrl).addOnSuccessListener(OnSuccessListener<Any?> {

        }).addOnFailureListener(OnFailureListener { e ->
            return@OnFailureListener
        })
    }
}