package com.example.flighttracker20

import com.google.gson.annotations.SerializedName

data class AviationStackResponse(
    val data: List<FlightInfo>?
)

data class FlightInfo(
    val flight: FlightNumber?,
    val departure: Departure?,
    val arrival: Arrival?,
    val live: LiveData?,
    val airline: Airline?,

    @SerializedName("flight_status")
    val flightStatus: String?
)

data class FlightNumber(
    val number: String?,
    val iata: String? // ✅ Added for iata access
)

data class Airline(
    val name: String?
)

data class Departure(
    val airport: String?,
    val scheduled: String?,
    val delay: Int? // ✅ Added for delay access
)

data class Arrival(
    val airport: String?,
    val scheduled: String?
)

data class LiveData(
    val latitude: Double?,
    val longitude: Double?,
    val updated: String?
)
