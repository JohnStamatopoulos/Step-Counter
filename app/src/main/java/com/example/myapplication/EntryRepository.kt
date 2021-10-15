package com.example.myapplication

import androidx.annotation.WorkerThread
import com.example.myapplication.Database.EntryDao
import com.example.myapplication.Model.Entry
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class EntryRepository(private val entryDao: EntryDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allEntries: Flow<List<Entry>> = entryDao.getAllEntries()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(entry: Entry){
        entryDao.insert(entry)
    }
}