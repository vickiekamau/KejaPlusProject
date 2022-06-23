package com.kejaplus.application.ui.AddProperty

import android.app.Application
import android.content.Context

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kejaplus.Repository.AddPropertyRepository
import com.kejaplus.application.Model.Property
import com.kejaplus.application.response.Resource
import kotlinx.coroutines.launch
import androidx.lifecycle.*
import com.kejaplus.application.Network.NetworkConnectivity


class AddPropertyViewModel(application: Application) : AndroidViewModel(application)  {


    private val repository = AddPropertyRepository(application)



    private val _insertPropertyStatus = MutableLiveData<Resource<String>>()

    val insertPropertyStatus: LiveData<Resource<String>> = _insertPropertyStatus

    // Save method that saves the property data to realtime database
    fun insertPropertyData(property: Property) {

        viewModelScope.launch {
            _insertPropertyStatus.postValue(Resource.loading(null))
            try {
                val data = repository.saveProperty(property)
                _insertPropertyStatus.postValue(Resource.success(data, ""))

            } catch (exception: Exception) {
                _insertPropertyStatus.postValue(Resource.error(null, exception.message!!))
            }
        }
    }

    fun netConnectivity(context: Context):Boolean {
        val networkConnectivity = NetworkConnectivity(context)
        return networkConnectivity.isOnline()
    }
    //
}


