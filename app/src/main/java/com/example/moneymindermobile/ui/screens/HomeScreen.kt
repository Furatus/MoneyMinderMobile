package com.example.moneymindermobile.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.CurrentUserQuery
import com.example.moneymindermobile.data.MainViewModel
import com.example.moneymindermobile.data.api.ApiEndpoints
import com.example.moneymindermobile.routes
import com.example.moneymindermobile.ui.components.CurrentUserCard

@Composable
fun HomeScreen(viewModel: MainViewModel, navController: NavHostController) {
    val firstFetchDone by rememberSaveable { mutableStateOf(false) }
    val currentUserState = viewModel.currentUserResponse.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()

    LaunchedEffect(firstFetchDone) {
        viewModel.getCurrentUser()
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
            Column {
                CurrentUserCard(currentUserQueryData = currentUserState.value, viewModel = viewModel)
                UserGroupList(currentUserQueryData = currentUserState.value) {clickedGroup ->
                    navController.navigate("${routes.GROUP_DETAILS}/${clickedGroup.group.id}")
                }
            }
        }
    }
}

@Composable
fun UserGroupList(currentUserQueryData: CurrentUserQuery.Data?, onGroupClicked: (CurrentUserQuery.UserGroup) -> Unit) {
    val userGroups = currentUserQueryData?.currentUser?.userGroups
    userGroups?.forEach { currentUserGroup -> UserGroupCard(currentUserGroup, onGroupClicked) }
}

@Composable
fun UserGroupCard(currentUserGroup: CurrentUserQuery.UserGroup, onGroupClicked: (CurrentUserQuery.UserGroup) -> Unit) {
    val group = currentUserGroup.group

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onGroupClicked(currentUserGroup) }
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
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
                Text(
                    text = group.name,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
