package com.example.flighttracker20

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class FlightStatusActivity : AppCompatActivity() {

    private lateinit var statusContainer: LinearLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flight_status)

        statusContainer = findViewById(R.id.statusContainer)
        progressBar = findViewById(R.id.progressBar)

        val flightNumber = intent.getStringExtra("flight_number") ?: ""
        if (flightNumber.isNotEmpty()) {
            fetchFlightStatus(flightNumber)
        } else {
            addFlightInfo("Flight number not provided", "", "❌", true)
        }
    }

    private fun addFlightInfo(label: String, value: String?, emoji: String = "", isTitle: Boolean = false) {
        val tv = TextView(this)
        tv.text = "$emoji $label ${value ?: "N/A"}"
        tv.textSize = if (isTitle) 18f else 16f
        tv.setPadding(0, 8, 0, 0)
        tv.setTextColor(Color.parseColor("#333333"))
        statusContainer.addView(tv)
    }

    private fun fetchFlightStatus(flightNumber: String) {
        progressBar.visibility = View.VISIBLE

        val retrofit = Retrofit.Builder()
            .baseUrl("http://api.aviationstack.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(AviationStackService::class.java)
        val apiKey = "e09bc626f89a7e444a6542ed284cb634"

        val call = service.getFlightStatus(apiKey, flightNumber)

        call.enqueue(object : Callback<AviationStackResponse> {
            override fun onResponse(
                call: Call<AviationStackResponse>,
                response: Response<AviationStackResponse>
            ) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val flight = response.body()?.data?.firstOrNull()
                    if (flight != null) {
                        addFlightInfo("Airline:", flight.airline?.name, "✈️", true)
                        addFlightInfo("Flight:", flight.flight?.number, "🔢")
                        addFlightInfo("Status:", flight.flightStatus, "🟡")
                        addFlightInfo("", "")

                        addFlightInfo("Departure:", "", "🛫", true)
                        addFlightInfo("Airport:", flight.departure?.airport)
                        addFlightInfo("Scheduled:", flight.departure?.scheduled)
                        addFlightInfo("", "")

                        addFlightInfo("Arrival:", "", "🛬", true)
                        addFlightInfo("Airport:", flight.arrival?.airport)
                        addFlightInfo("Scheduled:", flight.arrival?.scheduled)
                        addFlightInfo("", "")

                        if (flight.live != null) {
                            addFlightInfo("Live Position:", "(${flight.live.latitude}, ${flight.live.longitude})", "📍")
                            addFlightInfo("Updated:", flight.live.updated)
                        } else {
                            addFlightInfo("Live Tracking:", "Not available", "❌")
                        }

                        val from = flight.departure?.airport ?: return
                        val to = flight.arrival?.airport ?: return

                        addFlightInfo("","")
                        addFlightInfo("Recommendation:", "", "📈", true)

                        lifecycleScope.launch {
                            val dao = FlightDatabase.getInstance(this@FlightStatusActivity).flightDao()
                            val avg = dao.getAverageFlightTime(from, to)

                            if (avg != null) {
                                addFlightInfo(
                                    "Based on past week’s flights from $from to $to:",
                                    "⏱️ Average time: ${avg.toInt()} minutes"
                                )
                            } else {
                                addFlightInfo("No historical data available", "Try again tomorrow!")
                            }
                        }

                    } else {
                        addFlightInfo("No flight data found", "", "❌", true)
                    }
                } else {
                    addFlightInfo("Error:", response.message(), "⚠️", true)
                }
            }

            override fun onFailure(call: Call<AviationStackResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                addFlightInfo("Failed to get data:", t.localizedMessage, "❌", true)
            }
        })
    }
}
