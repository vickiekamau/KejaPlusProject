package com.kejaplus.application.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kejaplus.Model.SaveProperty
import com.example.kejaplus.Model.SavePropertyDao
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(entities = [SaveProperty::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun savePropertyDao(): SavePropertyDao
    companion object {
        private const val name = "kejaplus_db"

        /**init {
        System.loadLibrary("kejaplus")
        }*/

        /**private external fun getApiKey(id: Int): String

        @Volatile
        private var instance: AppDatabase? = null

        fun getDB(context: Context): AppDatabase {
        if (instance == null) {
        synchronized(this) {
        val encryptFactory =
        SupportFactory(SQLiteDatabase.getBytes(getApiKey(1).toCharArray()))

        instance = Room.databaseBuilder(
        context.applicationContext, AppDatabase::class.java,
        name
        )
        .openHelperFactory(encryptFactory).fallbackToDestructiveMigration().build()
        }
        }
        return instance!!
        }

        val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "database-name"
        ).build()
        }*/
        @Volatile
        private var instance: AppDatabase? = null
        fun getDB(context: Context): AppDatabase {
            instance = Room.databaseBuilder(
                context.applicationContext, AppDatabase::class.java,
                name
            ).allowMainThreadQueries()
                .build()
            return instance!!
        }

    }

}
