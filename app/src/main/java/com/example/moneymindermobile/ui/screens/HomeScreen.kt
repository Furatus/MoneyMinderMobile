package com.example.moneymindermobile.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.CurrentUserQuery
import com.example.moneymindermobile.Routes
import com.example.moneymindermobile.data.MainViewModel
import com.example.moneymindermobile.ui.components.CurrentUserCard
import com.example.moneymindermobile.ui.components.MoneyMinderImage

@Composable
fun HomeScreen(viewModel: MainViewModel, navController: NavHostController) {
    val firstFetchDone by rememberSaveable { mutableStateOf(false) }
    val currentUserState by viewModel.currentUserResponse.collectAsState()
    val graphQlErrors by viewModel.graphQlError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Properties to create a new group
    var showDialog by remember { mutableStateOf(false) }
    var createGroupName by remember { mutableStateOf("") }
    var createGroupDescription by remember { mutableStateOf("") }
    val newGroupResponse by viewModel.createGroupResponse.collectAsState()


    LaunchedEffect(firstFetchDone) {
        viewModel.getCurrentUser()
    }

    Column {
        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column {
                CurrentUserCard(
                    currentUserQueryData = currentUserState, viewModel = viewModel
                )
                UserGroupList(currentUserQueryData = currentUserState) { clickedGroup ->
                    navController.navigate("${Routes.GROUP_DETAILS}/${clickedGroup.group.id}")
                }
            }
        }
        Button(onClick = { showDialog = true }) {
            Text(text = "Create new group")
        }

        if (showDialog) {
            Dialog(onDismissRequest = {
                createGroupDescription = ""
                createGroupName = ""
                showDialog = false
            }) {
                val keyboardController = LocalSoftwareKeyboardController.current
                Column {
                    TextField(
                        value = createGroupName,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Group Name Icon"
                            )
                        },
                        onValueChange = { createGroupName = it },
                        label = { Text("Group Name") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    )
                    TextField(
                        value = createGroupDescription,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Group Description Icon"
                            )
                        },
                        onValueChange = { createGroupDescription = it },
                        label = { Text("Group Description") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    )
                    Button(onClick = {
                        viewModel.createGroup(
                            name = createGroupName,
                            description = createGroupDescription
                        )
                        val newGroupId = newGroupResponse?.createGroup?.id
                        if (newGroupId != null) {
                            navController.navigate("${Routes.GROUP_DETAILS}/${newGroupId}")
                            showDialog = false
                        }
                    }) {
                        Text(text = "Submit")
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    ) {
                        if (graphQlErrors != null) {
                            items(graphQlErrors!!) { error ->
                                Text(
                                    text = error.message,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserGroupList(
    currentUserQueryData: CurrentUserQuery.Data?,
    onGroupClicked: (CurrentUserQuery.UserGroup) -> Unit
) {
    val userGroups = currentUserQueryData?.currentUser?.userGroups
    userGroups?.forEach { currentUserGroup -> UserGroupCard(currentUserGroup, onGroupClicked) }
}

@Composable
fun UserGroupCard(
    currentUserGroup: CurrentUserQuery.UserGroup,
    onGroupClicked: (CurrentUserQuery.UserGroup) -> Unit
) {
    val group = currentUserGroup.group

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .clickable { onGroupClicked(currentUserGroup) }) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)
            ) {
                MoneyMinderImage(group = group)
                Text(
                    text = group.name,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
