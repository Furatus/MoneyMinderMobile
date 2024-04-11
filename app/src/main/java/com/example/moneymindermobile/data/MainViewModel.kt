package com.example.moneymindermobile.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.CreateGroupMutation
import com.example.CreateUserMutation
import com.example.CurrentUserQuery
import com.example.GetGroupByIdQuery
import com.example.GetUserDetailsByIdQuery
import com.example.SignInMutation
import com.example.UploadProfilePictureMutation
import com.example.moneymindermobile.data.api.ApiEndpoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class MainViewModel(
    private val apolloClient: ApolloClient, private val okHttpClient: OkHttpClient
//    private val repository: Repository
) : ViewModel() {
    // Global loading state
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Global graphql error
    private val _graphQlError: MutableStateFlow<List<com.apollographql.apollo3.api.Error>?> =
        MutableStateFlow(null)
    val graphQlError: StateFlow<List<com.apollographql.apollo3.api.Error>?> = _graphQlError

    // SignIn response
    private val _signInResponse: MutableStateFlow<SignInMutation.Data?> = MutableStateFlow(null)
    val signInResponse: StateFlow<SignInMutation.Data?> = _signInResponse

    // Register response
    private val _registerResponse: MutableStateFlow<CreateUserMutation.Data?> =
        MutableStateFlow(null)
    val registerResponse: StateFlow<CreateUserMutation.Data?> = _registerResponse

    // GroupById response
    private val _groupByIdResponse: MutableStateFlow<GetGroupByIdQuery.Data?> =
        MutableStateFlow(null)
    val groupByIdResponse: StateFlow<GetGroupByIdQuery.Data?> = _groupByIdResponse

    // UserDetailsById response
    private val _userDetailsById: MutableStateFlow<GetUserDetailsByIdQuery.Data?> =
        MutableStateFlow(null)
    val userDetailsById: StateFlow<GetUserDetailsByIdQuery.Data?> = _userDetailsById

    // CurrentUser response
    private val _currentUserResponse: MutableStateFlow<CurrentUserQuery.Data?> =
        MutableStateFlow(null)
    val currentUserResponse: StateFlow<CurrentUserQuery.Data?> = _currentUserResponse

    // createGroup request
    private val _createGroupResponse: MutableStateFlow<CreateGroupMutation.Data?> =
        MutableStateFlow(null)
    val createGroupResponse: StateFlow<CreateGroupMutation.Data?> = _createGroupResponse

    // uploadProfilePicture request
    private val _uploadProfilePictureResponse: MutableStateFlow<UploadProfilePictureMutation.Data?> =
        MutableStateFlow(null)

    val uploadProfilePictureResponse = _uploadProfilePictureResponse

    fun refreshGraphQlError() {
        viewModelScope.launch {
            _graphQlError.value = emptyList()
        }
    }

    fun createGroup(name: String, description: String){
        viewModelScope.launch {
            println("creating group")
            _isLoading.value = true
            try {
                val response = apolloClient.mutation(CreateGroupMutation(name = name, description = description)).execute()
                response.data.let {
                    _createGroupResponse.value = it
                }
                _graphQlError.value = response.errors
            } catch (e: ApolloException){
                println(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getUserDetailsById(userId: String) {
        viewModelScope.launch {
            println("getting user details by id: $userId")
            _isLoading.value = true
            try {
                val response = apolloClient.query(GetUserDetailsByIdQuery(userId)).execute()
                response.data.let {
                    _userDetailsById.value = it
                }
                _graphQlError.value = response.errors
            } catch (e: ApolloException) {
                println(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getGroupById(groupId: String) {
        viewModelScope.launch {
            println("getting group by id: $groupId")
            _isLoading.value = true
            try {
                val response = apolloClient.query(GetGroupByIdQuery(groupId)).execute()
                response.data.let {
                    _groupByIdResponse.value = it
                }
                _graphQlError.value = response.errors
            } catch (e: ApolloException) {
                println("e")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(username: String, password: String, email: String) {
        viewModelScope.launch {
            println("registering")
            _isLoading.value = true
            try {
                val response = apolloClient.mutation(
                    CreateUserMutation(
                        userName = username, password = password, email = email
                    )
                ).execute()
                response.data.let {
                    _registerResponse.value = it
                }
                _graphQlError.value = response.errors
            } catch (e: ApolloException) {
                println(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signIn(username: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            println("signing in")
            _isLoading.value = true
            try {
                val response =
                    apolloClient.mutation(SignInMutation(username, password, rememberMe)).execute()
                response.data.let {
                    _signInResponse.value = it
                }
                _graphQlError.value = response.errors
            } catch (e: ApolloException) {
                println(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            println("fetching current user")
            _isLoading.value = true
            try {
                val response = apolloClient.query(CurrentUserQuery()).execute()
                response.data.let {
                    _currentUserResponse.value = it
                }
                _graphQlError.value = response.errors
            } catch (e: ApolloException) {
                println(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadProfilePicture(imageFile: File) {
        viewModelScope.launch(Dispatchers.IO) {
            println("uploading profile picture")
            _isLoading.value = true
            try {
                // Step 1: Execute the UploadProfilePicture mutation to get the unique upload link
                val uploadLinkResponse =
                    apolloClient.mutation(UploadProfilePictureMutation()).execute()
                var uploadLink = uploadLinkResponse.data?.uploadProfilePicture
                uploadLink = uploadLink?.replace("localhost", ApiEndpoints.API_ADDRESS)

                val requestBody =
                    MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart(
                        "file",
                        imageFile.name,
                        imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                    ).build()

                val request = Request.Builder().url(uploadLink!!).post(requestBody).build()

                val response = okHttpClient.newCall(request).execute()

                if (!response.isSuccessful) throw IOException("Unexpected code $response")
            } catch (e: ApolloException) {
                println("ApolloException: $e")
            } catch (e: IOException) {
                println("IOException: $e")
            } finally {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        }
    }
}