package com.example.myapplication

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapters.EntryListAdapter
import com.example.myapplication.Model.Entry
import com.example.myapplication.ViewModels.EntryViewModel
import com.example.myapplication.ViewModels.EntryViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity2 : AppCompatActivity(), SensorEventListener {

    private val entryViewModel: EntryViewModel by viewModels {
        EntryViewModelFactory((application as MyApp).repository)
    }
    private var sensorManager: SensorManager? = null
    private var running = false
    private var simerinaBimata = 0f

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // SOS PREPEI na kaneis KAI observe to livedata, alliws den 8a ginontai ta load/save, ta selecet * kai insert dld!!??
        /*val temp = entryViewModel.latestEntry
        temp.observe(this)  {
            Log.d("MainActivity", "Observing...: ${temp.value}")
        }*/
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = EntryListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        entryViewModel.allEntries.observe(this) { entries ->
            // Update the cached copy of the entries in the adapter.
            entries?.let {
                adapter.submitList(it)
            }
        }

        mLoadData()
        mResetSteps()
        makeButtonsUseful()
        // Adding a context of SENSOR_SERVICE aas Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private fun makeButtonsUseful() {
        findViewById<Button>(R.id.btnStart).setOnClickListener {
            mSaveData()
        }
        findViewById<Button>(R.id.btnStop).setOnClickListener {
            mLoadData()
        }
    }

    private fun mResetSteps() {
        val mStepsTaken = findViewById<TextView>(R.id.tvDebugSteps)
        mStepsTaken.setOnClickListener {
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }
        mStepsTaken.setOnLongClickListener {
            //TODO mhpws einai la8os logikh?
            simerinaBimata = 0f
            mStepsTaken.text = 0.toString()
            Log.d("Main Activity", "mResetSteps")
            true
        }

    }

    private fun mSaveData() {
        val calendar: Calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val today = dateFormat.format(calendar.time)
        val id = 12
        val newEntry = Entry(today,id,simerinaBimata)
        entryViewModel.insert(newEntry)
        Log.d("Main Activity", "mSaveData: Ekana save to: $newEntry")
    }

    private fun mLoadData() {
        //TODO Λιγη προσοχη εδω, γτ καλειται καθε φορα που αλλαζει η τιμη του todayIRun στην DB
        // αρα και οταν κανουμε save...Χρειαζεται να το αλλαξω?
        // https://developer.android.com/training/data-storage/room/async-queries
        // Note: Observable queries in Room have one important limitation: the query reruns whenever any row in the table is updated,
        // whether or not that row is in the result set.
        // You can ensure that the UI is only notified when the actual query results change
        // by applying the distinctUntilChanged() operator from the corresponding library: Flow, RxJava, or LiveData.
        entryViewModel.todayIRun.observe(this){ todayEntry->
            simerinaBimata = todayEntry?.steps ?: 0f
            val mStepsTaken = findViewById<TextView>(R.id.tvDebugSteps)
            mStepsTaken.text = ("$simerinaBimata")
            Log.d("Main Activity", "mLoadData: Ekana load to: $todayEntry")
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val mStepsTaken = findViewById<TextView>(R.id.tvDebugSteps)
        if (running) {
            simerinaBimata++
            mStepsTaken.text = ("$simerinaBimata")
            Log.d("MainActivity, sensorChanged" , "Σημερινα Βηματα: $simerinaBimata")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // We do not have to write anything in this function for this app
    }

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

}