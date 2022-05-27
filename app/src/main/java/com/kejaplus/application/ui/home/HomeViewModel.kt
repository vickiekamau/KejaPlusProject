package com.kejaplus.application.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.kejaplus.Model.SaveProperty
import com.kejaplus.Repository.HomeRepository
import com.kejaplus.application.Model.Property
import com.kejaplus.application.db.AppDatabase
import kotlinx.coroutines.Dispatchers

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDB(application)

    private val repository = HomeRepository(application)





    val text : MutableLiveData<String> = MutableLiveData()
    val fetchedData = text.switchMap{
        liveData(Dispatchers.IO) {
            if (it==null||it==""){
                val data = repository.getPropertyData()
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

    /**fun getPropertyData(){
       //repository.getPropertyData(_propertyItems)
        repository.getPropertyData(allProperties)
    }*/


}