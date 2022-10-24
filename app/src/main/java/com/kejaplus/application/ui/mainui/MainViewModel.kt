package com.kejaplus.application.ui.mainui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.kejaplus.Repository.NotificationRepository

open class MainViewModel (application: Application) : AndroidViewModel(application) {

    private val repository = NotificationRepository(application)

    val notificationCount : LiveData<Int> = repository.getUnreadNotifications()

}