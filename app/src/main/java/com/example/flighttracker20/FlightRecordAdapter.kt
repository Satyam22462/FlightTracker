package com.example.flighttracker20

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FlightRecordAdapter(
    private val flights: List<FlightRecord>
) : RecyclerView.Adapter<FlightRecordAdapter.FlightViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flight_history, parent, false)
        return FlightViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        val flight = flights[position]

        holder.flightNumber.text = "Flight: ${flight.flightNumber}"
        holder.routeText.text = "From ${flight.departureAirport} to ${flight.arrivalAirport}"

        val hours = flight.actualDurationMinutes / 60
        val minutes = flight.actualDurationMinutes % 60
        holder.timeText.text = "Duration: ${hours}h ${minutes}m (${flight.delayMinutes} min delay)"
        holder.dateText.text = "Date: ${flight.date}"
    }

    override fun getItemCount(): Int = flights.size

    class FlightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flightNumber: TextView = itemView.findViewById(R.id.flightNumber)
        val routeText: TextView = itemView.findViewById(R.id.routeText)
        val timeText: TextView = itemView.findViewById(R.id.timeText)
        val dateText: TextView = itemView.findViewById(R.id.dateText)
    }
}
