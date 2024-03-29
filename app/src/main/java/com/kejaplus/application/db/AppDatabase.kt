package com.kejaplus.application.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kejaplus.Model.SaveProperty
import com.example.kejaplus.Model.SavePropertyDao
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(entities = [SaveProperty::class], version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun savePropertyDao(): SavePropertyDao
    companion object {
        private const val name = "KejaPlus_database"


        @Volatile
        private var instance: AppDatabase? = null
        fun getDB(context: Context): AppDatabase {
            instance = Room.databaseBuilder(
                context.applicationContext, AppDatabase::class.java,
                name
            ).allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
            return instance!!
        }

    }

}
