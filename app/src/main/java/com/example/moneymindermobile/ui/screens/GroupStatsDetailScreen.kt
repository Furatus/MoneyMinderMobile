package com.example.moneymindermobile.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.moneymindermobile.Routes
import com.example.moneymindermobile.data.MainViewModel
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState

@Composable
fun GroupStatsDetailScreen (viewModel: MainViewModel, navController: NavHostController, groupId : String?) {
    val currentUser = rememberSaveable { mutableStateOf("") }
    Column {
        Button(onClick = { navController.navigate("${Routes.GROUP_DETAILS}/${groupId}") }, modifier = Modifier.padding(8.dp), contentPadding = PaddingValues(1.dp)) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "back to main menu button"
            )
        }

        if (groupId != null) {
            if (currentUser.value != groupId) {
                currentUser.value = groupId
                viewModel.groupPdfSumUp(groupId = groupId)
            }
            Column {
                val documentByteArray = viewModel.groupPdfSumUpResponse.collectAsState().value
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
                } else {
                    Text(text = "Unable to fetch Data")
                }
            }
        }
        else {
            Text(text = "Error : no group provided")
        }
    }
}