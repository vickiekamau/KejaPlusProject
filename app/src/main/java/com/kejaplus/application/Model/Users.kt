package com.kejaplus.application.Model

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.kejaplus.Model.SaveProperty

@Entity(tableName = "users")
class Users (
    @PrimaryKey
    @ColumnInfo(name = "userId") var id: String = "",
    @ColumnInfo(name = "name") var name:String="",
    @ColumnInfo(name = "email") var email: String="",
    @ColumnInfo(name = "password") var password:String=""

    )
@Dao
interface SaveUsersDao {

    @Query("SELECT * FROM users")
    fun getAll(): LiveData<List<SaveProperty>>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(users: List<Users>)

    @Query("DELETE FROM users")
    fun clearUsers()


    @Transaction
    fun syncProperty(users: List<Users>) {
        //clearProperty()
        insert(users)
    }



}