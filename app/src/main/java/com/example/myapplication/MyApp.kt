package com.example.myapplication

import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import android.app.Application
import android.content.Context
import com.example.myapplication.Database.EntryRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MyApp : Application() {

    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { EntryRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { EntryRepository(database.entryDao()) }


    companion object {
        const val CHANNEL_ID = "Service Channel Notification"
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            serviceChannel.description = "This is ServiceChannel description"
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(serviceChannel)
        }
    }

}