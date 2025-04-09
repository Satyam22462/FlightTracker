package com.example.flighttracker20


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FlightApiService {
    @GET("v1/flights")
    fun getFlightInfo(
        @Query("access_key") accessKey: String,
        @Query("flight_iata") flightNumber: String
    ): Call<AviationStackResponse>
}
