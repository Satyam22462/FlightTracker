package com.example.flighttracker20

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RouteStatusActivity : AppCompatActivity() {

    private lateinit var flightRecyclerView: RecyclerView
    private lateinit var saveButton: Button
    private lateinit var averageTextView: TextView
    private lateinit var refreshButton: ImageButton

    private var selectedFlights: List<FlightRecord> = emptyList()
    private var allFlights: List<FlightRecord> = emptyList()

    private lateinit var from: String
    private lateinit var to: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_status)

        flightRecyclerView = findViewById(R.id.flightRecyclerView)
        saveButton = findViewById(R.id.saveSelectedFlightsButton)
        averageTextView = findViewById(R.id.averageTextView)
        refreshButton = findViewById(R.id.refreshButton)

        from = intent.getStringExtra("from_airport")?.trim()?.uppercase() ?: ""
        to = intent.getStringExtra("to_airport")?.trim()?.uppercase() ?: ""

        if (from.isBlank() || to.isBlank()) {
            Toast.makeText(this, "Invalid route information", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        title = "Route: $from ➝ $to"

        loadFlights()

        refreshButton.setOnClickListener {
            loadFlights()
        }

        saveButton.setOnClickListener {
            if (selectedFlights.size < 3) {
                Toast.makeText(this, "Please select at least 3 flights", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        val dao = FlightDatabase.getInstance(this@RouteStatusActivity).flightDao()
                        selectedFlights.forEach { dao.insertFlight(it) }
                    }

                    val avgDuration = selectedFlights.map { it.actualDurationMinutes }.average()
                    val hours = (avgDuration / 60).toInt()
                    val minutes = (avgDuration % 60).toInt()
                    averageTextView.text = "Avg Duration: ${hours}h ${minutes}m"

                    Toast.makeText(
                        this@RouteStatusActivity,
                        "Saved! Avg Time: ${hours}h ${minutes}m",
                        Toast.LENGTH_LONG
                    ).show()

                    flightRecyclerView.postDelayed({ finish() }, 1000)

                } catch (e: Exception) {
                    Toast.makeText(this@RouteStatusActivity, "Failed to save flights", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadFlights() {
        lifecycleScope.launch {
            try {
                val dao = withContext(Dispatchers.IO) {
                    FlightDatabase.getInstance(this@RouteStatusActivity).flightDao()
                }

                allFlights = withContext(Dispatchers.IO) {
                    dao.getAllFlights().filter {
                        it.departureAirport.equals(from, ignoreCase = true) &&
                                it.arrivalAirport.equals(to, ignoreCase = true)
                    }
                }

                if (allFlights.isEmpty()) {
                    averageTextView.text = "Avg Duration: --"
                    Toast.makeText(this@RouteStatusActivity, "No flights found.", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val adapter = FlightCheckboxAdapter(allFlights) { selected ->
                    selectedFlights = selected
                    saveButton.isEnabled = selectedFlights.size >= 3
                }

                flightRecyclerView.layoutManager = LinearLayoutManager(this@RouteStatusActivity)
                flightRecyclerView.adapter = adapter
                saveButton.isEnabled = false

                val avgDuration = allFlights.map { it.actualDurationMinutes }.average()
                val hours = (avgDuration / 60).toInt()
                val minutes = (avgDuration % 60).toInt()
                averageTextView.text = "Avg Duration: ${hours}h ${minutes}m"

            } catch (e: Exception) {
                Log.e("RouteStatus", "Error loading flights: ${e.message}", e)
                Toast.makeText(this@RouteStatusActivity, "Error loading flights", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
