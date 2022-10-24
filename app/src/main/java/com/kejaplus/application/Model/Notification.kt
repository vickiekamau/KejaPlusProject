package com.kejaplus.application.Model

import android.graphics.ColorSpace
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.recyclerview.widget.DiffUtil
import androidx.room.*

@Entity(tableName = "notification")
data class Notification(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Long = 0L,
    @ColumnInfo(name = "notification_title") var title:String="",
    @ColumnInfo(name = "message") var message: String="",
    @ColumnInfo(name = "time_stamp") var timeStamp:String="",
    @ColumnInfo(name = "is_read") var read: Boolean = false
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

    @Query("SELECT * FROM notification ORDER BY id DESC")
    fun getAll():List<Notification>

    @Transaction
    @Query("SELECT count(*) FROM notification WHERE is_read == 0")
    fun count(): LiveData<Int>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notification:  List<Notification>)


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

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg notification: Notification)

    @Transaction
    fun markAsRead(vararg notification: Notification) {
        notification.forEach { it.read = true }
        update(*notification)
    }

    @Transaction
    @Query("UPDATE notification SET is_read = 1")
    fun markAllAsRead()

    @Delete
    fun delete(vararg notification: Notification)

    @Transaction
    @Query("DELETE FROM notification")
    fun clear()

    @Transaction
    @Query("SELECT * FROM notification ORDER BY id DESC")
    fun all(): PagingSource<Int, Notification>
}