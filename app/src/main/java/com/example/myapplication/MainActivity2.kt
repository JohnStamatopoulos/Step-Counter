package com.example.myapplication

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
import com.example.myapplication.Model.Entry
import com.example.myapplication.Services.StepTrackingService
import com.example.myapplication.ViewModels.EntryViewModel
import com.example.myapplication.ViewModels.EntryViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity2 : AppCompatActivity()/*, SensorEventListener*/ {

    private val entryViewModel: EntryViewModel by viewModels {
        EntryViewModelFactory((application as MyApp).repository)
    }
    //private var sensorManager: SensorManager? = null
    //private var running = false
    private var simerinaBimata = 0

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        // SOS PREPEI na kaneis KAI observe to livedata, alliws den 8a ginontai ta load/save, ta selecet * kai insert dld!!??
        /*val temp = entryViewModel.latestEntry
        temp.observe(this)  {
            Log.d("MainActivity", "Observing...: ${temp.value}")
        }*/
        initUI()
        val x = StepTrackingService.simerinaBimata.observe(this){
            findViewById<TextView>(R.id.tvSteps).text = it.toString()
            if (it!=null){
                initUIStepCounters(it)
            }
            else{
                initUIStepCounters(1)
            }
            Log.d("Main Activity", "Observing StepTrackingService.simerinaBimata with value: ${StepTrackingService.simerinaBimata.value}")
        }
        initUIStepCounters(0)
    }

    override fun onResume() {
        super.onResume()

    }

    private fun mResetSteps() {
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

    private fun mSaveData() {
        val calendar: Calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val today = dateFormat.format(calendar.time)
        //val id = Random(Random.nextInt())
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
        entryViewModel.haveIRunToday.observe(this){ todayEntry->
            //simerinaBimata = myStepsToday IFF I have already run today, else 0
            simerinaBimata = todayEntry?.steps ?: 0
            val mStepsTaken = findViewById<TextView>(R.id.tvSteps)
            mStepsTaken.text = ("$simerinaBimata")
            Log.d("Main Activity", "mLoadData: Ekana load to: $todayEntry")
        }
    }

    private fun initUI(){
        initRecyclerView()
        mLoadData()
        mResetSteps()
        makeButtonsUseful()
        //initUIStepCounters(0)
    }

    private fun initRecyclerView(){
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
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            mSaveData()
        }
        findViewById<Button>(R.id.btnLoad).setOnClickListener {
            mLoadData()
        }
        findViewById<Button>(R.id.btnStart).setOnClickListener {
            startStepTrackingService()
        }
        findViewById<Button>(R.id.btnStop).setOnClickListener {
            stopStepTrackingService()
        }
    }

    private fun initUIStepCounters(currentSteps: Int){
        findViewById<TextView>(R.id.tvStepsGoal).text = entryViewModel.stepsGoal
        findViewById<ProgressBar>(R.id.progressBar).let { progressBar ->
            val test = entryViewModel.stepsGoal.substring(1).toInt()
            val perCentGoal = ((currentSteps.toFloat() / test.toFloat()) * 100).toInt()
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
        val serviceIntent = Intent(this, StepTrackingService::class.java)
        val x = findViewById<TextView>(R.id.tvSteps).text.toString().toInt()
        serviceIntent.putExtra("steps", x)
        //startService(serviceIntent)
        //or this, maybe better
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopStepTrackingService() {
        val serviceIntent = Intent(this, StepTrackingService::class.java)

        stopService(serviceIntent)

        simerinaBimata = StepTrackingService.simerinaBimata.value ?: 0
        mSaveData()
    }

}