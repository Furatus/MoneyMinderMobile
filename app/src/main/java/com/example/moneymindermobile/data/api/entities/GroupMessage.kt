package com.example.moneymindermobile.data.api.entities

import java.util.Date
import java.util.UUID

data class GroupMessage(
    val id: UUID,
    val sender: AppUser,
    val group: Group,
    val content: String,
    val sentAt: Date
)