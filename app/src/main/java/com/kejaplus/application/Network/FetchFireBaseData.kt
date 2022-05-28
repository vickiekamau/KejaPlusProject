package com.kejaplus.application.Network

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kejaplus.Model.SaveProperty
import com.google.firebase.database.*
import com.kejaplus.application.db.AppDatabase
import okhttp3.Interceptor
import okhttp3.OkHttpClient

interface FetchFireBaseData {


    suspend fun getPropety(context: Context): LiveData<List<SaveProperty>> {

        val appContext = context.applicationContext
        val connectivityInterceptor = NetworkConnectivity(appContext)

        val list = MutableLiveData<List<SaveProperty>>()

        if (connectivityInterceptor.isOnline()) {
            val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference("property")
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val propertyItems: List<SaveProperty> = snapshot.children.map { dataSnapshot ->
                        dataSnapshot.getValue(SaveProperty::class.java)!!
                    }
                    list.postValue(propertyItems)
                }

                override fun onCancelled(error: DatabaseError) {
                    //Toast.makeText(.application,error.toString(), Toast.LENGTH_LONG).show()
                }
            })


        } else {
            return list;
        }
        return list;
    }

}
