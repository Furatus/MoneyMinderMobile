package com.example.moneymindermobile.ui.components

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
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.CurrentUserQuery

@Composable
fun CurrentUserCard(currentUserQueryData: CurrentUserQuery.Data?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                if (currentUserQueryData?.currentUser?.avatarUrl.isNullOrEmpty())
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "${currentUserQueryData?.currentUser?.userName} default avatar",
                        modifier = Modifier.size(64.dp)
                    )
                else
                    AsyncImage(
                        model = currentUserQueryData?.currentUser?.avatarUrl,
                        contentDescription = "${currentUserQueryData?.currentUser?.userName} avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(64.dp)
                            .width(64.dp)
                    )
                currentUserQueryData?.currentUser?.userName?.let {
                    Text(
                        text = it,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CurrentUserCardPreview(){
    val currentUser : CurrentUserQuery.Data? = CurrentUserQuery.Data(CurrentUserQuery.CurrentUser(balance = 0f, id = "000000000", userGroups = emptyList(), userName = "userName", avatarUrl = null))
    currentUser?.currentUser?.userGroups?.plus(CurrentUserQuery.UserGroup(group = CurrentUserQuery.Group(id = "1111111111", name= "group" )))
    CurrentUserCard(currentUserQueryData = currentUser)
}