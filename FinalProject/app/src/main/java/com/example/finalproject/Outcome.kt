package com.example.finalproject

sealed class Outcome<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Outcome<T>(data)
    class Error<T>(data: T? = null, message: String) : Outcome<T>(data, message)
    class Loading<T> : Outcome<T>()
}