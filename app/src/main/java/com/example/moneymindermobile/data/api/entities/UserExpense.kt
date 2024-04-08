package com.example.moneymindermobile.data.api.entities

import java.util.Date

data class UserExpense(
    val expense: Expense,
    val user: AppUser,
    val amount: Float,
    val paidAt: Date
)