@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class
)

package com.example.moneymindermobile.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.GetGroupByIdQuery
import com.example.GetUsersByUsernameQuery
import com.example.moneymindermobile.Routes
import com.example.moneymindermobile.data.MainViewModel
import com.example.moneymindermobile.data.api.ApiEndpoints
import com.example.moneymindermobile.ui.components.EntityImage
import com.example.moneymindermobile.ui.components.camera.Camera
import com.example.type.KeyValuePairOfGuidAndNullableOfDecimalInput
import kotlinx.coroutines.launch

@Composable
fun GroupDetailsScreen(
    groupId: String?, viewModel: MainViewModel, navController: NavHostController
) {
    val currentUserId =
        viewModel.currentUserResponse.collectAsState().value?.currentUser?.id?.toString()
    val isLoading = viewModel.isLoading.collectAsState()
    val isGetUsersByUsernameLoading = viewModel.isGetUsersByUsernameLoading.collectAsState()
    val firstFetchDone by rememberSaveable { mutableStateOf(false) }
    val currentGroupByIdState = viewModel.groupByIdResponse.collectAsState()
    val refreshTrigger = rememberSaveable { mutableIntStateOf(0) }

    val sheetStateOptions = rememberModalBottomSheetState()
    var addUser by rememberSaveable { mutableStateOf(false) }
    var isOptionsSheetOpen by rememberSaveable { mutableStateOf(false) }

    val sheetStateMembers = rememberModalBottomSheetState()
    var isMembersSheetOpen by rememberSaveable { mutableStateOf(false) }

    var isAddExpenseSheetOpen by rememberSaveable { mutableStateOf(false) }

    val getUsersByUsernameTextField = rememberSaveable { mutableStateOf("") }
    val getUsersByUsernameResponse = viewModel.getUsersByUsernameResponse.collectAsState()

    val scope = rememberCoroutineScope()
    val groupNameTextField = rememberSaveable { mutableStateOf("default name") }
    val groupDescriptionTextField = rememberSaveable { mutableStateOf("default description") }

    val tabItems = listOf(
        TabItem(
            title = "Expenses",
            unselectedIcon = Icons.Outlined.ExitToApp,
            selectedIcon = Icons.Filled.ExitToApp
        ), TabItem(
            title = "Balance",
            unselectedIcon = Icons.Outlined.ArrowForward,
            selectedIcon = Icons.Filled.ArrowForward
        )
    )

    LaunchedEffect(getUsersByUsernameTextField.value) {
        viewModel.getUsersByUsername(getUsersByUsernameTextField.value)
    }

    LaunchedEffect(groupId, refreshTrigger.intValue) {
        if (groupId != null) viewModel.getGroupById(groupId)
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (isLoading.value) {
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        } else {
            val groupById = currentGroupByIdState.value?.groupById
            if (groupById != null) {
                val invitations = groupById.invitations
                val isGroupByIdCreatedByCurrentUser = groupById.owner.id == currentUserId


                if (isOptionsSheetOpen) {
                    ModalBottomSheet(
                        onDismissRequest = { isOptionsSheetOpen = false },
                        sheetState = sheetStateOptions
                    ) {
                        TextField(
                            value = groupNameTextField.value,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = "Group name"
                                )
                            },
                            onValueChange = { groupNameTextField.value = it },
                            label = { Text("Name") },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                        )
                        TextField(
                            value = groupDescriptionTextField.value,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Group Description"
                                )
                            },
                            onValueChange = { groupDescriptionTextField.value = it },
                            label = { Text("Description") },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                        )
                        Button(onClick = {
                            scope.launch { sheetStateOptions.hide() }
                                .invokeOnCompletion { isOptionsSheetOpen = false }
                        }) {
                            Text(text = "Submit changes")
                        }
                        Spacer(modifier = Modifier.padding(30.dp))
                    }
                }

                if (isMembersSheetOpen) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            isMembersSheetOpen = false
                            addUser = false
                        }, sheetState = sheetStateMembers
                    ) {
                        if (!addUser) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Members of ${groupById.name}",
                                        modifier = Modifier.padding(bottom = 15.dp)
                                    )
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp)
                                    ) {
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
                                                LazyRowOfMembers(
                                                    members = currentGroupMembersWithoutCurrentUser
                                                ) {
                                                    navController.navigate("${Routes.USER_DETAILS}/${it.user.id}")
                                                }
                                            }
                                        }
                                    }
                                }
                                IconButton(
                                    onClick = { addUser = true },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(50.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Add member"
                                    )
                                }
                            }
                        } else {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    Text(
                                        text = "Add member to ${groupById.name}",
                                        modifier = Modifier.padding(bottom = 15.dp)
                                    )
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
                                    if (isGetUsersByUsernameLoading.value) {
                                        CircularProgressIndicator()
                                    } else {
                                        getUsersByUsernameResponse.value?.users?.let {
                                            LazyRowOfUsersToInvite(
                                                members = groupById.userGroups,
                                                users = it,
                                                invited = invitations,
                                                refreshTrigger = refreshTrigger
                                            ) { memberClicked ->
                                                if (groupId != null) {
                                                    viewModel.inviteUser(
                                                        groupId = groupId,
                                                        userId = memberClicked.id.toString()
                                                    )
                                                    refreshTrigger.intValue++
                                                }
                                            }
                                        }
                                    }
                                }
                                IconButton(
                                    onClick = { addUser = false },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(50.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = "Show members"
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.padding(30.dp))

                    }
                }

                if (isAddExpenseSheetOpen) BottomSheetAddExpense(
                    viewModel = viewModel,
                    groupState = groupById
                ) { isDismissed -> isAddExpenseSheetOpen = isDismissed }

                Row {
                    Button(onClick = { isOptionsSheetOpen = true }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Edit, contentDescription = "Edit icon"
                            )
                            Spacer(modifier = Modifier.padding(5.dp))
                            Text(text = "Edit group")
                        }
                    }
                    Spacer(modifier = Modifier.padding(5.dp))
                    Button(onClick = { isMembersSheetOpen = true }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Members icon"
                            )
                            Spacer(modifier = Modifier.padding(5.dp))
                            Text(text = "Members")
                        }
                    }
                }

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
                                modifier = Modifier.size(64.dp)
                            )
                        } else {
                            AsyncImage(
                                model = groupById.groupImageUrl.replace(
                                    "localhost", ApiEndpoints.API_ADDRESS
                                ),
                                contentDescription = "${groupById.name} avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                        Text(text = groupById.name)
                    }
                    if (isGroupByIdCreatedByCurrentUser) Text(text = "You are the owner of this group")

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
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                        .padding(5.dp)
                                ) {
                                    Button(
                                        onClick = { isAddExpenseSheetOpen = true },
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
                            if (index == 1) {
                                //Text(text = "I'm another custom text")
                                Camera()
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
    refreshTrigger: MutableIntState,
    onMemberClicked: (GetUsersByUsernameQuery.User) -> Unit
) {
    val memberIds = members.map { it.user.id }
    val invitedIds = invited.map { it.user.id }
    val usersToInvite = users.filter { it.id !in memberIds && it.id !in invitedIds }
    LazyRow {
        items(usersToInvite) { user ->
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
    toInvite: GetUsersByUsernameQuery.User, onMemberClicked: (GetUsersByUsernameQuery.User) -> Unit
) {
    Card(modifier = Modifier
        .padding(8.dp)
        .clickable { onMemberClicked(toInvite) }) {
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
    member: GetGroupByIdQuery.UserGroup, onMemberClicked: (GetGroupByIdQuery.UserGroup) -> Unit
) {
    Card(modifier = Modifier
        .padding(8.dp)
        .clickable { onMemberClicked(member) }) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            EntityImage(imageLink = member.user.avatarUrl, title = member.user.userName)
            member.user.userName?.let { Text(text = it) }
        }
    }
}

@Composable
fun BottomSheetAddExpense(
    viewModel: MainViewModel,
    groupState: GetGroupByIdQuery.GroupById,
    onSheetDismissed: (Boolean) -> Unit
) {
    val sheetStateAddExpense = rememberModalBottomSheetState()
    var ExpenseTitleTextField = rememberSaveable { mutableStateOf("") }
    val datePickerState = rememberDatePickerState()
    var isDatePickerOpen = rememberSaveable { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = { onSheetDismissed(false) },
        sheetState = sheetStateAddExpense
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add Expense", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            TextField(
                value = ExpenseTitleTextField.value,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Description"
                    )
                },
                onValueChange = { ExpenseTitleTextField.value = it },
                label = { Text("Description") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            )

            TextField(
                value = ExpenseTitleTextField.value,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Total Amount"
                    )
                },
                onValueChange = { ExpenseTitleTextField.value = it },
                label = { Text("Total Amount") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            //DatePickerDialog(onDismissRequest = {}, confirmButton = {}) { DatePicker(state = datePickerState)}
            Text(text = "Sharing", modifier = Modifier.padding(8.dp))
            var expenseList: List<KeyValuePairOfGuidAndNullableOfDecimalInput> = emptyList()

            AddExpenseUserList(groupState = groupState, expenseList) { list ->
                expenseList = list
            }

            Text(text = "Justification", modifier = Modifier.padding(8.dp))



        }
        Spacer(modifier = Modifier.padding(30.dp))
    }
}

data class TabItem(
    val title: String, val unselectedIcon: ImageVector, val selectedIcon: ImageVector
)

@Composable
fun AddExpenseUserList(
    groupState: GetGroupByIdQuery.GroupById,
    expenseList: List<KeyValuePairOfGuidAndNullableOfDecimalInput>,
    expenseDetailsList: (List<KeyValuePairOfGuidAndNullableOfDecimalInput>) -> Unit
) {
    val members = groupState.userGroups
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(members) {index, member ->
            var amountUserInput by rememberSaveable { mutableStateOf("") }
            //val currentValue = expenseList.getOrNull(index)?.value?.toString() ?: ""
            //var amountUserInput by rememberSaveable { mutableStateOf(currentValue) }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Card(modifier = Modifier.fillMaxWidth(0.7f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EntityImage(imageLink = member.user.avatarUrl, title = member.user.userName)
                        member.user.userName?.let { Text(text = it) }
                        Text(text = "${index}")
                    }
                }
                Spacer(modifier = Modifier.padding(10.dp))
                TextField(value = amountUserInput , onValueChange = { amountUserInput = it
                }, modifier = Modifier.fillMaxWidth() )
            }
            Spacer(modifier = Modifier.padding(8.dp))
        }
    }
}