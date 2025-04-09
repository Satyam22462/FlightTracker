package com.example.flighttracker20

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.Data
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class FlightWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val fromIata = inputData.getString("from") ?: return Result.failure()
        val toIata = inputData.getString("to") ?: return Result.failure()
        val apiKey = "e09bc626f89a7e444a6542ed284cb634"

        return try {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://api.aviationstack.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(AviationStackService::class.java)
            val response = service.getFlights(apiKey)

            val validFlights = response.data?.filter {
                val dep = it.departure?.airport
                val arr = it.arrival?.airport
                val depScheduled = it.departure?.scheduled
                val arrScheduled = it.arrival?.scheduled
                val depIata = it.departure?.airport
                val arrIata = it.arrival?.airport

                dep != null && arr != null &&
                        depScheduled != null && arrScheduled != null &&
                        it.departure?.airport.equals(fromIata, ignoreCase = true) &&
                        it.arrival?.airport.equals(toIata, ignoreCase = true)
            }?.take(3) ?: emptyList()

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = dateFormat.format(Date())

            val dao = FlightDatabase.getInstance(applicationContext).flightDao()

            for (flight in validFlights) {
                val dep = flight.departure
                val arr = flight.arrival
                val flightInfo = flight.flight

                val durationMinutes = calculateMinutes(dep?.scheduled, arr?.scheduled)
                val delay = dep?.delay ?: 0

                val record = FlightRecord(
                    flightNumber = flightInfo?.iata ?: flightInfo?.number ?: "UNKNOWN",
                    departureAirport = dep?.airport ?: "UNKNOWN",
                    arrivalAirport = arr?.airport ?: "UNKNOWN",
                    departureTime = dep?.scheduled ?: "UNKNOWN",
                    arrivalTime = arr?.scheduled ?: "UNKNOWN",
                    delayMinutes = delay,
                    actualDurationMinutes = durationMinutes + delay,
                    date = today
                )

                dao.insertFlight(record)
            }

            Log.d("FlightWorker", "✅ Inserted ${validFlights.size} flights from $fromIata to $toIata for $today")
            Result.success()
        } catch (e: Exception) {
            Log.e("FlightWorker", "❌ API Fetch failed: ${e.message}", e)
            Result.failure()
        }
    }

    private fun calculateMinutes(start: String?, end: String?): Int {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val depTime = start?.let { format.parse(it) }
            val arrTime = end?.let { format.parse(it) }
            if (depTime != null && arrTime != null) {
                val diff = arrTime.time - depTime.time
                (diff / (1000 * 60)).toInt()
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }
}
