package com.example.moneymindermobile.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymindermobile.data.api.ApiEndpoints
import com.example.moneymindermobile.data.api.entities.SignInResult
import com.example.moneymindermobile.data.api.mutations.signin.SignInResponse
import com.example.moneymindermobile.data.api.mutations.signin.signInMutationBuilder
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MainViewModel(
    private val httpClient: HttpClient,
//    private val repository: Repository
) : ViewModel() {
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun signIn(username: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            println("signing in")
            _isLoading.value = true
            val query: String =
                signInMutationBuilder(
                    username = username,
                    password = password,
                    rememberMe = rememberMe
                )
            println(query)

            try {
                val response: HttpResponse = httpClient.post(ApiEndpoints.GRAPHQL) {
                    setBody(query)
                    contentType(ContentType.Application.Json)
                }
                if (response.status.isSuccess()) {
                    val responseBody: String = response.body()
                    val signInResponse: SignInResponse = Json.decodeFromString(responseBody)
                    val signInSucceeded: Boolean = signInResponse.data.signIn.succeeded
                    _isLoading.value = false
                    println("signIn successfully")
                } else {
                    _isLoading.value = false
                    println("signIn failed")
                }
            } catch (e: Exception) {
                _isLoading.value = false
                println("Error signIn: ${e.message}")
            }
        }
    }
}