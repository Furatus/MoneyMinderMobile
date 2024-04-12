package com.example.moneymindermobile.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.GetGroupByIdQuery
import com.example.GetUsersByUsernameQuery
import com.example.moneymindermobile.Routes
import com.example.moneymindermobile.data.MainViewModel
import com.example.moneymindermobile.data.api.ApiEndpoints
import com.example.moneymindermobile.ui.components.EntityImage

@Composable
fun GroupDetailsScreen(
    groupId: String?,
    viewModel: MainViewModel,
    navController: NavHostController
) {
    val currentUserId =
        viewModel.currentUserResponse.collectAsState().value?.currentUser?.id?.toString()
    val isLoading = viewModel.isLoading.collectAsState()
    val isGetUsersByUsernameLoading = viewModel.isGetUsersByUsernameLoading.collectAsState()
    val firstFetchDone by rememberSaveable { mutableStateOf(false) }
    val currentGroupByIdState = viewModel.groupByIdResponse.collectAsState()
    val refreshTrigger = rememberSaveable { mutableStateOf(0)    }

    val getUsersByUsernameTextField = rememberSaveable { mutableStateOf("") }
    val getUsersByUsernameResponse = viewModel.getUsersByUsernameResponse.collectAsState()

    LaunchedEffect(getUsersByUsernameTextField.value) {
        viewModel.getUsersByUsername(getUsersByUsernameTextField.value)
    }

    LaunchedEffect(groupId, refreshTrigger.value) {
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
                val invitations = groupById.invitations
                val isGroupByIdCreatedByCurrentUser = groupById.owner.id == currentUserId
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (groupById.groupImageUrl.isNullOrEmpty()) {
                            Icon(
                                imageVector = Icons.Filled.AccountBox,
                                contentDescription = "${groupById.name} default image",
                                modifier = Modifier
                                    .size(64.dp)
                            )
                        } else {
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
                        }
                        Text(text = groupById.name)
                    }
                    if (isGroupByIdCreatedByCurrentUser)
                        Text(text = "You are the owner of this group")
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
                                Text(text = "Here are the very lucky members of ${if (isGroupByIdCreatedByCurrentUser) "your" else "this"} group:")
                                LazyRowOfMembers(
                                    members = currentGroupMembersWithoutCurrentUser
                                ) {
                                    navController.navigate("${Routes.USER_DETAILS}/${it.user.id}")
                                }
                            }
                        }
                    }
                    TextField(
                        value = getUsersByUsernameTextField.value,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "User By Username Search"
                            )
                        },
                        onValueChange = { getUsersByUsernameTextField.value = it },
                        label = { Text("Username") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    )
                    if (isGetUsersByUsernameLoading.value){
                        CircularProgressIndicator()
                    } else {
                        getUsersByUsernameResponse.value?.users?.let {
                            LazyRowOfUsersToInvite(
                                members = groupById.userGroups,
                                users = it,
                                invited = invitations,
                                refreshTrigger = refreshTrigger
                            ){memberClicked ->
                                if (groupId != null) {
                                    viewModel.inviteUser(groupId = groupId, userId = memberClicked.id.toString())
                                    refreshTrigger.value++
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LazyRowOfUsersToInvite(
    members: List<GetGroupByIdQuery.UserGroup>,
    users: List<GetUsersByUsernameQuery.User>,
    invited: List<GetGroupByIdQuery.Invitation>,
    refreshTrigger: MutableState<Int>,
    onMemberClicked: (GetUsersByUsernameQuery.User) -> Unit
){
    val memberIds = members.map { it.user.id }
    val invitedIds = invited.map { it.user.id }
    val usersToInvite = users.filter { it.id !in memberIds && it.id !in invitedIds }
    LazyRow {
        items(usersToInvite) {user ->
            UserToInviteCard(toInvite = user, onMemberClicked = onMemberClicked)
        }
    }
}

@Composable
fun LazyRowOfMembers(
    members: List<GetGroupByIdQuery.UserGroup>,
    onMemberClicked: (GetGroupByIdQuery.UserGroup) -> Unit
) {
    LazyRow {
        items(members) { member ->
            GroupMemberCard(member = member, onMemberClicked = onMemberClicked)
        }
    }

}

@Composable
fun UserToInviteCard(
    toInvite: GetUsersByUsernameQuery.User,
    onMemberClicked: (GetUsersByUsernameQuery.User) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onMemberClicked(toInvite) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            EntityImage(imageLink = toInvite.avatarUrl, title = toInvite.userName)
            toInvite.userName?.let { Text(text = it) }
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
            EntityImage(imageLink = member.user.avatarUrl, title = member.user.userName)
            member.user.userName?.let { Text(text = it) }
        }
    }
}
