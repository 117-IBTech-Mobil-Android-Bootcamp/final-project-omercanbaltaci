package com.example.finalproject.ui.weatherapp.model

import com.example.finalproject.network.response.CurrentResponse

data class CurrentWeatherViewStateModel(val currentWeatherResponse: CurrentResponse) {
    fun getCurrentWeather(): Current = currentWeatherResponse.current
    fun getCurrentLocation(): Location = currentWeatherResponse.location
    fun getCurrentCelsiusWithSymbol(): String =
        currentWeatherResponse.current.tempC.toString().plus("°")

    fun getCurrentFahrenheitWithSymbol(): String =
        currentWeatherResponse.current.tempF.toString().plus("°")

    fun getCurrentFeelsLikeCelsius(): String =
        "Feels like ".plus(currentWeatherResponse.current.feelsLikeC.toString()).plus("°")

    fun getCurrentFeelsLikeFahrenheit(): String =
        "Feels like ".plus(currentWeatherResponse.current.feelsLikeF.toString()).plus("°")

    fun getIconURL(): String = currentWeatherResponse.current.condition.icon.substring(2)
}