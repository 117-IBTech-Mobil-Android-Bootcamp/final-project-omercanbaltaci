package com.example.finalproject.repository

import com.example.finalproject.Result
import com.example.finalproject.db.ResultDAO
import com.example.finalproject.network.WeatherAPI
import com.example.finalproject.network.response.AutocompleteResponse
import com.example.finalproject.network.response.CurrentResponse
import com.example.finalproject.network.response.DBCurrentResponse
import com.example.finalproject.network.response.ForecastResponse
import com.example.finalproject.ui.weatherapp.model.ResultCurrent
import com.example.finalproject.util.API_KEY

class WeatherRepository(private val api: WeatherAPI, private val resultDao: ResultDAO) {
    suspend fun getAutocompleteFromRemote(q: String): Result<AutocompleteResponse> {
        val autocompleteResponse = AutocompleteResponse(api.getAutocomplete(API_KEY, q))
        return Result.Success(autocompleteResponse)
    }

    suspend fun getCurrentWeatherFromRemote(q: String): Result<CurrentResponse> {
        val currentResponse = api.getCurrentWeather(API_KEY, q)
        return if (currentResponse != null) {
            insertDataAsync(
                ResultCurrent(
                    currentResponse.location,
                    currentResponse.current,
                    false
                )
            )
            Result.Success(currentResponse)
        } else Result.Error("An error occurred.")
    }

    suspend fun getForecastFromRemote(q: String): Result<ForecastResponse> {
        val forecastResponse = api.getForecast(API_KEY, q)
        return if (forecastResponse != null) {
            Result.Success(forecastResponse)
        } else Result.Error("An error occurred.")
    }

    private suspend fun insertDataAsync(resultCurrent: ResultCurrent) =
        resultDao.insertResult(resultCurrent)

    suspend fun getResultsAsync(): DBCurrentResponse {
        val results = resultDao.fetchResults()
        return DBCurrentResponse(results)
    }

    suspend fun getSingleResultAsync(name: String, region: String): ResultCurrent? {
        return resultDao.fetchSingleResult(name, region)
    }

    suspend fun deleteDataAsync(name: String, region: String): Int {
        return resultDao.deleteResult(name, region)
    }
}