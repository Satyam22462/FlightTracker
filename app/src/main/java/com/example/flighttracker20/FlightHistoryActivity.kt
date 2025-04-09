// File: FlightHistoryActivity.kt
package com.example.flighttracker20

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FlightHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flight_history)

        recyclerView = findViewById(R.id.flightHistoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        GlobalScope.launch {
            val dao = FlightDatabase.getInstance(applicationContext).flightDao()
            val flights = withContext(Dispatchers.IO) {
                dao.getAllFlights()
            }

            runOnUiThread {
                recyclerView.adapter = FlightRecordAdapter(flights)
            }
        }
    }
}
