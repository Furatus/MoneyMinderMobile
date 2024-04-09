package com.example.moneymindermobile.data.api.mutations.signin

import kotlinx.serialization.Serializable

@Serializable
data class SignInResponse(
    val data: SignInData,
    val errors: List<Error>?
)

@Serializable
data class SignInData(
    val signIn: SignInResult
)

@Serializable
data class SignInResult(
    val succeeded: Boolean
)

@Serializable
data class Error(
    val message: String
)