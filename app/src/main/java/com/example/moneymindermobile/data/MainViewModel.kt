package com.example.moneymindermobile.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.CurrentUserQuery
import com.example.SignInMutation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val apolloClient: ApolloClient
//    private val repository: Repository
) : ViewModel() {
    // Global loading state
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Global graphql error
    private val _graphQlError: MutableStateFlow<List<com.apollographql.apollo3.api.Error>?> = MutableStateFlow(null)
    val graphQlError: StateFlow<List<com.apollographql.apollo3.api.Error>?> = _graphQlError

    // SignIn response
    private val _signInResponse: MutableStateFlow<SignInMutation.Data?> = MutableStateFlow(null)
    val signInResponse : StateFlow<SignInMutation.Data?> = _signInResponse

    // CurrentUser response
    private val _currentUserResponse: MutableStateFlow<CurrentUserQuery.Data?> = MutableStateFlow(null)
    val currentUserResponse: StateFlow<CurrentUserQuery.Data?> = _currentUserResponse

    fun signIn(username: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            println("signing in")
            _isLoading.value = true
            try {
                val response = apolloClient.mutation(SignInMutation(username, password, rememberMe)).execute()
                response.data.let {
                    _signInResponse.value = it
                }
                _graphQlError.value = response.errors
            } catch (e: ApolloException){
                println(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getCurrentUser(){
        viewModelScope.launch {
            println("fetching current user")
            _isLoading.value = true
            try {
                val response = apolloClient.query(CurrentUserQuery()).execute()
                response.data.let {
                    _currentUserResponse.value = it
                }
                _graphQlError.value = response.errors
            } catch (e: ApolloException){
                println(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}