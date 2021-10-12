package com.example.finalproject.ui.weatherapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.Result
import com.example.finalproject.repository.WeatherRepository
import com.example.finalproject.ui.weatherapp.model.CurrentWeatherViewStateModel
import com.example.finalproject.ui.weatherapp.model.MainViewStateModel
import kotlinx.coroutines.launch

class MainViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {
    val onAutocompleteFetched = MutableLiveData<MainViewStateModel>()
    val onCurrentWeatherFetched = MutableLiveData<CurrentWeatherViewStateModel>()
    val onAutocompleteError = MutableLiveData<Unit>()
    val onCurrentWeatherError = MutableLiveData<Unit>()

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
}