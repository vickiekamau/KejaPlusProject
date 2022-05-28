package com.kejaplus.Repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kejaplus.Model.SaveProperty
import com.google.firebase.database.*
import com.kejaplus.application.db.AppDatabase
import org.apache.commons.lang3.mutable.Mutable


class FetchPropertyDataRepository(application: Application) {

    private val db: AppDatabase = AppDatabase. getDB(application)

    fun getProperty(): LiveData<List<SaveProperty>> {

        val list = MutableLiveData<List<SaveProperty>>()
        val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference("property")


            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val propertyItems: List<SaveProperty> = snapshot.children.map { dataSnapshot ->
                        dataSnapshot.getValue(SaveProperty::class.java)!!
                    }
                    list.postValue(propertyItems)
                    val savePropertyDao = db.savePropertyDao()

                    try{
                        savePropertyDao.clearProperty()
                        savePropertyDao.insert(propertyItems)
                        Log.i("results", propertyItems.toString())
                    }catch (e:Exception){
                        Log.d("Exception", e.message.toString())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //Toast.makeText(.application,error.toString(), Toast.LENGTH_LONG).show()
                }
            })

        return list
    }
}