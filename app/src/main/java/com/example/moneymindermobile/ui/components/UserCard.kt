package com.example.moneymindermobile.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.moneymindermobile.data.api.entities.AppUser
import java.util.UUID

@Composable
fun UserCard(user: AppUser) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { expanded = !expanded }
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                if (user.avatarUrl.isNullOrEmpty())
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "${user.userName} default avatar",
                        modifier = Modifier.size(64.dp)
                    )
                else
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = "${user.userName} avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(64.dp)
                            .width(64.dp)
                    )
                Text(
                    text = user.userName,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Additional information about the user
            if (expanded) {
                Text(
                    text = "Expanded",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}


@Preview
@Composable
fun UserCardPreview() {
    val appUserPreview = AppUser(
        id = UUID.randomUUID(),
        balance = 0.0f,
        userGroups = emptyList(),
        ownedGroups = emptyList(),
        sentMessages = emptyList(),
        receivedMessages = emptyList(),
        sentGroupMessages = emptyList(),
        userExpenses = emptyList(),
        createdExpenses = emptyList(),
        invitations = emptyList(),
        avatarUrl = null,
        userName = "John Doe",
        email = "john.doe@gmail.com"
    )
    UserCard(user = appUserPreview)
}