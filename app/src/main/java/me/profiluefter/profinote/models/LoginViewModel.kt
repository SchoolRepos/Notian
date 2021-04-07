package me.profiluefter.profinote.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.profiluefter.profinote.data.remote.CredentialManager
import me.profiluefter.profinote.models.LoginViewModel.LoginState.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val credentialManager: CredentialManager,
    application: Application
) :
    AndroidViewModel(application) {
    val username: MutableLiveData<String> = MutableLiveData("")
    val password: MutableLiveData<String> = MutableLiveData("")

    val state: MutableLiveData<LoginState> = MutableLiveData(INITIAL)
    val errorMessage: MutableLiveData<String> = MutableLiveData("")

    enum class LoginState {
        INITIAL, ERROR, SUCCESS
    }

    private val sharedProperties = PreferenceManager.getDefaultSharedPreferences(getApplication())

    fun register() {
        viewModelScope.launch {
            // Saved because the user could change it during the request
            val username = username.value!!
            val password = password.value!!

            val (success, error) = credentialManager.register(username, password)
            if (success) {
                saveCredentials(username, password)
                state.value = SUCCESS
            } else {
                errorMessage.value = error
                state.value = ERROR
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            // Saved because the user could change it during the request
            val username = username.value!!
            val password = password.value!!

            val (success, error) = credentialManager.login(username, password)
            if (success) {
                saveCredentials(username, password)
                state.value = SUCCESS
            } else {
                errorMessage.value = error
                state.value = ERROR
            }
        }
    }

    private fun saveCredentials(username: String, password: String) {
        sharedProperties.edit().apply {
            putString("username", username)
            putString("password", password)
            apply()
        }
    }
}