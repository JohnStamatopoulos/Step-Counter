package com.example.myapplication.Services

import android.app.Notification
import android.app.PendingIntent
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
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.MyApp
import com.example.myapplication.R

class StepTrackingService: Service(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps  = 0f
    private var previousTotalSteps  = 0f
    private var currentSteps = 0

    // Get the layouts to use in the custom notification
    private var notificationLayout : RemoteViews? = null
    private var notificationLayoutExpanded : RemoteViews? = null

    private lateinit var globalNotification: Notification

    private lateinit var notificationManager: NotificationManagerCompat

    //TODO("pending intent flag na mhn anoigei neo activity ka8e fora pou pataw sto notification")
    //status: FIXED (?)

    //TODO("giati den ananewnetai to text sto custom notification layout?")
    //status: FIXED

    //this will be called only the first time we create our service
    override fun onCreate() {
        super.onCreate()
        Log.d("Service", "onCreate called")

        running = true

        notificationManager = NotificationManagerCompat.from(this)

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

        val notificationIntent = Intent(this,MainActivity::class.java)
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
        Log.d("Service", "onStartCommand called")
        //return super.onStartCommand(intent, flags, startId)

        loadData()
        startForeground(1,globalNotification)

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
        previousTotalSteps = savedNumber
        Log.d("Service, load Data" , "\n previousTotalSteps = ${previousTotalSteps}"
                + "\n totalSteps = ${totalSteps}"
                + "\n currentSteps = ${currentSteps}")
        updateUI()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (running) {
            totalSteps = event!!.values[0]

            // Current steps are calculated by taking the difference of total steps
            // and previous steps
            currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()

            Log.d("Service, sensorChanged" , "\n previousTotalSteps = ${previousTotalSteps}"
                + "\n totalSteps = ${totalSteps}"
                + "\n currentSteps = ${currentSteps}")

            updateUI()
        }
    }

    private fun updateUI(){
        notificationLayout?.run{
            setTextViewText(R.id.tvCurrentStepsValue, currentSteps.toString())
            setTextViewText(R.id.tvTotalPreviousStepsValue, previousTotalSteps.toString())
        }
        notificationLayoutExpanded?.run {
            setTextViewText(R.id.tvCurrentStepsValue, currentSteps.toString())
            setTextViewText(R.id.tvTotalPreviousStepsValue, previousTotalSteps.toString())
        }
        notificationManager.notify(1,globalNotification)
        Log.d("Service", "updateUI called")
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //nothing to do here
    }
}