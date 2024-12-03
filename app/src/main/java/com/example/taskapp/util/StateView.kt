package com.example.taskapp.util

sealed class StateView<T>(val data: T? = null) {
    class OnLoading<T> : StateView<T>()
    class OnSuccess<T>(data: T) : StateView<T>(data)
    class OnError<T>(val errorMessage: String) : StateView<T>()
}
