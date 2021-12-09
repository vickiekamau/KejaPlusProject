package com.kejaplus.application.Adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kejaplus.Model.SaveProperty
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.kejaplus.application.R
import java.io.File

class ImageAdapter(
    private val propertyList: ArrayList<SaveProperty>,
    private val context: Context
):RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val propertyName: TextView = itemView.findViewById(R.id.imageName)
        val imageUrl : ImageView = itemView.findViewById(R.id.ivImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.grid_item_view,parent,false))
    }

    override fun getItemCount(): Int {
        Log.e("property size", propertyList.size.toString())
        return propertyList.size

    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
       var storageReference = FirebaseStorage.getInstance().getReference("images/")

        val propertyItem = propertyList[position]
        val image = propertyItem.image
        storageReference = FirebaseStorage.getInstance().reference.child(image)
        holder.propertyName.text = propertyItem.property_name
        Glide.with(holder.itemView).load(image).into(holder.imageUrl)
        val localFile = File.createTempFile("image", "jpg")
        storageReference.getFile(localFile)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                Glide.with(holder.itemView).load(bitmap).dontTransform().into(holder.imageUrl)
               // activityNoteEditorBinding.iDImageView.setImageBitmap(bitmap)
            }.addOnFailureListener { e ->
                Toast.makeText(context,
                    "Failed " + e.message,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
    }



}