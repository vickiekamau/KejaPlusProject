package com.kejaplus.application.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kejaplus.Model.SaveProperty
import com.kejaplus.Repository.HomeRepository
import com.kejaplus.application.Model.Property
import com.kejaplus.application.db.AppDatabase

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDB(application)

    private val repository = HomeRepository(application)



    val allProperties: LiveData<List<SaveProperty>> = db.savePropertyDao().getAll()

    private val _propertyItems = MutableLiveData<List<SaveProperty>>()
    val propertyItems:LiveData<List<SaveProperty>> = _propertyItems

    /**fun getPropertyData(){
       //repository.getPropertyData(_propertyItems)
        repository.getPropertyData(allProperties)
    }*/


}