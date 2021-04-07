package me.profiluefter.profinote.data.remote

import android.util.Log
import com.google.gson.Gson
import retrofit2.HttpException
import javax.inject.Inject

class CredentialManager @Inject constructor(private val api: NotesAPI, private val gson: Gson) {
    private val logTag = "CredentialManager"

    suspend fun register(username: String, password: String): Pair<Boolean, String?> =
        catchServerError {
            val userID = api.register(Credentials(username, password, "Notian"))
            Log.i(logTag, "Registered with ID ${userID.userId}!")
            true
        }

    suspend fun login(username: String, password: String): Pair<Boolean, String?> = catchServerError {
        api.getAllLists(username, password)
        Log.i(logTag, "Login-test API call succeeded!")
        true
    }

    private suspend fun catchServerError(block: suspend () -> Boolean): Pair<Boolean, String?> {
        return try {
            block() to null
        } catch (httpException: HttpException) {
            Log.w(logTag, "HTTP error while making server request", httpException)
            val error: String = try {
                val error = gson.fromJson(
                    httpException.response()!!.errorBody()!!.charStream(),
                    Error::class.java
                )
                error.message
            } catch (e: Exception) {
                Log.e(logTag, "Additional error while trying to parse error response", e)
                "${httpException.code()} ${httpException.message()}"
            }
            Log.i(logTag, "Parsed error response: $error")
            false to error
        }
    }
}