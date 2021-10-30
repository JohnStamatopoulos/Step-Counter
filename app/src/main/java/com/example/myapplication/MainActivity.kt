package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapters.EntryListAdapter
import com.example.myapplication.Model.Entry
import com.example.myapplication.Services.StepTrackingService
import com.example.myapplication.ViewModels.EntryViewModel
import com.example.myapplication.ViewModels.EntryViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


class MainActivity : AppCompatActivity(), SensorEventListener {

    //TODO() Fix sorting dates from DB [pending]
    //TODO Make your UI [pending]
    //TODO look at GitHub for charts. [pending]
    //TODO fix resetting steps logic [pending]

    //TODO bale leitourgia gia save/load data edw mesa [pending]
    //TODO bale leitourgia gia na stelneis actions sto Service [pending]

    private val entryViewModel: EntryViewModel by viewModels {
        EntryViewModelFactory((application as MyApp).repository)
    }
    private val newEntryActivityRequestCode = 1

    private var sensorManager: SensorManager? = null
    private var running = false
    // Creating a variable which will counts total steps
    // and it has been given the value of 0 float
    private var totalSteps = 0
    // Creating a variable  which counts previous total
    // steps and it has also been given the value of 0 float
    private var previousTotalSteps = 0

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = EntryListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Add an observer on the LiveData returned by getAllEntries.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        entryViewModel.allEntries.observe(this) { entries ->
            // Update the cached copy of the entries in the adapter.
            entries?.let {
                adapter.submitList(it)
            }
        }
        entryViewModel.latestEntry.observe(this) { latestEntry ->
            //grafei null sto TextView otan o pinakas einai empty
            //findViewById<TextView>(R.id.tv_stepsTaken).text = latestEntry?.steps.toString()
            //ara protimame auto:
            if (latestEntry != null) {
                findViewById<TextView>(R.id.tv_stepsTaken).text = latestEntry.steps.toString()
            }
            else {
                findViewById<TextView>(R.id.tv_stepsTaken).text = "-69" //TODO fix that :P
            }
        }
        //TODO make this happen in ViewModel...
        val stepsGoal = entryViewModel.stepsGoal
        findViewById<TextView>(R.id.tvStepsGoal).text = stepsGoal
        findViewById<ProgressBar>(R.id.progressBar).let { seekBar ->
            val currentSteps = findViewById<TextView>(R.id.tvSteps).text.toString().toInt()
            val test = stepsGoal.substring(1).toInt()
            val perCentGoal = ((currentSteps.toFloat() / test.toFloat()) * 100).toInt()
            seekBar.progress = perCentGoal
            if (perCentGoal >= 100){
                findViewById<ImageView>(R.id.imgFire).visibility = View.VISIBLE
            }
        }


        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewEntryActivity::class.java)
            startActivityForResult(intent, newEntryActivityRequestCode)
        }

        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnStop = findViewById<Button>(R.id.btnStop)

        btnStart.setOnClickListener {
            //startStepTrackingService()
        }
        btnStop.setOnClickListener {
            //stopStepTrackingService()
        }

        mLoadData()
        mResetSteps()
        // Adding a context of SENSOR_SERVICE aas Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        //addTestEntries()
    }

    private fun mResetSteps() {
        val mStepsTaken = findViewById<TextView>(R.id.tvDebugSteps)
        mStepsTaken.setOnClickListener {
            // This will give a toast message if the user want to reset the steps
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }

        mStepsTaken.setOnLongClickListener {

            previousTotalSteps = totalSteps
            // This will save the data
            mSaveData()
            Log.d("MainActivity", "mResetSteps, previousTotalSteps:$previousTotalSteps")

            // When the user will click long tap on the screen,
            // the steps will be reset to 0
            mStepsTaken.text = 0.toString()

            true
        }

    }

    private fun mSaveData() {
        val latestEntry = entryViewModel.latestEntry
        Log.d("MainActivity", "mSaveData, replaced latestEntry:${latestEntry.value.toString()}")
        latestEntry.value?.let {
            it.steps = previousTotalSteps
            entryViewModel.insert(it)
        }
        Log.d("MainActivity", "mSaveData, with:${latestEntry.value.toString()}")
    }

    private fun mLoadData() {
        val lastEntry = entryViewModel.latestEntry
        previousTotalSteps = lastEntry.value?.steps ?: 0
        Log.d("MainActivity", "mLoadData, lastEntry:${lastEntry.value.toString()}" +
                " previousTotalSteps:${previousTotalSteps}")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val mStepsTaken = findViewById<TextView>(R.id.tvDebugSteps)
        if (running) {
            totalSteps = event!!.values[0].toInt()

            // Current steps are calculated by taking the difference of total steps
            // and previous steps
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()

            // It will show the current steps to the user
            mStepsTaken.text = ("$currentSteps")
            Log.d("MainActivity, sensorChanged" , "\n previousTotalSteps = ${previousTotalSteps}"
                    + " totalSteps = ${totalSteps}"
                    + " currentSteps = ${currentSteps}")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // We do not have to write anything in this function for this app
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newEntryActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.getStringExtra(NewEntryActivity.EXTRA_REPLY)?.let { reply ->
                val entry = Entry(reply,555, 5555)
                entryViewModel.insert(entry)
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /*@RequiresApi(Build.VERSION_CODES.N)
    private fun addTestEntries() {
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

        val entry1 = Entry(date1,1,100f)
        val entry2 = Entry(date2,2,200f)
        val entry3 = Entry(date3,3,100f)
        val entry4 = Entry(date4,4,200f)
        val entry5 = Entry(date5,5,100f)
        val entry6 = Entry(date6,6,200f)
        val entry7 = Entry(date7,7,100f)
        val entry8 = Entry(date8,8,200f)
        val entry9 = Entry(date9,9,100f)
        val entry10 = Entry(date10, 10, 200f)
        val entry11 = Entry(date11, 11, 200f)

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
    }*/


    override fun onResume() {
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
        editor.putInt("key1", previousTotalSteps)
        editor.apply()
    }

    private fun loadData() {

        // In this function we will retrieve data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getInt("key1", 0)

        // Log.d is used for debugging purposes
        Log.d("MainActivity", "$savedNumber")

        previousTotalSteps = savedNumber
    }

}