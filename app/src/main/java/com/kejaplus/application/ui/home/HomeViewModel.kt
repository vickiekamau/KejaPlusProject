package com.kejaplus.application.ui.home

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.kejaplus.Model.SaveProperty
import com.kejaplus.Repository.HomeRepository
import com.kejaplus.application.Model.Property
import com.kejaplus.application.Network.NetworkConnectivity
import com.kejaplus.application.db.AppDatabase
import kotlinx.coroutines.Dispatchers

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HomeRepository(application)


    val text : MutableLiveData<String> = MutableLiveData()
    val fetchData = text.switchMap{
        liveData(Dispatchers.IO) {
            if (it==null||it==""){
                val data = repository.fetchPropertyData()
                emitSource(data)
            }
            else{
                val data = repository.searchProperty(it)
                emitSource(data)
            }
        }
    }

    init {
        text.value = ""
    }
    fun search(searchText:String){
        text.value = searchText
        Log.d("searched data",searchText)
    }

    fun netConnectivity(context: Context):Boolean {
        val networkConnectivity = NetworkConnectivity(context)
        return networkConnectivity.isOnline()
    }

    //val  getOfflineData:LiveData<List<SaveProperty>> =  repository.getOfflineData()
    val fetchOfflineData = text.switchMap {
        liveData(Dispatchers.IO) {
            if (it == null || it == "") {
                val data = repository.getOfflineData()
                emitSource(data)
            } else {
                val data = repository.searchProperty(it)
                emitSource(data)
            }
        }
    }



}