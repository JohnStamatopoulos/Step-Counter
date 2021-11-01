package com.example.myapplication.Services

import android.app.Notification
import android.app.PendingIntent
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
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.UI.MainActivity2
import com.example.myapplication.App.MyApp
import com.example.myapplication.Other.Constants.STEPS
import com.example.myapplication.R

class StepTrackingService: LifecycleService(), SensorEventListener {

    var sensorManager: SensorManager? = null

    companion object {
        var simerinaBimataService = MutableLiveData<Int>()
        var simpleRunning = false
    }

    // Get the layouts to use in the custom notification
    var notificationLayout : RemoteViews? = null
    var notificationLayoutExpanded : RemoteViews? = null

    lateinit var globalNotification: Notification
    lateinit var notificationManager: NotificationManagerCompat

    //this will be called only the first time we create our service
    override fun onCreate() {
        super.onCreate()
        Log.d("Service", "onCreate called")

        simpleRunning = true

        notificationManager = NotificationManagerCompat.from(this)

        // Adding a context of SENSOR_SERVICE as Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            // This will give a toast message to the user if there is no sensor in the device
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            // Rate suitable for the user interface
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }

        val notificationIntent = Intent(this, MainActivity2::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 0)

        notificationLayout = RemoteViews(packageName, R.layout.notification_small)
        notificationLayoutExpanded = RemoteViews(packageName, R.layout.notification_large)

        // Apply the layouts to the notification
        globalNotification = NotificationCompat.Builder(this, MyApp.CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon_black)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutExpanded)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .build()

        //egw to ebala
        //notificationManager.notify(1,globalNotification)

        //loadData()
    }

    //this will be called every time we call startService()
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("Service", "onStartCommand called")
        //return super.onStartCommand(intent, flags, startId)
        val previousStepsFromTodayOrZero = intent?.getIntExtra(STEPS,0)
        Log.d("Service", "onStartCommand has lastSteps = ${previousStepsFromTodayOrZero}")
        simerinaBimataService.value = previousStepsFromTodayOrZero!!
        simpleRunning = true
        //loadData()
        startForeground(1,globalNotification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Service", "onDestroy called")
        simpleRunning = false
    }

    override fun onBind(p0: Intent): IBinder? {
        super.onBind(p0)
        Log.d("Service", "onBind called")
        return null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (simpleRunning) {
            // setValue, not postValue cause this need to happen immediately
            simerinaBimataService.value = simerinaBimataService.value?.plus(1)
            Log.d("Service, sensorChanged" , "Σημερινα Βηματα: ${simerinaBimataService.value}")
        }
    }

    private fun updateUI(){
        notificationLayout?.run{
            //setTextViewText(R.id.tvCurrentStepsValue, simpleSteps.toString())
        }
        notificationLayoutExpanded?.run {
            //setTextViewText(R.id.tvCurrentStepsValue, simpleSteps.toString())
        }
        notificationManager.notify(1,globalNotification)
        Log.d("Service", "updateUI called")
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //nothing to do here
        Log.d("Service", "onAccuracyChanged called")
    }
}
