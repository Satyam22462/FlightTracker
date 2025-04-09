package com.example.flighttracker20

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var flightInput: EditText
    private lateinit var fromInput: EditText
    private lateinit var toInput: EditText
    private lateinit var flightSpinner: Spinner
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var modeSwitch: Switch
    private lateinit var showWeb: Button
    private lateinit var showByRoute: Button
    private lateinit var historyButton: ImageButton

    private val sampleFlights = arrayOf("EK215", "BA283", "BA142", "DL100", "AF220")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("flight_prefs", MODE_PRIVATE)
        if (!prefs.getBoolean("sample_inserted", false)) {
            lifecycleScope.launch {
                FlightDatabase.insertSampleFlight(this@MainActivity)
                prefs.edit { putBoolean("sample_inserted", true) }
            }
        }

        val workRequest = PeriodicWorkRequestBuilder<FlightWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "flight_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        flightInput = findViewById(R.id.flightInput)
        fromInput = findViewById(R.id.fromInput)
        toInput = findViewById(R.id.toInput)
        flightSpinner = findViewById(R.id.flightSpinner)
        modeSwitch = findViewById(R.id.modeSwitch)
        showWeb = findViewById(R.id.showWeb)
        showByRoute = findViewById(R.id.showByRoute)
        historyButton = findViewById(R.id.historyButton) // 🔽 Add this ImageButton to your layout

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sampleFlights)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        flightSpinner.adapter = adapter

        flightSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                flightInput.setText(sampleFlights[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        showWeb.setOnClickListener {
            val flight = flightInput.text.toString().trim().uppercase()
            if (flight.isNotEmpty()) {
                val intent = if (modeSwitch.isChecked) {
                    Intent(this, FlightStatusActivity::class.java)
                } else {
                    Intent(this, FlightWebActivity::class.java)
                }
                intent.putExtra("flight_number", flight)
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.enter_valid_flight_warning), Toast.LENGTH_SHORT).show()
            }
        }

        showByRoute.setOnClickListener {
            val from = fromInput.text.toString().trim().uppercase()
            val to = toInput.text.toString().trim().uppercase()
            if (from.isNotEmpty() && to.isNotEmpty()) {
                val intent = Intent(this, RouteStatusActivity::class.java)
                intent.putExtra("from_airport", from)
                intent.putExtra("to_airport", to)
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.enter_both_airports_warning), Toast.LENGTH_SHORT).show()
            }
        }

        // 🔽 Launch full history screen
        historyButton.setOnClickListener {
            startActivity(Intent(this, FlightHistoryActivity::class.java))
        }
    }
}
