package com.example.myapplication

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.Model.Entry
import com.example.myapplication.Model.Entry.MyComparator
import com.example.myapplication.Services.StepTrackingService
import java.util.*

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnStop = findViewById<Button>(R.id.btnStop)

        btnStart.setOnClickListener {
            startStepTrackingService()
        }
        btnStop.setOnClickListener {
            stopStepTrackingService()
        }

        val calendar: Calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date1 = dateFormat.format(calendar.time)
        val date2 = "01/10/2021"
        val date3 = "02/10/2021"
        val date4 = "01/11/2021"
        val date5 = "01/10/2022"
        val date6 = "01/09/2022"
        val date7 = "01/11/2022"
        val date8 = "30/09/2021"
        val date9 = "30/09/2022"
        val date10 = "30/12/2021"
        val date11 = "30/12/2022"

        val dates = arrayListOf<String>()
        dates.add(date1)
        dates.add(date2)
        dates.add(date3)
        dates.add(date4)
        dates.add(date5)
        dates.add(date6)
        dates.add(date7)
        dates.add(date8)
        dates.add(date9)
        dates.add(date10)
        dates.add(date11)

        val entry1 = Entry(1, date1, 100f)
        val entry2 = Entry(2, date2, 200f)
        val entry3 = Entry(3, date3, 100f)
        val entry4 = Entry(4, date4, 200f)
        val entry5 = Entry(5, date5, 100f)
        val entry6 = Entry(6, date6, 200f)
        val entry7 = Entry(7, date7, 100f)
        val entry8 = Entry(8, date8, 200f)
        val entry9 = Entry(9, date9, 100f)
        val entry10 = Entry(10, date10, 200f)
        val entry11 = Entry(11, date11, 200f)

        val entries = arrayListOf<Entry>()
        entries.add(entry1)
        entries.add(entry2)
        entries.add(entry3)
        entries.add(entry4)
        entries.add(entry5)
        entries.add(entry6)
        entries.add(entry7)
        entries.add(entry8)
        entries.add(entry9)
        entries.add(entry10)
        entries.add(entry11)

        //sort(dates,MyComparator)
        val entriesSorted = entries.sortedWith(MyComparator)

        findViewById<TextView>(R.id.tvDates).text = dates.toString()
        findViewById<TextView>(R.id.tvEntriesWithDatesSorted).text = entriesSorted.toString()


        findViewById<TextView>(R.id.tvEntry1).text = entry1.toString()
        findViewById<TextView>(R.id.tvEntry2).text = entry2.toString()

    }


    private fun startStepTrackingService() {
        val serviceIntent = Intent(this, StepTrackingService::class.java)
        //serviceIntent.putExtra("previousTotalSteps", previousTotalSteps)

        //startService(serviceIntent)
        //or this, maybe better
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopStepTrackingService() {
        val serviceIntent = Intent(this, StepTrackingService::class.java)

        stopService(serviceIntent)
    }

    /*override fun onResume() {
        super.onResume()
        running = true

        // Returns the number of steps taken by the user since the last reboot while activated
        // This sensor requires permission android.permission.ACTIVITY_RECOGNITION.
        // So don't forget to add the following permission in AndroidManifest.xml present in manifest folder of the app.
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)


        if (stepSensor == null) {
            // This will give a toast message to the user if there is no sensor in the device
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            // Rate suitable for the user interface
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {

        // Calling the TextView that we made in activity_main.xml
        // by the id given to that TextView
        val tv_stepsTaken = findViewById<TextView>(R.id.tv_stepsTaken)

        val tvTotalStepsValue = findViewById<TextView>(R.id.tvTSValue)
        val tvPreviousTotalStepsValue = findViewById<TextView>(R.id.tvPTSValue)

        if (running) {
            totalSteps = event!!.values[0]

            // Current steps are calculated by taking the difference of total steps
            // and previous steps
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()

            // It will show the current steps to the user
            tv_stepsTaken.text = ("$currentSteps")

            tvTotalStepsValue.text = totalSteps.toString()
            tvPreviousTotalStepsValue.text = previousTotalSteps.toString()
        }
    }

    private fun resetSteps() {
        val tv_stepsTaken = findViewById<TextView>(R.id.tv_stepsTaken)
        tv_stepsTaken.setOnClickListener {
            // This will give a toast message if the user want to reset the steps
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }

        tv_stepsTaken.setOnLongClickListener {

            previousTotalSteps = totalSteps

            // When the user will click long tap on the screen,
            // the steps will be reset to 0
            tv_stepsTaken.text = 0.toString()

            // This will save the data
            saveData()

            true
        }
    }

    private fun saveData() {

        // Shared Preferences will allow us to save
        // and retrieve data in the form of key,value pair.
        // In this function we will save data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    private fun loadData() {

        // In this function we will retrieve data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)

        // Log.d is used for debugging purposes
        Log.d("MainActivity", "$savedNumber")

        previousTotalSteps = savedNumber
    }*/

    /*private fun loadData() {

        // In this function we will retrieve data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)

        // Log.d is used for debugging purposes
        Log.d("MainActivity", "$savedNumber")

        previousTotalSteps = savedNumber
    }*/

}