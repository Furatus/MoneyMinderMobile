package com.example.moneymindermobile.data.api.entities

import java.util.Date

data class Invitation(
    val group: Group,
    val user: AppUser,
    val invitedAt: Date
)