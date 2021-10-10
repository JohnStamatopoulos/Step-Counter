package com.example.myapplication

import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import android.app.Application
import android.content.Context

class MyApp : Application() {

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
                NotificationManager.IMPORTANCE_HIGH
            )
            serviceChannel.description = "This is ServiceChannel description"
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(serviceChannel)
        }
    }

}