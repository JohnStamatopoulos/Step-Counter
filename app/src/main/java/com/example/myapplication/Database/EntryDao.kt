package com.example.myapplication.Database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.Model.Entry
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {

    @Query("SELECT * FROM entry_table ORDER BY date ASC")
    fun getAllEntries(): Flow<List<Entry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    //@Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: Entry) : Long

    @Query("DELETE FROM entry_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM entry_table ORDER BY date DESC LIMIT 1")
    fun getMostRecentEntry(): Flow<Entry> /*
        val testEntry = Entry("testDate", 0, -100f)
        return testEntry
    }*/

    @Query("SELECT * FROM entry_table WHERE date=:today")
    fun haveIRunToday(today:String): Flow<Entry?>

}