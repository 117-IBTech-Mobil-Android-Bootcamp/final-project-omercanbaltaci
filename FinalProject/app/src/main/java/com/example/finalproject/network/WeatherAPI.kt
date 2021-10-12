package com.example.finalproject.network

import com.example.finalproject.network.response.CurrentResponse
import com.example.finalproject.ui.weatherapp.model.Autocomplete
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("search.json")
    suspend fun getAutocomplete(
        @Query("key") key: String,
        @Query("q") q: String
    ): List<Autocomplete>

    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") key: String,
        @Query("q") q: String,
        @Query("aqi") aqi: String = "no"
    ): CurrentResponse?
}