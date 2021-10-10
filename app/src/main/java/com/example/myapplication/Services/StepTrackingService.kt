package com.example.myapplication.Services

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myapplication.MyApp
import com.example.myapplication.R

class StepTrackingService: Service(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    //this will be called only the first time we create our service
    override fun onCreate() {
        super.onCreate()
        Log.d("Service", "onCreate called")
    }

    //this will be called every time we call startService()
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Service", "onStartCommand called")
        //return super.onStartCommand(intent, flags, startId)

        //this could/SHOULD?? be done in onCreate...
        val notification = NotificationCompat.Builder(this, MyApp.CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon_black)
            .setContentTitle("Example Service")
            .setContentText("Hello World!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            //.setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .build()

        startForeground(1,notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Service", "onDestroy called")
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("Service", "onBind called")
        return null
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        TODO("Not yet implemented")
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
        //nothing to do here
    }


}