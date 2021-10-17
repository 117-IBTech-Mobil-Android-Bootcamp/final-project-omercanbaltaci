package com.example.finalproject.ui.weatherapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.Result
import com.example.finalproject.network.response.CurrentResponse
import com.example.finalproject.repository.WeatherRepository
import com.example.finalproject.ui.weatherapp.model.CurrentWeatherViewStateModel
import com.example.finalproject.ui.weatherapp.model.ForecastViewStateModel
import com.example.finalproject.ui.weatherapp.model.MainViewStateModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {
    val onAutocompleteFetched = MutableLiveData<MainViewStateModel>()
    val onCurrentWeatherFetched = MutableLiveData<CurrentWeatherViewStateModel>()
    val onForecastFetched = MutableLiveData<ForecastViewStateModel>()
    val onAutocompleteError = MutableLiveData<Unit>()
    val onCurrentWeatherError = MutableLiveData<Unit>()
    val onForecastError = MutableLiveData<Unit>()
    val onSingleResultFetched = MutableLiveData<Boolean>()
    val onResultDeleteFetched = MutableLiveData<Int>()
    val onNewCurrentWeatherFetched = MutableLiveData<Unit>()

    fun prepareAutocomplete(q: String) {
        viewModelScope.launch {
            when (val remoteResponse = weatherRepository.getAutocompleteFromRemote(q)) {
                is Result.Success -> {
                    onAutocompleteFetched.value = MainViewStateModel(remoteResponse.data!!)
                }
                is Result.Error -> onAutocompleteError.value = Unit
            }
        }
    }

    fun prepareCurrentWeather(q: String) {
        viewModelScope.launch {
            when (val currentWeatherResponse = weatherRepository.getCurrentWeatherFromRemote(q)) {
                is Result.Success -> {
                    onCurrentWeatherFetched.value =
                        CurrentWeatherViewStateModel(currentWeatherResponse.data!!)
                }
                is Result.Error -> onCurrentWeatherError.value = Unit
            }
        }
    }

    fun prepareForecast(q: String) {
        viewModelScope.launch {
            when (val forecastResponse = weatherRepository.getForecastFromRemote(q)) {
                is Result.Success -> {
                    onForecastFetched.value = ForecastViewStateModel(forecastResponse.data!!)
                }
                is Result.Error -> onForecastError.value = Unit
            }
        }
    }

    fun prepareResult(name: String, region: String, fullName: String) {
        viewModelScope.launch {
            val result = weatherRepository.getSingleResultAsync(name, region)
            if (result != null) {
                onSingleResultFetched.value = true
            } else {
                onSingleResultFetched.value = false
                prepareCurrentWeather(fullName)
            }
        }
    }

    fun deleteResult(name: String, region: String) {
        viewModelScope.launch {
            onResultDeleteFetched.value = weatherRepository.deleteDataAsync(name, region)
        }
    }

    fun prepareCurrentsFromDB() {
        viewModelScope.launch {
            val listFetchedFromDB = weatherRepository.getResultsAsync()
            listFetchedFromDB.resultCurrentList.forEach {
                onCurrentWeatherFetched.value =
                    CurrentWeatherViewStateModel(CurrentResponse(it.location, it.current))
            }

            delay(3000)

            onNewCurrentWeatherFetched.value = Unit
            listFetchedFromDB.resultCurrentList.forEach {
                when (val newCurrentWeatherResponse =
                    weatherRepository.getCurrentWeatherFromRemote(it.location.name)) {
                    is Result.Success -> {
                        onCurrentWeatherFetched.value =
                            CurrentWeatherViewStateModel(newCurrentWeatherResponse.data!!)
                    }
                    is Result.Error -> onCurrentWeatherError.value = Unit
                }
            }
        }
    }
}