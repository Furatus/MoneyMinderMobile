package com.example.moneymindermobile.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.AcceptInvitationMutation
import com.example.AddUserExpenseMutation
import com.example.CreateGroupMutation
import com.example.CreateUserMutation
import com.example.CurrentUserQuery
import com.example.ExpenseJustificationMutation
import com.example.GetGroupByIdQuery
import com.example.GetUserDetailsByIdQuery
import com.example.GetUsersByUsernameQuery
import com.example.GroupPdfSumUpMutation
import com.example.InviteUserMutation
import com.example.PayDuesToGroupMutation
import com.example.RefuseInvitationMutation
import com.example.SignInMutation
import com.example.SignOutMutation
import com.example.UploadExpenseJustificationMutation
import com.example.UploadGroupImagePictureMutation
import com.example.UploadProfilePictureMutation
import com.example.UserInfoMutation
import com.example.moneymindermobile.data.api.ApiEndpoints
import com.example.type.KeyValuePairOfGuidAndNullableOfDecimalInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.io.InputStream

class MainViewModel(
    public val apolloClient: ApolloClient, private val okHttpClient: OkHttpClient
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

    // GetUsersByUsername request
    private val _getUsersByUsernameResponse: MutableStateFlow<GetUsersByUsernameQuery.Data?> =
        MutableStateFlow(null)
    val getUsersByUsernameResponse: StateFlow<GetUsersByUsernameQuery.Data?> =
        _getUsersByUsernameResponse
    private val _isGetUsersByUsernameLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isGetUsersByUsernameLoading: StateFlow<Boolean> = _isGetUsersByUsernameLoading

    // uploadProfilePicture request
    private val _uploadProfilePictureResponse: MutableStateFlow<UploadProfilePictureMutation.Data?> =
        MutableStateFlow(null)
    val uploadProfilePictureResponse = _uploadProfilePictureResponse

    //AddUserExpense request
    private val _addUserExpenseResponse: MutableStateFlow<AddUserExpenseMutation.Data?> =
        MutableStateFlow(null)
    val addUserExpenseResponse: StateFlow<AddUserExpenseMutation.Data?> = _addUserExpenseResponse

    //Upload image group
    private val _uploadGroupPictureResponse: MutableStateFlow<UploadGroupImagePictureMutation.Data?> =
        MutableStateFlow(null)
    val uploadGroupImagePictureMutation = _uploadGroupPictureResponse

    private val _expenseJustificationArray = MutableStateFlow<ByteArray?>(null)
    var expenseJustificationArray: StateFlow<ByteArray?> = _expenseJustificationArray

    private val _userInfoResponse: MutableStateFlow<ByteArray?> = MutableStateFlow(null)
    val userInfoResponse = _userInfoResponse

    private val _groupPdfSumUpResponse: MutableStateFlow<ByteArray?> = MutableStateFlow(null)
    val groupPdfSumUpResponse = _groupPdfSumUpResponse


    fun refreshGraphQlError() {
        viewModelScope.launch {
            _graphQlError.value = emptyList()
        }
    }

    fun handleInvitation(groupId: String, accept: Boolean) {
        viewModelScope.launch {
            println("${if (accept) "accepting" else "rejecting"} invitation to group $groupId")
            _isLoading.value = true
            try {
                if (accept) {
                    val response =
                        apolloClient.mutation(AcceptInvitationMutation(groupId)).execute()
                    _graphQlError.value = response.errors
                } else {
                    val response =
                        apolloClient.mutation(RefuseInvitationMutation(groupId)).execute()
                    _graphQlError.value = response.errors
                }
            } catch (e: ApolloException) {
                println(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun GetPaymentUrl(groupId: String): String {
        var result = ""
        println("starting payment for group $groupId")
        _isLoading.value = false
        try {
            val response =
                apolloClient.mutation(PayDuesToGroupMutation(groupId = groupId)).execute().also {
                    println(it)
                }
            _graphQlError.value = response.errors
            result = response.data?.payDuesToGroup ?: ""
        } catch (e: ApolloException) {
            println(e)
        } finally {
            _isLoading.value = false
        }
        return result
    }

    public fun OpenPaymentUrlIfNeeded(groupId: String, context: Context) {
        viewModelScope.launch {
            println("calling payment for group $groupId")
            val paymentUrlData = GetPaymentUrl(groupId)
            println("payment url: $paymentUrlData")
            if (paymentUrlData.isNotEmpty()) {
                try {
                    val paymentUrl = Uri.parse(paymentUrlData)
                    val intent = Intent(Intent.ACTION_VIEW, paymentUrl)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                } catch (e: Exception) {
                    println(e)
                }
            }
        }
    }

    fun inviteUser(groupId: String, userId: String) {
        viewModelScope.launch {
            println("inviting user $userId to group $groupId")
            _isLoading.value = true
            try {
                val response =
                    apolloClient.mutation(InviteUserMutation(groupId = groupId, userId = userId))
                        .execute()
                _graphQlError.value = response.errors
            } catch (e: ApolloException) {
                println(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getUsersByUsername(value: String) {
        viewModelScope.launch {
            println("getting all users containing $value in their usernames")
            _isGetUsersByUsernameLoading.value = true
            try {
                val response = apolloClient.query(GetUsersByUsernameQuery(value)).execute()
                response.data.let {
                    _getUsersByUsernameResponse.value = it
                }
                _graphQlError.value = response.errors
            } catch (e: ApolloException) {
                println(e)
            } finally {
                _isGetUsersByUsernameLoading.value = false
            }
        }
    }

    fun createGroup(name: String, description: String) {
        viewModelScope.launch {
            println("creating group")
            _isLoading.value = true
            try {
                val response = apolloClient.mutation(
                    CreateGroupMutation(
                        name = name,
                        description = description
                    )
                ).execute()
                response.data.let {
                    _createGroupResponse.value = it
                }
                _graphQlError.value = response.errors
            } catch (e: ApolloException) {
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

    fun uploadProfilePicture(imageByteArray: ByteArray, username: String) {
        val fileExtension = determineFileExtension(imageByteArray)

        viewModelScope.launch(Dispatchers.IO) {
            println("uploading profile picture")
            _isLoading.value = true
            try {
                // Step 1: Execute the UploadProfilePicture mutation to get the unique upload link
                val uploadLinkResponse =
                    apolloClient.mutation(UploadProfilePictureMutation()).execute()
                var uploadLink: String? = uploadLinkResponse.data?.uploadProfilePicture ?: ""
                uploadLink?.let { Log.d("link", it) }
                uploadLink = uploadLink?.replace("localhost", ApiEndpoints.API_ADDRESS)
                val regex =
                    Regex("^http://[a-zA-Z0-9.\\-]+(:\\d+)?/avatars/[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$")
                if (uploadLink != null && regex.matches(uploadLink)) {

                    val requestBody =
                        MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart(
                            "file",
                            "UserImage_${username}.${fileExtension}",
                            imageByteArray.toRequestBody(
                                "image/${fileExtension}".toMediaTypeOrNull(),
                                0,
                                imageByteArray.size
                            )
                        ).build()

                    val request = Request.Builder().url(uploadLink).post(requestBody).build()

                    okHttpClient.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Log.e("Http-Error", "Erreur lors de la requête : ${e.message}")
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val responseCode = response.code

                            when (responseCode) {
                                200 -> {
                                    Log.d(
                                        "http-response",
                                        "Successfully uploaded picture : ${response.body?.string()}"
                                    )
                                }

                                500 -> {
                                    Log.d(
                                        "http-response",
                                        "internal Error : ${response.body?.string()}"
                                    )
                                }
                            }
                        }
                    })
                }
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

    fun addUserExpense(
        amount: Float,
        description: String,
        groupId: String,
        userAmountsList: List<KeyValuePairOfGuidAndNullableOfDecimalInput>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apolloClient.mutation(
                    AddUserExpenseMutation(
                        amount = amount,
                        description = description,
                        groupid = groupId,
                        userAmountsList = userAmountsList
                    )
                ).execute()
                response.data.let {
                    _addUserExpenseResponse.value = it
                }
                _graphQlError.value = response.errors
            } catch (e: ApolloException) {
                println(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadGroupImagePicture(groupId: String, imageByteArray: ByteArray) {

        val fileExtension = determineFileExtension(imageByteArray)

        viewModelScope.launch(Dispatchers.IO) {
            println("uploading profile picture")
            _isLoading.value = true
            try {
                // Step 1: Execute the UploadProfilePicture mutation to get the unique upload link
                val uploadLinkResponse =
                    apolloClient.mutation(UploadGroupImagePictureMutation(groupId)).execute()
                var uploadLink: String? = uploadLinkResponse.data?.uploadGroupImagePicture ?: ""
                uploadLink?.let { Log.d("link", it) }
                Log.d("resp", uploadLinkResponse.data.toString())
                uploadLink = uploadLink?.replace("localhost", ApiEndpoints.API_ADDRESS)

                val requestBody =
                    MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart(
                        "file",
                        "groupImage_${groupId}.${fileExtension}",
                        imageByteArray.toRequestBody(
                            "image/${fileExtension}".toMediaTypeOrNull(),
                            0,
                            imageByteArray.size
                        )
                    ).build()
                val regex =
                    Regex("^http://[a-zA-Z0-9.\\-]+(:\\d+)?/groupimages/[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$")
                if (uploadLink != null && regex.matches(uploadLink)) {
                    val request = Request.Builder().url(uploadLink).post(requestBody).build()

                    okHttpClient.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Log.e("Http-Error", "Erreur lors de la requête : ${e.message}")
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val responseCode = response.code

                            when (responseCode) {
                                200 -> {
                                    Log.d(
                                        "http-response",
                                        "Successfully uploaded picture : ${response.body?.string()}"
                                    )
                                }

                                500 -> {
                                    Log.d(
                                        "http-response",
                                        "internal Error : ${response.body?.string()}"
                                    )
                                }
                            }
                        }
                    })
                }
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

    fun uploadExpenseJustification(expenseId: String, justificationByteArray: ByteArray) {
        val fileExtension = determineFileExtension(justificationByteArray)

        viewModelScope.launch(Dispatchers.IO) {
            println("uploading profile picture")
            _isLoading.value = true
            try {
                // Step 1: Execute the UploadProfilePicture mutation to get the unique upload link
                val uploadLinkResponse =
                    apolloClient.mutation(UploadExpenseJustificationMutation(expenseId)).execute()
                var uploadLink: String? = uploadLinkResponse.data?.uploadExpenseJustification ?: ""
                uploadLink?.let { Log.d("link", it) }
                Log.d("resp", uploadLinkResponse.data.toString())
                uploadLink = uploadLink?.replace("localhost", ApiEndpoints.API_ADDRESS)

                val mediaType = when (fileExtension) {
                    "jpg", "jpeg", "png" -> "image/${fileExtension}"
                    "pdf" -> "application/pdf"
                    else -> "application/octet-stream"
                }

                Log.d("file_ext", mediaType)
                Log.d("exp_id", expenseId)

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        "filename.${fileExtension}",
                        justificationByteArray.toRequestBody(
                            mediaType.toMediaTypeOrNull(),
                            0,
                            justificationByteArray.size
                        )
                    )
                    .build()

                val regex =
                    Regex("^http://[a-zA-Z0-9.\\-]+(:\\d+)?/justifications/[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$")
                if (uploadLink != null && regex.matches(uploadLink)) {
                    val request = Request.Builder().url(uploadLink).post(requestBody).build()

                    okHttpClient.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Log.e("Http-Error", "Erreur lors de la requête : ${e.message}")
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val responseCode = response.code

                            when (responseCode) {
                                200 -> {
                                    Log.d(
                                        "http-response",
                                        "Successfully uploaded picture : ${response.body?.string()}"
                                    )
                                }

                                500 -> {
                                    Log.d(
                                        "http-response",
                                        "internal Error : ${response.body?.string()}"
                                    )
                                }
                            }
                        }
                    })
                }
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

    fun expenseJustification(expenseId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response =
                    apolloClient.mutation(ExpenseJustificationMutation(expenseId)).execute()
                var justificationLink = response.data?.expenseJustification
                justificationLink?.let { Log.d("link", it) }
                Log.d("resp", response.data.toString())
                justificationLink =
                    justificationLink?.replace("localhost", ApiEndpoints.API_ADDRESS)
                if (justificationLink != null) {
                    val request = Request.Builder().url(justificationLink).build()
                    okHttpClient.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Log.e("Http-Error", "Erreur lors de la requête : ${e.message}")
                        }

                        override fun onResponse(call: Call, response: Response) {
                            response.body?.let { responseBody ->
                                val inputStream: InputStream = responseBody.byteStream()
                                val byteArray = inputStream.use { it.readBytes() }
                                _expenseJustificationArray.value = byteArray
                                responseBody.close()
                            }
                        }
                    })
                }
                _graphQlError.value = response.errors
            } catch (e: ApolloException) {
                println(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut() {
        _signInResponse.value = null
        viewModelScope.launch {
            try {
                apolloClient.mutation(SignOutMutation()).execute()
            } catch (e: ApolloException) {
                println(e)
            }
        }
    }

    fun userInfo() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response =
                    apolloClient.mutation(UserInfoMutation()).execute()
                var userInfoLink = response.data?.userInfo
                userInfoLink?.let { Log.d("link", it) }
                Log.d("resp", response.data.toString())
                userInfoLink =
                    userInfoLink?.replace("localhost", ApiEndpoints.API_ADDRESS)
                if (userInfoLink != null) {
                    val request = Request.Builder().url(userInfoLink).build()
                    okHttpClient.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Log.e("Http-Error", "Erreur lors de la requête : ${e.message}")
                        }

                        override fun onResponse(call: Call, response: Response) {
                            response.body?.let { responseBody ->
                                val inputStream: InputStream = responseBody.byteStream()
                                val byteArray = inputStream.use { it.readBytes() }
                                _userInfoResponse.value = byteArray
                                responseBody.close()
                            }
                        }
                    })
                }
                _graphQlError.value = response.errors
            } catch (e: ApolloException) {
                println(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun groupPdfSumUp(groupId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response =
                    apolloClient.mutation(GroupPdfSumUpMutation(groupId)).execute()
                var userInfoLink = response.data?.groupPdfSumUp
                userInfoLink?.let { Log.d("link", it) }
                Log.d("resp", response.data.toString())
                userInfoLink =
                    userInfoLink?.replace("localhost", ApiEndpoints.API_ADDRESS)
                if (userInfoLink != null) {
                    val request = Request.Builder().url(userInfoLink).build()
                    okHttpClient.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Log.e("Http-Error", "Erreur lors de la requête : ${e.message}")
                        }

                        override fun onResponse(call: Call, response: Response) {
                            response.body?.let { responseBody ->
                                val inputStream: InputStream = responseBody.byteStream()
                                val byteArray = inputStream.use { it.readBytes() }
                                _groupPdfSumUpResponse.value = byteArray
                                responseBody.close()
                            }
                        }
                    })
                }
                _graphQlError.value = response.errors
            } catch (e: ApolloException) {
                println(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun determineFileExtension(bytes: ByteArray): String? {
        return when {
            bytes.size >= 2 && bytes[0] == 0xFF.toByte() && bytes[1] == 0xD8.toByte() -> "jpg"
            bytes.size >= 3 && bytes[0] == 0x89.toByte() && bytes[1] == 0x50.toByte() && bytes[2] == 0x4E.toByte() -> "png"
            bytes.size >= 4 && bytes[0] == 0x25.toByte() && bytes[1] == 0x50.toByte() && bytes[2] == 0x44.toByte() && bytes[3] == 0x46.toByte() -> "pdf"
            else -> null
        }
    }

}