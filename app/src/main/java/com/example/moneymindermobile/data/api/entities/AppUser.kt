package com.example.moneymindermobile.data.api.entities

import java.util.UUID

data class AppUser(
    val id: UUID,
    val balance: Float,
    val userGroups: List<UserGroup>,
    val ownedGroups: List<Group>,
    val sentMessages: List<Message>,
    val receivedMessages: List<Message>,
    val sentGroupMessages: List<GroupMessage>,
    val userExpenses: List<UserExpense>,
    val createdExpenses: List<Expense>,
    val invitations: List<Invitation>,
    val avatarUrl: String?,
    val userName: String,
    val email: String
)