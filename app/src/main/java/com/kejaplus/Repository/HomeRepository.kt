package com.kejaplus.Repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.kejaplus.Model.SaveProperty
import com.google.firebase.database.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.kejaplus.application.Model.FetchDataResponse
import com.kejaplus.application.Support.Constants.PRODUCTS_REF
import com.kejaplus.application.db.AppDatabase
import kotlinx.coroutines.tasks.await

class HomeRepository(application: Application,
                     private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance(),
                     private val productRef: CollectionReference = rootRef.collection(PRODUCTS_REF)
)  {
    private val db: AppDatabase = AppDatabase. getDB(application)




    suspend fun fetchPropertyData(): LiveData<List<SaveProperty>>{

       val response = FetchDataResponse()
       val savePropertyDao = db.savePropertyDao()

        try {
            response.property = productRef.get().await().documents.mapNotNull { snapShot ->
                snapShot.toObject(SaveProperty::class.java)
            }
            savePropertyDao.clearProperty()
            savePropertyDao.syncProperty(response.property!!)
            Log.i("results", response.property!!.toString())
        } catch (exception: Exception) {
            response.exception = exception
        }
        return db.savePropertyDao().getAll()
    }

    fun getOfflineData(): LiveData<List<SaveProperty>>{
        return db.savePropertyDao().getAll()
    }

     fun searchProperty(text:String): LiveData<List<SaveProperty>>{
            Log.d("searched record",db.savePropertyDao().getSearchResult(text).toString() )
         return db.savePropertyDao().getSearchResult(text)
        }


}