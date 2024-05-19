package com.example.moneymindermobile.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.GetMessageByOtherIdQuery
import com.example.moneymindermobile.data.MainViewModel
import com.example.moneymindermobile.ui.components.EntityImage

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
                if (otherUserId != null) {
                    DisplayPrivateMessages(
                        privateMessageToOtherUser?.messagesByOtherUserId!!,
                        viewModel = viewModel,
                        otherUserId = otherUserId
                    )
                }
            } else {
                Text(text = "No messages found")
            }
        }
    }
}

@Composable
fun DisplayPrivateMessages(
    messagesByOtherUserId: List<GetMessageByOtherIdQuery.MessagesByOtherUserId>,
    viewModel: MainViewModel,
    otherUserId: String
) {
    val lazyColumnScope = rememberLazyListState()
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyColumn(
            state = lazyColumnScope,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            reverseLayout = true
        ) {
            items(messagesByOtherUserId) { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row {
                        EntityImage(
                            imageLink = message.sender.avatarUrl,
                            title = message.sender.userName
                        )
                        Column() {
                            message.sender.userName?.let {
                                Text(
                                    text = it,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(text = message.content)
                        }
                    }
                }
            }
        }
        LaunchedEffect(messagesByOtherUserId) {
            lazyColumnScope.animateScrollToItem(
                0
            )
        }
        var newMessageValue by rememberSaveable { mutableStateOf("") }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                value = newMessageValue,
                onValueChange = { newMessageValue = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(1f)
            )
            Button(onClick = {
                viewModel.sendPrivateMessage(
                    content = newMessageValue,
                    otherUserId = otherUserId
                )
                newMessageValue = ""
            }) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Send private message button"
                )
            }
        }
    }
}