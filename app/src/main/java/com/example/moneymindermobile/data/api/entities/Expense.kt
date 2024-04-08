package com.example.moneymindermobile.data.api.entities

import java.util.Date
import java.util.UUID

data class Expense(
    val id: UUID,
    val group: Group,
    val amount: Float,
    val description: String,
    val createdAt: Date,
    val createdBy: AppUser,
    val userExpenses: List<UserExpense>
)