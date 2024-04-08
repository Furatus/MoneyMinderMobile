package com.example.moneymindermobile.data.api.entities

import java.util.Date
import java.util.UUID

data class Message(
    val id: UUID,
    val sender: AppUser,
    val receiver: AppUser,
    val content: String,
    val sentAt: Date
)