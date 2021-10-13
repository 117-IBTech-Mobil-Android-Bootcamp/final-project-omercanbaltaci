package com.example.finalproject.repository

import com.example.finalproject.Result
import com.example.finalproject.network.WeatherAPI
import com.example.finalproject.network.response.AutocompleteResponse
import com.example.finalproject.network.response.CurrentResponse
import com.example.finalproject.network.response.ForecastResponse
import com.example.finalproject.util.API_KEY

class WeatherRepository(private val api: WeatherAPI) {
    suspend fun getAutocompleteFromRemote(q: String): Result<AutocompleteResponse> {
        val autocompleteResponse = AutocompleteResponse(api.getAutocomplete(API_KEY, q))
        return Result.Success(autocompleteResponse)
    }

    suspend fun getCurrentWeatherFromRemote(q: String): Result<CurrentResponse> {
        val currentResponse = api.getCurrentWeather(API_KEY, q)
        return if (currentResponse != null) {
            Result.Success(currentResponse)
        } else Result.Error("An error occurred.")
    }

    suspend fun getForecastFromRemote(q: String): Result<ForecastResponse> {
        val forecastResponse = api.getForecast(API_KEY, q)
        return if (forecastResponse != null) {
            Result.Success(forecastResponse)
        } else Result.Error("An error occurred.")
    }
}