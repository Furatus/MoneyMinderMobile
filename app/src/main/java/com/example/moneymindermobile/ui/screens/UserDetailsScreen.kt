package com.example.moneymindermobile.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.moneymindermobile.data.MainViewModel
import com.example.moneymindermobile.ui.components.MoneyMinderImage

@Composable
fun UserDetailsScreen(
    userId: String?,
    viewModel: MainViewModel,
    navController: NavHostController
){
    val currentUserId =
        viewModel.currentUserResponse.collectAsState().value?.currentUser?.id?.toString()
    val isLoading = viewModel.isLoading.collectAsState()
    val firstFetchDone by rememberSaveable { mutableStateOf(false) }
    val userDetailsById = viewModel.userDetailsById.collectAsState()

    LaunchedEffect(firstFetchDone) {
        if (userId != null)
            viewModel.getUserDetailsById(userId)
    }

    Column {
        if (isLoading.value) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        } else {
            val userDetails = userDetailsById.value?.userById
            if (userDetails != null){
                Column {
                    userDetails.userName?.let { Text(text = it) }
                    MoneyMinderImage(currentUser = userDetails)
                }
            } else {
                Text(text = "Error getting details for user: $userId")
            }
        }
    }
}