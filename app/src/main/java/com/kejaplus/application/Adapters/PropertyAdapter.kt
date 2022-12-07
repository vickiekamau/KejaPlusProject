package com.kejaplus.application.Adapters

import android.content.Context
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
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import android.widget.ProgressBar
import coil.load
import com.kejaplus.application.interfaces.Communicator
import com.squareup.picasso.Picasso
import timber.log.Timber


class PropertyAdapter(
    private val propertyList: ArrayList<SaveProperty>,
    private val context: Context,
    private val listener: Communicator
):RecyclerView.Adapter<PropertyAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View):RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val propertyName: TextView = itemView.findViewById(com.kejaplus.application.R.id.imageName)
        val imageUrl: ImageView = itemView.findViewById(com.kejaplus.application.R.id.ivImage)
        val progressBar: ProgressBar = itemView.findViewById(com.kejaplus.application.R.id.progressBar)
        val view: View = itemView

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            val propertyName = propertyList[adapterPosition].property_name
            val propertyType = propertyList[adapterPosition].property_type
            val propertyDesc = propertyList[adapterPosition].property_desc
            val propertyLocation = propertyList[adapterPosition].location
            val propertyCondition = propertyList[adapterPosition].condition
            val propertyCategory = propertyList[adapterPosition].property_category
            val price = propertyList[adapterPosition].price
            val timeStamp = propertyList[adapterPosition].timeStamp
            val propertyImage = propertyList[adapterPosition].image

            if (position != RecyclerView.NO_POSITION) {
                listener.passData(
                    position,
                    propertyName,
                    propertyType,
                    propertyDesc,
                    propertyLocation,
                    propertyCondition,
                    propertyCategory,
                    price,
                    timeStamp,
                    propertyImage
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(LayoutInflater.from(parent.context).inflate(com.kejaplus.application.R.layout.grid_item_view,parent,false))
    }

    override fun getItemCount(): Int {
        Timber.tag("property size").e(propertyList.size.toString())
        return propertyList.size

    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        var storageReference = FirebaseStorage.getInstance().getReference("images/")

        val propertyItem = propertyList[position]
        val image = propertyItem.image
        storageReference = FirebaseStorage.getInstance().reference.child(image)
        holder.propertyName.text = propertyItem.property_name
        Timber.tag("Image").i(image)

        //val picasso = Picasso.get()

        storageReference.downloadUrl.addOnSuccessListener { uri -> // Got the download URL for the image
            holder.progressBar.visibility = View.GONE
            // Pass it to Picasso to download, show in ImageView and caching
            //picasso.load(uri.toString()).into(holder.imageUrl)
            holder.imageUrl.load(uri)
        }.addOnFailureListener { e ->
            // Handle any errors
            holder.progressBar.visibility = View.GONE
            Toast.makeText(
                context,
                "Failed " + e.message,
                Toast.LENGTH_SHORT
            ).show()
        }

    }


    }
