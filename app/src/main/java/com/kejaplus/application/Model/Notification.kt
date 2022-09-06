package com.kejaplus.application.Model

import android.graphics.ColorSpace
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.room.*

@Entity(tableName = "notification")
data class Notification(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Long = 0L,
    @ColumnInfo(name = "notification_title") var title:String="",
    @ColumnInfo(name = "message") var message: String="",
    @ColumnInfo(name = "time_stamp") var timeStamp:String=""
){

    companion object{
        val COMPARATOR = object : DiffUtil.ItemCallback<Notification>(){
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean =
                oldItem == newItem


            override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean =
                oldItem==newItem
        }
    }

}

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notification")
    fun getAll():List<Notification>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notification:  List<Notification>)

    @Query("DELETE FROM notification")
    fun clearNotification()

    //for single user insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotification(notification: Notification):Long


    @Transaction
    fun syncNotification(notification: List<Notification>) {
        //clearProperty()
        insert(notification)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createAll(objects: List<Notification>)

}