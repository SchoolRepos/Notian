package me.profiluefter.profinote.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    val username: MutableLiveData<String> = MutableLiveData("")
    val password: MutableLiveData<String> = MutableLiveData("")

    fun register() {
        println("${username.value} ${password.value} register")
    }

    fun login() {
        println("${username.value} ${password.value} login")
    }
}