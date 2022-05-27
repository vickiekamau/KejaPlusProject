package com.example.kejaplus.Model

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "property")
class SaveProperty(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "property_category") val property_category:String,
    @ColumnInfo(name = "property_type") val property_type: String,
    @ColumnInfo(name = "no_bedroom") val no_bedroom:String,
    @ColumnInfo(name = "location") val location:String,
    @ColumnInfo(name = "property_name") val property_name: String,
    @ColumnInfo(name = "condition") val condition: String,
    @ColumnInfo(name = "price") val price: String,
    @ColumnInfo(name = "contact_no") val contact_no: String,
    @ColumnInfo(name = "property_desc") val property_desc: String,
    @ColumnInfo(name = "image_url") val image:String,
    @ColumnInfo(name = "time_stamp") val timeStamp:String
    )
{
    constructor() : this(id = "", property_category = "", property_type = "", no_bedroom ="", location = "", property_name ="", condition="", price="", contact_no="", property_desc="", image="", timeStamp = "") // this constructor is an explicit
    // "empty" constructor, as seen by Java.
}

@Dao
interface SavePropertyDao {

    @Query("SELECT * FROM property")
    fun getAll(): LiveData<List<SaveProperty>>

    /**@Query("SELECT loan_description FROM loan_products")
    fun getLoanDescription(): LiveData<List<String>>*/


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg saveProperty: SaveProperty)

    @Query("DELETE FROM property")
    fun clearProperty()


    @Transaction
    fun syncProperty(vararg saveProperty: SaveProperty) {
        //clearProperty()
        insert(*saveProperty)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createAll(objects: List<SaveProperty>)

    @Query("Select * from property where property_name like '%' || :search || '%' ")
    fun getSearchResult(search:String):LiveData<List<SaveProperty>>
}