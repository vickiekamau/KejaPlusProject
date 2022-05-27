package com.kejaplus.Repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kejaplus.Model.SaveProperty
import com.google.firebase.database.*
import com.kejaplus.application.db.AppDatabase

class HomeRepository(application: Application)  {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var propertyArrayList :List<SaveProperty>
    private val db: AppDatabase = AppDatabase. getDB(application)


    fun getPropertyData(): LiveData<List<SaveProperty>> {
        databaseReference = FirebaseDatabase.getInstance().getReference("property")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val propertyItems: List<SaveProperty> = snapshot.children.map { dataSnapshot ->
                    dataSnapshot.getValue(SaveProperty::class.java)!!
                }

                 //liveData.postValue(propertyItems)
                propertyArrayList = propertyItems
                try{
                    val savePropertyDao = db.savePropertyDao()
                    savePropertyDao.clearProperty()
                    savePropertyDao.syncProperty(*propertyItems.toTypedArray())
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

    /**fun fetchProperty():LiveData<List<SaveProperty>>{
        if(getPropertyData().isEmpty()){
            Log.d("Exception", "Data Empty")
        }else{
            try{
                val savePropertyDao = db.savePropertyDao()
                savePropertyDao.syncProperty(*getPropertyData().toTypedArray())
                Log.i("results", getPropertyData().toString())
            }catch (e:Exception){
                Log.d("Exception", e.message.toString())
            }
        }
         return db.savePropertyDao().getAll()

    }*/

     fun searchProperty(text:String): LiveData<List<SaveProperty>>{
            Log.d("searched record",db.savePropertyDao().getSearchResult(text).toString() )
         return db.savePropertyDao().getSearchResult(text)
        }

   /** init {
        propertyArrayList = arrayListOf<SaveProperty>()
        databaseReference = FirebaseDatabase.getInstance().getReference("property")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (propertySnapshot in snapshot.children) {
                    val propertyItems = propertySnapshot.getValue(SaveProperty::class.java)
                    propertyArrayList.add(propertyItems!!)


                }
                Log.i("Properties items", propertyArrayList.toString())
            }

            override fun onCancelled(error: DatabaseError) {

                //Toast.makeText(.application,error.toString(), Toast.LENGTH_LONG).show()
            }

         })

    }*/
}