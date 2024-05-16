package com.example.moneymindermobile.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.moneymindermobile.Routes
import com.example.moneymindermobile.data.MainViewModel
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState

@Composable
fun StatsScreen(viewModel: MainViewModel, navController: NavHostController) {
    val currentUser = rememberSaveable { mutableStateOf("") }

    // Vérifie si l'expenseId a changé
    if (currentUser.value != viewModel.currentUserResponse.collectAsState().value?.currentUser?.id) {
        currentUser.value = viewModel.currentUserResponse.collectAsState().value?.currentUser?.id as String
        viewModel.userInfo()
    }
    Column {
        Button(onClick = { navController.navigate(Routes.HOME) }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "back to main menu button"
            )
        }
        val documentByteArray = viewModel.userInfoResponse.collectAsState().value
        if (documentByteArray != null) {
            val pdfState = rememberVerticalPdfReaderState(
                resource = ResourceType.Base64(
                    convertByteArrayToBase64(
                        documentByteArray
                    )
                ),
                isZoomEnable = true
            )
            VerticalPDFReader(
                state = pdfState,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
        else {
            Text(text = "Unable to fetch Data")
        }
    }
}