package com.kejaplus.application.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kejaplus.Model.SaveProperty
import com.kejaplus.application.R

class ImageAdapter(
    private val propertyList: ArrayList<SaveProperty>
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
        val propertyItem = propertyList[position]
        val image = propertyItem.image
        holder.propertyName.text = propertyItem.property_name
        Glide.with(holder.itemView).load(image).into(holder.imageUrl)
    }



}