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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class WeatherRepository(private val api: WeatherAPI, private val resultDao: ResultDAO) {
    fun getAutocomplete(q: String): Flow<Result<AutocompleteResponse>> = flow {
        val autocompleteResponse = AutocompleteResponse(api.getAutocomplete(API_KEY, q))
        emit(Result.Success(autocompleteResponse))
    }.flowOn(Dispatchers.IO)

    fun getCurrentWeatherFromRemote(q: String): Flow<Result<CurrentResponse>> = flow {
        val currentResponse = api.getCurrentWeather(API_KEY, q)
        if (currentResponse != null) {
            insertDataAsync(
                ResultCurrent(currentResponse.location, currentResponse.current, false)
            ).single()
            emit(Result.Success(currentResponse))
        } else emit(Result.Error("An error occurred."))
    }.flowOn(Dispatchers.IO)

    fun getForecastFromRemote(q: String): Flow<Result<ForecastResponse>> = flow {
        val forecastResponse = api.getForecast(API_KEY, q)
        if (forecastResponse != null) emit(Result.Success(forecastResponse))
        else emit(Result.Error("An error occured"))
    }.flowOn(Dispatchers.IO)

    private fun insertDataAsync(resultCurrent: ResultCurrent) = flow {
        resultDao.insertResult(resultCurrent)
        emit(Result.Success(null))
    }

    fun getResultsAsync(): Flow<DBCurrentResponse> = flow {
        emit(DBCurrentResponse(resultDao.fetchResults()))
    }.flowOn(Dispatchers.IO)

    fun getSingleResultAsync(name: String, region: String): Flow<ResultCurrent?> = flow {
        emit(resultDao.fetchSingleResult(name, region))
    }.flowOn(Dispatchers.IO)

    fun deleteDataAsync(name: String, region: String): Flow<Result<Int>> = flow {
        emit(Result.Success(resultDao.deleteResult(name, region)))
    }

    fun refreshLocations() = flow {
        val weatherRepository = WeatherRepository(api, resultDao)
        emit(Result.Progress(null))
        weatherRepository.getResultsAsync().collect { dbCurrentResponse ->
            dbCurrentResponse.resultCurrentList.forEach {
                getCurrentWeatherFromRemote(it.location.name).single()
            }
        }
        emit(Result.Success(null))
    }.catch { emit(Result.Error("")) }
}