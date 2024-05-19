package com.example.moneymindermobile.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.moneymindermobile.data.MainViewModel

@Composable
fun PrivateMessageScreen(
    otherUserId: String?,
    viewModel: MainViewModel,
    navController: NavHostController
) {
    val isLoading = viewModel.isLoading.collectAsState()
    val userDetailsByIdState by viewModel.userDetailsById.collectAsState()
    var firstFetchDone by rememberSaveable { mutableStateOf(false) }

    if (!firstFetchDone) {
        if (otherUserId != null) {
            viewModel.getUserDetailsById(otherUserId)
            viewModel.getMessagesByOtherId(otherUserId)
            firstFetchDone = true
        }
    }
    val privateMessageToOtherUser by viewModel.getMessageByOtherIdResponse.collectAsState(null)

    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "private messages to ${userDetailsByIdState?.userById?.userName}",
            fontWeight = FontWeight.Bold
        )
        if (isLoading.value) {
            CircularProgressIndicator()
        } else {
            if (privateMessageToOtherUser?.messagesByOtherUserId?.isNotEmpty() == true) {
                privateMessageToOtherUser?.messagesByOtherUserId?.forEach {
                    Text(text = it.content)
                }
            } else {
                Text(text = "No messages found")
            }
            var newMessageValue by rememberSaveable { mutableStateOf("") }
            TextField(value = newMessageValue, onValueChange = { newMessageValue = it })
            Button(onClick = {
                if (otherUserId != null) {
                    viewModel.sendPrivateMessage(
                        content = newMessageValue,
                        otherUserId = otherUserId
                    )
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Send private message button"
                )
            }
        }
    }
}
