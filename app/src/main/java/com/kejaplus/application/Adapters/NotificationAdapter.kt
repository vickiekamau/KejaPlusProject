package com.kejaplus.application.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.kejaplus.application.Model.Notification
import com.kejaplus.application.R
import com.kejaplus.application.databinding.NotificationItemBinding


class NotificationAdapter :
    //ListAdapter<Notification, NotificationAdapter.NotificationViewHolder>(Notification.COMPARATOR)
    PagingDataAdapter<Notification, NotificationAdapter.NotificationViewHolder>(Notification.COMPARATOR){

    inner class NotificationViewHolder(
        val binding: NotificationItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {

            }
        }

        fun bind(notification: Notification) {
            binding.apply {
                titleN.text = notification.title
                ts.text = notification.timeStamp
                message.text = notification.message
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

}
