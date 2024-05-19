@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package com.example.moneymindermobile.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.CurrentUserQuery
import com.example.GetFriendsQuery
import com.example.GetUsersByUsernameQuery
import com.example.moneymindermobile.Routes
import com.example.moneymindermobile.data.MainViewModel
import com.example.moneymindermobile.ui.components.CurrentUserCard
import com.example.moneymindermobile.ui.components.EntityImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel, navController: NavHostController) {
    val firstFetchDone by rememberSaveable { mutableStateOf(false) }
    val currentUserState by viewModel.currentUserResponse.collectAsState()
    val users by viewModel.getUsersByUsernameResponse.collectAsState()
    val getFriendsState by viewModel.getFriendsResponse.collectAsState()
    val graphQlErrors by viewModel.graphQlError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val refreshTrigger = rememberSaveable { mutableStateOf(0) }

    // Properties to create a new group
    var showDialog by remember { mutableStateOf(false) }
    var createGroupName by remember { mutableStateOf("") }
    var createGroupDescription by remember { mutableStateOf("") }
    val newGroupResponse by viewModel.createGroupResponse.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(firstFetchDone, refreshTrigger.value) {
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
                    currentUserQueryData = currentUserState,
                    viewModel = viewModel,
                    navController = navController
                )
                val tabItems = listOf(
                    TabItem(
                        title = "My Groups",
                        unselectedIcon = Icons.Outlined.Face,
                        selectedIcon = Icons.Filled.Face
                    ), TabItem(
                        title = "Invitations",
                        unselectedIcon = Icons.Outlined.MailOutline,
                        selectedIcon = Icons.Filled.Email
                    ), TabItem(
                        title = "Private Messages",
                        unselectedIcon = Icons.Outlined.List,
                        selectedIcon = Icons.Filled.List
                    )
                )

                var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
                val pagerState = rememberPagerState(pageCount = { tabItems.size })

                LaunchedEffect(pagerState.currentPage) {

                    selectedTabIndex = pagerState.currentPage
                }

                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabItems.forEachIndexed { index, item ->
                        Tab(selected = index == selectedTabIndex, onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                            selectedTabIndex = index
                        },
                            text = {
                                Text(text = item.title)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (index == selectedTabIndex) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = "${item.title} Icon"
                                )
                            })
                    }
                }

                HorizontalPager(
                    state = pagerState, modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .weight(1f)
                ) { index ->
                    run {
                        if (index == 0) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Your Groups",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(8.dp)
                                )
                                Box(modifier = Modifier.fillMaxSize()) {
                                    UserGroupList(currentUserQueryData = currentUserState) { clickedGroup ->
                                        navController.navigate("${Routes.GROUP_DETAILS}/${clickedGroup.group.id}")
                                    }
                                    Button(
                                        onClick = { showDialog = true },
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(8.dp)
                                    ) {
                                        Text(text = "Create new group")
                                    }
                                }
                            }
                        }

                        if (index == 1) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Your invitations",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(8.dp)
                                )
                                InvitationsList(
                                    currentUserQueryData = currentUserState,
                                    onAcceptClicked = { group ->
                                        viewModel.handleInvitation(
                                            groupId = group.id.toString(),
                                            accept = true
                                        )
                                        refreshTrigger.value++
                                    },
                                    onRefuseClicked = { group ->
                                        viewModel.handleInvitation(
                                            groupId = group.id.toString(),
                                            accept = false
                                        )
                                        refreshTrigger.value++
                                    }
                                )
                            }
                        }
                        if (index == 2) {
                            viewModel.getFriends()
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Your private messages",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(8.dp)
                                )
                                DisplayFriends(
                                    getFriendsState = getFriendsState,
                                    navController = navController,
                                    users = users,
                                    currentUserId = currentUserState?.currentUser?.id.toString(),
                                    viewModel = viewModel
                                )
                            }
                        }
                    }

                }

            }
            if (showDialog) {
                ModalBottomSheet(onDismissRequest = {
                    createGroupDescription = ""
                    createGroupName = ""
                    showDialog = false
                }) {
                    val keyboardController = LocalSoftwareKeyboardController.current
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
                            showDialog = false
                            if (newGroupId != null) {
                                navController.navigate("${Routes.GROUP_DETAILS}/${newGroupId}")
                            }
                        }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Send,
                                    contentDescription = "Create New Group Button"
                                )
                                Text(text = "Submit")
                            }
                        }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
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
                    Spacer(modifier = Modifier.padding(30.dp))
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayFriends(
    getFriendsState: GetFriendsQuery.Data?,
    navController: NavHostController,
    users: GetUsersByUsernameQuery.Data?,
    currentUserId: String? = null,
    viewModel: MainViewModel
) {
    val friends = getFriendsState?.friends
    var isAddNewMessageFriend by remember { mutableStateOf(false) }
    if (friends != null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(5.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                items(friends) { friend ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                navController.navigate("${Routes.PRIVATE_MESSAGE}/${friend.id}")
                            }
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                EntityImage(
                                    imageLink = friend.avatarUrl,
                                    title = friend.userName
                                )
                                friend.userName?.let {
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
            }
            Button(
                onClick = { isAddNewMessageFriend = true },
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.BottomCenter),
                contentPadding = PaddingValues(1.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Expense"
                )
            }
        }
    }
    if (isAddNewMessageFriend) {
        ModalBottomSheet(onDismissRequest = { isAddNewMessageFriend = false }) {
            val userNameSearchValue = rememberSaveable { mutableStateOf("") }
            LaunchedEffect(userNameSearchValue.value) {
                viewModel.getUsersByUsername(userNameSearchValue.value)
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = userNameSearchValue.value,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Face,
                            contentDescription = "User Name Search Icon"
                        )
                    },
                    onValueChange = { userNameSearchValue.value = it },
                    label = { Text("User Name") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                )
                Text(text = "Add new message to:")
                val usersWithoutCurrentUser = users?.users?.filter { it.id != currentUserId }
                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(usersWithoutCurrentUser ?: listOf()) { user ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    navController.navigate("${Routes.PRIVATE_MESSAGE}/${user.id}")
                                    isAddNewMessageFriend = false
                                }
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    EntityImage(
                                        imageLink = user.avatarUrl,
                                        title = user.userName
                                    )
                                    user.userName?.let {
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
                }
            }
        }
    }
}

