package com.kejaplus.Repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.kejaplus.Model.SaveProperty
import com.google.firebase.database.*
import com.kejaplus.application.db.AppDatabase

class HomeRepository(application: Application)  {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var propertyArrayList :List<SaveProperty>
    private val db: AppDatabase = AppDatabase. getDB(application)


    fun getPropertyData(): LiveData<List<SaveProperty>> {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("property")


        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val propertyItems: List<SaveProperty> = snapshot.children.map { dataSnapshot ->
                    dataSnapshot.getValue(SaveProperty::class.java)!!
                }
                //list.postValue(propertyItems)
                val savePropertyDao = db.savePropertyDao()

                try{
                    savePropertyDao.clearProperty()
                    savePropertyDao.syncProperty(propertyItems)
                    Log.i("results", propertyItems.toString())
                }catch (e:Exception){
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //Toast.makeText(.application,error.toString(), Toast.LENGTH_LONG).show()
            }
        })
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