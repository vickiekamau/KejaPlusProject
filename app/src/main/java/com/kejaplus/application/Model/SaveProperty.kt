package com.example.kejaplus.Model

import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.firebase.firestore.DocumentId

@Entity(tableName = "property")
class SaveProperty(
    @DocumentId
    @PrimaryKey
    @ColumnInfo(name = "id") var id: String = "",
    @ColumnInfo(name = "property_category") var property_category:String="",
    @ColumnInfo(name = "property_type") var property_type: String="",
    @ColumnInfo(name = "no_bedroom") var no_bedroom:String="",
    @ColumnInfo(name = "location") var location:String="",
    @ColumnInfo(name = "property_name") var property_name: String="",
    @ColumnInfo(name = "condition") var condition: String="",
    @ColumnInfo(name = "price") var price: String="",
    @ColumnInfo(name = "contact_no") var contact_no: String="",
    @ColumnInfo(name = "property_desc") var property_desc: String ="",
    @ColumnInfo(name = "image_url") var image:String="",
    @ColumnInfo(name = "time_stamp") var timeStamp:String=""
    )

@Dao
interface SavePropertyDao {

    @Query("SELECT * FROM property")
    fun getAll(): LiveData<List<SaveProperty>>

    /**@Query("SELECT loan_description FROM loan_products")
    fun getLoanDescription(): LiveData<List<String>>*/


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(saveProperty: List<SaveProperty>)

    @Query("DELETE FROM property")
    fun clearProperty()


    @Transaction
    fun syncProperty(saveProperty: List<SaveProperty>) {
        //clearProperty()
        insert(saveProperty)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createAll(objects: List<SaveProperty>)

    @Query("Select * from property where property_name like '%' || :search || '%' ")
    fun getSearchResult(search:String):LiveData<List<SaveProperty>>
}