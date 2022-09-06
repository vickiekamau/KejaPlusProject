package com.kejaplus.application.ui.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kejaplus.Repository.HomeRepository
import com.kejaplus.Repository.NotificationRepository
import com.kejaplus.application.Model.Notification
import com.kejaplus.application.Model.Property
import com.kejaplus.application.response.Resource
import kotlinx.coroutines.launch

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NotificationRepository(application)

    private val _fetchNotificationStatus = MutableLiveData<Resource<List<Notification>>>()

    private val fetchNotificationStatus: LiveData<Resource<List<Notification>>> = _fetchNotificationStatus

    val  fetchNotifications : List<Notification> = repository.getNotification()


}