package com.example.moneymindermobile.data.api.entities

import java.util.UUID

data class Group(
    val id: UUID,
    val name: String,
    val description: String,
    val image: String?,
    val userGroups: List<UserGroup>,
    val owner: AppUser,
    val receivedGroupMessages: List<GroupMessage>,
    val expenses: List<Expense>,
    val invitations: List<Invitation>,
    val groupImageUrl: String?
)