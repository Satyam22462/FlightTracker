package com.example.flighttracker20

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AviationStackService {

    @GET("flights")
    fun getFlightStatus(
        @Query("access_key") accessKey: String,
        @Query("flight_iata") flightIata: String
    ): Call<AviationStackResponse>

    @GET("flights")
    suspend fun getFlights(
        @Query("access_key") accessKey: String
    ): AviationStackResponse
}
