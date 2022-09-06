package com.kejaplus.application.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kejaplus.application.Adapters.NotificationAdapter
import com.kejaplus.application.databinding.FragmentNotificationBinding
import com.kejaplus.application.ui.home.HomeViewModel


class NotificationFragment: Fragment() {

    private lateinit var binding: FragmentNotificationBinding
    private val viewModel: NotificationViewModel by viewModels()
    private val adapter = NotificationAdapter()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = FragmentNotificationBinding.inflate(inflater,container,false)

        binding.recyclerview.adapter = adapter

        adapter.submitList(viewModel.fetchNotifications)

        return binding.root

    }

}