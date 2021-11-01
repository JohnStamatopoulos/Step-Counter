package com.example.myapplication.UI

import android.content.Intent
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
import com.example.myapplication.App.MyApp
import com.example.myapplication.Model.Entry
import com.example.myapplication.Other.Constants.STEPS
import com.example.myapplication.R
import com.example.myapplication.Services.StepTrackingService
import com.example.myapplication.ViewModels.EntryViewModel
import com.example.myapplication.ViewModels.EntryViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class MainActivity2 : AppCompatActivity(){

    private val entryViewModel: EntryViewModel by viewModels {
        EntryViewModelFactory((application as MyApp).repository)
    }
    private var simerinaBimata = 0
    private var serviceRunning = false

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        Log.d("Main Activity", "onCreate and setContentView called")
        // SOS PREPEI na kaneis KAI observe to livedata, alliws den 8a ginontai ta load/save, ta selecet * kai insert dld!!??
        /*val temp = entryViewModel.latestEntry
        temp.observe(this)  {
            Log.d("MainActivity", "Observing...: ${temp.value}")
        }*/
        entryViewModel.testInit()
        /*entryViewModel.haveIRunToday.observe(this){
            initUI(this.simerinaBimata)
        }*/
        val x = StepTrackingService.simerinaBimataService.observe(this){
            findViewById<TextView>(R.id.tvSteps).text = it.toString()
            if (it!=null){
                simerinaBimata = it
                initUIStepCounters(it)
            }
            Log.d("Main Activity", "Observing StepTrackingService.simerinaBimata with value: ${StepTrackingService.simerinaBimataService.value}")
        }
        initUI()
        // tsekare an exw tre3ei shmera hdh.
        // -An nai, fti3e to UI me autes tis times.
        // -An oxi, ftia3e to UI me bhmata 0.

    }

    /*fun subToObservers(){
        entryViewModel.haveIRunToday.observe(this){

        }
    }*/

    override fun onResume() {
        super.onResume()
        Log.d("Main Activity", "onResume called")
        //initUI()
//        entryViewModel.haveIRunToday.observe(this){
//            initUI(this.simerinaBimata)
//        }
    }

    private fun mResetSteps() {
        Log.d("Main Activity", "mResetSteps called")
        val mStepsTaken = findViewById<TextView>(R.id.tvSteps)
        mStepsTaken.setOnClickListener {
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }
        mStepsTaken.setOnLongClickListener {
            //TODO mhpws einai la8os logikh?
            simerinaBimata = 0
            mStepsTaken.text = 0.toString()
            Log.d("Main Activity", "mResetSteps")
            true
        }
    }

    private fun mSaveData(steps: Int) {
        val calendar: Calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val today = dateFormat.format(calendar.time)
        val id = 12

        val newEntry = Entry(today,id,steps)
        //val newEntry = Entry(today,id,50)

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
        entryViewModel.haveIRunToday.observe(this){ todayEntry->
            //simerinaBimata = myStepsToday IFF I have already run today, else 0
            simerinaBimata = todayEntry?.steps ?: 0
            initUIStepCounters(simerinaBimata)
            val mStepsTaken = findViewById<TextView>(R.id.tvSteps)
            mStepsTaken.text = ("$simerinaBimata")

            // TODO find proper solution to this
            // manually removing the observer, to avoid being called every time the DB changes
            //entryViewModel.haveIRunToday.removeObservers(this)
            Log.d("Main Activity", "mLoadData: Ekana load to: $todayEntry")
        }

    }

    private fun initUI(/*steps: Int*/){
        Log.d("Main Activity", "initUI called")
        initRecyclerView()
        mLoadData()
        mResetSteps()
        makeButtonsUseful()
        // to viewmodel argei na arxikopoih8ei....
        //initUIStepCounters(steps)
    }

    private fun initRecyclerView(){
        Log.d("Main Activity", "initRecyclerView called")
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
    }

    private fun makeButtonsUseful() {
        Log.d("Main Activity", "makeButtonsUseful called")
        findViewById<Button>(R.id.btnStart).setOnClickListener {
            startStepTrackingService()
        }
        findViewById<Button>(R.id.btnStop).also {
            it.setOnClickListener {
                stopStepTrackingService()
            }
            it.isEnabled = false
        }
    }

    private fun initUIStepCounters(currentSteps: Int){
        Log.d("Main Activity", "initUIStepCounters called with currentSteps = $currentSteps")
        findViewById<TextView>(R.id.tvStepsGoal).text = entryViewModel.stepsGoal
        findViewById<ProgressBar>(R.id.progressBar).let { progressBar ->
            val mStepsGoal = entryViewModel.stepsGoal.substring(1).toInt()
            val perCentGoal = ((currentSteps.toFloat() / mStepsGoal.toFloat()) * 100).toInt()
            progressBar.progress = perCentGoal
            if (perCentGoal >= 100){
                findViewById<ImageView>(R.id.imgFire).visibility = View.VISIBLE
            }
            else{
                findViewById<ImageView>(R.id.imgFire).visibility = View.GONE
            }
        }
    }

    private fun startStepTrackingService() {
        if (!serviceRunning) {
            // before starting/creating the service, if I already have run today,
            // send these steps and start counting from there.
            // Else, start from 0.
            // //today's steps is {todayEntry?.steps ?: 0}
            val serviceIntent = Intent(this, StepTrackingService::class.java)
            val x = findViewById<TextView>(R.id.tvSteps).text.toString().toInt()
            serviceIntent.putExtra(STEPS, x)
            //startService(serviceIntent)
            //or this, maybe better
            ContextCompat.startForegroundService(this, serviceIntent)

            serviceRunning = true
            findViewById<Button>(R.id.btnStop).isEnabled = true
            findViewById<Button>(R.id.btnStart).isEnabled = false
            Log.d("Main Activity", "startStepTrackingService called")
        }

    }

    private fun stopStepTrackingService() {
        if (serviceRunning){
            val serviceIntent = Intent(this, StepTrackingService::class.java)
            stopService(serviceIntent)

            // Pare apo to Service posa bhmata etrexa shmera,
            // kai kanta save sthn DB.
            simerinaBimata = StepTrackingService.simerinaBimataService.value ?: 0
            mSaveData(simerinaBimata)

            serviceRunning = false
            findViewById<Button>(R.id.btnStop).isEnabled = false
            findViewById<Button>(R.id.btnStart).isEnabled = true
            Log.d("Main Activity", "stopStepTrackingService called")
        }

    }

}