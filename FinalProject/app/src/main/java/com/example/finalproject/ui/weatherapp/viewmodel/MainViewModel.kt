package com.example.finalproject.ui.weatherapp.viewmodel

import androidx.lifecycle.*
import com.example.finalproject.Result
import com.example.finalproject.network.response.CurrentResponse
import com.example.finalproject.repository.WeatherRepository
import com.example.finalproject.ui.weatherapp.model.CurrentWeatherViewStateModel
import com.example.finalproject.ui.weatherapp.model.ForecastViewStateModel
import com.example.finalproject.ui.weatherapp.model.MainViewStateModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {
    val onAutocompleteFetched = MutableLiveData<MainViewStateModel>()
    val onCurrentWeatherFetched = MutableLiveData<CurrentWeatherViewStateModel>()
    val onForecastFetched = MutableLiveData<ForecastViewStateModel>()
    val onAutocompleteError = MutableLiveData<Unit>()
    val onCurrentWeatherError = MutableLiveData<Unit>()
    val onForecastError = MutableLiveData<Unit>()

    private val _onSingleResultFetched = MutableLiveData<Boolean>()
    val onSingleResultFetched: LiveData<Boolean>
        get() = _onSingleResultFetched

    private val _onResultDelete = MutableLiveData<Int>()
    val onResultDelete: LiveData<Int>
        get() = _onResultDelete

    private val _refreshCurrent = weatherRepository
        .refreshLocations()
        .asLiveData()
    val refreshCurrent: LiveData<Result<out Nothing?>>
        get() = _refreshCurrent

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

    private fun prepareCurrentWeather(q: String) {
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
            weatherRepository.getSingleResultAsync(name, region).collect {
                if (it != null) {
                    _onSingleResultFetched.value = true
                } else {
                    _onSingleResultFetched.value = false
                    prepareCurrentWeather(fullName)
                }
            }
        }
    }

    fun deleteResult(name: String, region: String) {
        viewModelScope.launch {
            weatherRepository.deleteDataAsync(name, region).collect {
                _onResultDelete.value = it.data!!
            }
        }
    }

    fun prepareCurrentsFromDB() {
        viewModelScope.launch {
            weatherRepository.getResultsAsync().resultCurrentList.forEach {
                onCurrentWeatherFetched.value =
                    CurrentWeatherViewStateModel(CurrentResponse(it.location, it.current))
            }
        }
    }
}