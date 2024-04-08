package com.example.moneymindermobile.data.api.entities

data class SignInResult(
    val succeeded: Boolean,
    val isLockedOut: Boolean,
    val isNotAllowed: Boolean,
    val requiresTwoFactor: Boolean,
)