package com.kejaplus.Repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.kejaplus.Model.SaveProperty
import com.google.firebase.database.*
import com.kejaplus.application.db.AppDatabase

class HomeRepository(application: Application)  {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var propertyArrayList : ArrayList<SaveProperty>
    private val db: AppDatabase = AppDatabase. getDB(application)


    fun getPropertyData(liveData:MutableLiveData<List<SaveProperty>>){
        databaseReference = FirebaseDatabase.getInstance().getReference("property")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val propertyItems: List<SaveProperty> = snapshot.children.map { dataSnapshot ->
                    dataSnapshot.getValue(SaveProperty::class.java)!!
                }

                 //liveData.postValue(propertyItems)
                try{
                    val savePropertyDao = db.savePropertyDao()
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