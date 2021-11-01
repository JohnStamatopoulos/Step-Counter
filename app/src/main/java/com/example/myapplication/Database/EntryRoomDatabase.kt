package com.example.myapplication.Database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplication.Model.Entry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database
    (entities = arrayOf(Entry::class),
    version = 1,
    exportSchema = false)
abstract class EntryRoomDatabase: RoomDatabase() {

    abstract fun entryDao(): EntryDao

    private class EntryDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            Log.d("Main Database Callback", "onOpen called")
            /*INSTANCE?.let { database ->
                scope.launch {
                    Log.d("Database Callback", "onOpen called")
                }
            }*/
        }

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    Log.d("Database Callback", "onCreate called")
                    val entryDao = database.entryDao()

                    // Delete all content here.
                    entryDao.deleteAll()

                    // Add sample entries.
                    /*
                        var entry = Entry("10/10/2022",1,100)
                        entryDao.insert(entry)
                        entry = Entry("10/10/2021",2,200)
                        entryDao.insert(entry)
                        entry = Entry("10/10/2024",3,300)
                        entryDao.insert(entry)
                        entry = Entry("10/10/2023",4,400)
                        entryDao.insert(entry)
                    */

                    // TODO: Add your own entries!
                }
            }
        }

        /*suspend fun populateDatabase(entryDao: EntryDao) {

        }*/
    }


    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: EntryRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): EntryRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EntryRoomDatabase::class.java,
                    "entry_database"
                ).addCallback(EntryDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}