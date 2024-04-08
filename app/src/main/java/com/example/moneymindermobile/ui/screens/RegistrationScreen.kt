package com.example.moneymindermobile.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.moneymindermobile.data.MainViewModel

@Composable
fun RegistrationScreen(viewModel: MainViewModel, navController: NavHostController) {
    Text(text = "registration")
}