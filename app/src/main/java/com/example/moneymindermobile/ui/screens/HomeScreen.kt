package com.example.moneymindermobile.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import com.example.moneymindermobile.data.MainViewModel
import com.example.moneymindermobile.ui.components.CurrentUserCard

@Composable
fun HomeScreen(viewModel: MainViewModel, navController: NavHostController) {
    val firstFetchDone by rememberSaveable { mutableStateOf(false) }
    val currentUserState = viewModel.currentUserResponse.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()

    LaunchedEffect(firstFetchDone) {
        viewModel.getCurrentUser()
    }

    Column {
        Text(text = "home")
        if (isLoading.value) {
            CircularProgressIndicator()
        } else {
            CurrentUserCard(currentUserQueryData = currentUserState.value, viewModel = viewModel)
        }
    }
}
