package com.example.moneymindermobile.data.api.entities

import java.util.Date

data class UserGroup(
    val user: AppUser,
    val group: Group,
    val joinedAt: Date
)