package com.example.myapplication

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.myapplication.Database.EntryDao
import com.example.myapplication.Model.Entry
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class EntryRepository(private val entryDao: EntryDao) {
//object EntryRepository {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allEntries: LiveData<List<Entry>> = entryDao.getAllEntries().asLiveData()
    val lastEntry: LiveData<Entry> = entryDao.getMostRecentEntry().asLiveData()

    val calendar: Calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    val today = dateFormat.format(calendar.time)
    val haveIRunToday  = entryDao.haveIRunToday(today).asLiveData()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(entry: Entry){
        //var testing1 = entryDao.deleteAll()
        //Log.d("Repository, deleteAll..", "${entry} ${testing1}")
        var testing2 = entryDao.insert(entry)
        Log.d("Repository, insert..", "${entry} ${testing2}")
    }

    fun getTestLastEntryRepo(): LiveData<Entry> {
        return entryDao.getMostRecentEntry().asLiveData()
    }
}