@Composable
fun InvitationsList(
    currentUserQueryData: CurrentUserQuery.Data?,
    onAcceptClicked: (CurrentUserQuery.Group1) -> Unit,
    onRefuseClicked: (CurrentUserQuery.Group1) -> Unit,
) {
    val invitations = currentUserQueryData?.currentUser?.invitations
    if (invitations != null) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(invitations) { invitation ->
                InvitationCard(
                    currentInvitation = invitation,
                    onAcceptClicked = onAcceptClicked,
                    onRefuseClicked = onRefuseClicked
                )
            }
        }

    }
}

@Composable
fun InvitationCard(
    currentInvitation: CurrentUserQuery.Invitation,
    onAcceptClicked: (CurrentUserQuery.Group1) -> Unit,
    onRefuseClicked: (CurrentUserQuery.Group1) -> Unit
) {
    val group = currentInvitation.group
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)
            ) {
                EntityImage(imageLink = group.groupImageUrl, title = group.name)
                Text(
                    text = group.name,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Row {
                Button(onClick = { onAcceptClicked(group) }) {
                    Text(text = "Accept")
                }
                Button(onClick = { onRefuseClicked(group) }) {
                    Text(text = "Refuse")
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
    if (userGroups != null) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            items(userGroups) { item: CurrentUserQuery.UserGroup ->
                UserGroupCard(currentUserGroup = item, onGroupClicked)
            }
        }
    }
}

@Composable
fun UserGroupCard(
    currentUserGroup: CurrentUserQuery.UserGroup,
    onGroupClicked: (CurrentUserQuery.UserGroup) -> Unit
) {
    val group = currentUserGroup.group

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable { onGroupClicked(currentUserGroup) }) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)
            ) {
                EntityImage(imageLink = group.groupImageUrl, title = group.name)
                Text(
                    text = group.name,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

