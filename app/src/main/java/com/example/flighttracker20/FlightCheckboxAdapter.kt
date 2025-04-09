package com.example.flighttracker20

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FlightCheckboxAdapter(
    private val flights: List<FlightRecord>,
    private val onSelectionChanged: (List<FlightRecord>) -> Unit
) : RecyclerView.Adapter<FlightCheckboxAdapter.FlightViewHolder>() {

    private val selectedFlights = mutableSetOf<FlightRecord>()

    inner class FlightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flightInfoText: TextView = itemView.findViewById(R.id.flightInfoText)
        val flightCheckbox: CheckBox = itemView.findViewById(R.id.flightCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flight_checkbox, parent, false)
        return FlightViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        val flight = flights[position]

        holder.flightInfoText.text = """
            ✈ ${flight.flightNumber ?: "N/A"}
            🕒 ${flight.actualDurationMinutes} min
            📅 ${flight.date ?: "Unknown"}
        """.trimIndent()

        holder.flightCheckbox.setOnCheckedChangeListener(null) // Reset listener to avoid weird behavior
        holder.flightCheckbox.isChecked = selectedFlights.contains(flight)

        holder.flightCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedFlights.add(flight)
            } else {
                selectedFlights.remove(flight)
            }
            onSelectionChanged(selectedFlights.toList())
        }
    }

    override fun getItemCount(): Int = flights.size
}
