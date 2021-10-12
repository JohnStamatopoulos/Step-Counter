package com.example.myapplication.Services

import android.app.Application
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.MyApp
import com.example.myapplication.R

class StepTrackingService: Service(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps  = 0f
    private var previousTotalSteps  = 0f

    // Get the layouts to use in the custom notification
    var notificationLayout : RemoteViews? =null
    var notificationLayoutExpanded : RemoteViews? =null

    //TODO("pending intent flag na mhn anoigei neo activity ka8e fora pou pataw sto notification")
    //TODO("giati den ananewnetai to text sto custom notification layout?")

    //this will be called only the first time we create our service
    override fun onCreate() {
        super.onCreate()
        Log.d("Service", "onCreate called")

        running = true

        // Adding a context of SENSOR_SERVICE aas Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            // This will give a toast message to the user if there is no sensor in the device
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            // Rate suitable for the user interface
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }

        //loadData()
    }

    //this will be called every time we call startService()
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Service", "onStartCommand called")
        //return super.onStartCommand(intent, flags, startId)

        //previousTotalSteps = intent?.getFloatExtra("previousTotalSteps", 0f) ?: 0f

        val notificationIntent = Intent(this,MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, FLAG_CANCEL_CURRENT)

        //edw ftiaxneis to notification, auto douleue prin alla3w se custom UI
        //this could/SHOULD?? be done in onCreate...
        /*
        val notification = NotificationCompat.Builder(this, MyApp.CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon_black)
            .setContentTitle("Example Service")
            .setContentText("Hello World!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .build()
            */

        notificationLayout = RemoteViews(packageName, R.layout.notification_small)
        notificationLayoutExpanded = RemoteViews(packageName, R.layout.notification_large)

        // Apply the layouts to the notification
        val notification = NotificationCompat.Builder(this, MyApp.CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon_black)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutExpanded)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .build()

        //notificationLayout.view

        loadData()
        startForeground(1,notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Service", "onDestroy called")

        saveData()
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("Service", "onBind called")
        return null
    }

    private fun saveData() {

        // Shared Preferences will allow us to save
        // and retrieve data in the form of key,value pair.
        // In this function we will save data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        // og:
        editor.putFloat("key1", previousTotalSteps)
        //editor.putFloat("key1", totalSteps)
        editor.apply()

        Log.d("Service, save Data" , "\n previousTotalSteps = ${previousTotalSteps}" +
                "\n totalSteps = ${totalSteps}")
    }

    private fun loadData() {

        // In this function we will retrieve data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)

        // Log.d is used for debugging purposes
        //Log.d("MainActivity", "$savedNumber")

        previousTotalSteps = savedNumber

        notificationLayout?.setTextViewText(R.id.tvTotalPreviousStepsValue, previousTotalSteps.toString())

        Log.d("Service, load Data" , "\n previousTotalSteps = ${previousTotalSteps}" +
        "\n totalSteps = ${totalSteps}")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (running) {
            totalSteps = event!!.values[0]

            // Current steps are calculated by taking the difference of total steps
            // and previous steps
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            notificationLayout?.let {
                it.setTextViewText(R.id.tvCurrentStepsValue, currentSteps.toString())
                Log.d("Service, sensorChanged" , "\n previousTotalSteps = ${previousTotalSteps}"
                        + "\n totalSteps = ${totalSteps}"
                        + "\n currentSteps = ${currentSteps}")
            }

            // It will show the current steps to the user
            //tv_stepsTaken.text = ("$currentSteps")

            //tvTotalStepsValue.text = totalSteps.toString()
            //tvPreviousTotalStepsValue.text = previousTotalSteps.toString()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //nothing to do here
    }


}