package com.example.moneymindermobile.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.CurrentUserQuery
import com.example.GetGroupByIdQuery
import com.example.GetUserDetailsByIdQuery
import com.example.moneymindermobile.data.api.ApiEndpoints

@Composable
fun MoneyMinderImage(group : CurrentUserQuery.Group){
    if (group.groupImageUrl.isNullOrEmpty())
        Icon(
            imageVector = Icons.Filled.AccountBox,
            contentDescription = "${group.name} default image",
            modifier = Modifier
                .size(64.dp)
        )
    else
        AsyncImage(
            model = group.groupImageUrl.replace(
                "localhost",
                ApiEndpoints.API_ADDRESS
            ),
            contentDescription = "${group.name} avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
        )
}

@Composable
fun MoneyMinderImage(currentUser: CurrentUserQuery.CurrentUser, onImageClick: () -> Unit){
    if (currentUser.avatarUrl.isNullOrEmpty())
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = "${currentUser.userName} default avatar",
            modifier = Modifier
                .size(64.dp)
                .clickable { onImageClick() }
        )
    else
        AsyncImage(
            model = currentUser.avatarUrl.replace(
                "localhost",
                ApiEndpoints.API_ADDRESS
            ),
            contentDescription = "${currentUser.userName} avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clickable { onImageClick() }
        )
}
@Composable
fun MoneyMinderImage(currentUser: GetGroupByIdQuery.User){
    if (currentUser.avatarUrl.isNullOrEmpty())
        Icon(
            imageVector = Icons.Filled.AccountBox,
            contentDescription = "${currentUser.userName} default image",
            modifier = Modifier
                .size(64.dp)
        )
    else
        AsyncImage(
            model = currentUser.avatarUrl.replace(
                "localhost",
                ApiEndpoints.API_ADDRESS
            ),
            contentDescription = "${currentUser.userName} avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
        )
}

@Composable
fun MoneyMinderImage(currentUser: GetUserDetailsByIdQuery.UserById){
    if (currentUser.avatarUrl.isNullOrEmpty())
        Icon(
            imageVector = Icons.Filled.AccountBox,
            contentDescription = "${currentUser.userName} default image",
            modifier = Modifier
                .size(64.dp)
        )
    else
        AsyncImage(
            model = currentUser.avatarUrl.replace(
                "localhost",
                ApiEndpoints.API_ADDRESS
            ),
            contentDescription = "${currentUser.userName} avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
        )
}