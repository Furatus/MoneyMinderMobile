package com.example.moneymindermobile.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.GetGroupByIdQuery
import com.example.moneymindermobile.data.MainViewModel
import com.example.moneymindermobile.data.api.ApiEndpoints

@Composable
fun GroupDetailsScreen(
    groupId: String?,
    viewModel: MainViewModel,
    navController: NavHostController
) {
    val currentUserId =
        viewModel.currentUserResponse.collectAsState().value?.currentUser?.id?.toString()
    val isLoading = viewModel.isLoading.collectAsState()
    val firstFetchDone by rememberSaveable { mutableStateOf(false) }
    val currentGroupByIdState = viewModel.groupByIdResponse.collectAsState()

    LaunchedEffect(firstFetchDone) {
        if (groupId != null)
            viewModel.getGroupById(groupId)
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
            val groupById = currentGroupByIdState.value?.groupById
            if (groupById != null) {
                if (groupById.groupImageUrl.isNullOrEmpty())
                    Icon(
                        imageVector = Icons.Filled.AccountBox,
                        contentDescription = "${groupById.name} default image",
                        modifier = Modifier
                            .size(64.dp)
                    )
                else
                    AsyncImage(
                        model = groupById.groupImageUrl.replace(
                            "localhost",
                            ApiEndpoints.API_ADDRESS
                        ),
                        contentDescription = "${groupById.name} avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                    )
                Card {
                    val currentGroupMembersWithoutCurrentUser =
                        groupById.userGroups.filter { it.user.id != currentUserId }
                    if (currentGroupMembersWithoutCurrentUser.isEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Empty group"
                            )
                            Text(text = "This group has no other group members yet")
                        }
                    } else {
                        Column {
                            Text(text = "Here are the very lucky members of this group:")
                            LazyRowOfMembers(
                                userGroups = currentGroupMembersWithoutCurrentUser,
                                currentUserId = currentUserId
                            ) {
                                println("clicked member with id: ${it.user.id}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LazyRowOfMembers(
    userGroups: List<GetGroupByIdQuery.UserGroup>,
    currentUserId: String?,
    onMemberClicked: (GetGroupByIdQuery.UserGroup) -> Unit
) {
    LazyRow {
        items(userGroups ?: listOf()) { member ->
            GroupMemberCard(member = member, onMemberClicked = onMemberClicked)
        }
    }

}

@Composable
fun GroupMemberCard(
    member: GetGroupByIdQuery.UserGroup,
    onMemberClicked: (GetGroupByIdQuery.UserGroup) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onMemberClicked(member) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (member.user.avatarUrl.isNullOrEmpty())
                Icon(
                    imageVector = Icons.Filled.AccountBox,
                    contentDescription = "${member.user.userName} default image",
                    modifier = Modifier
                        .size(64.dp)
                )
            else
                AsyncImage(
                    model = member.user.avatarUrl.replace(
                        "localhost",
                        ApiEndpoints.API_ADDRESS
                    ),
                    contentDescription = "${member.user.userName} avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                )
            member.user.userName?.let { Text(text = it) }
        }
    }
}
