@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)

package com.example.moneymindermobile.ui.screens

import android.util.Base64
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.apollographql.apollo3.api.Optional
import com.example.GetGroupByIdQuery
import com.example.GetUsersByUsernameQuery
import com.example.moneymindermobile.Routes
import com.example.moneymindermobile.data.MainViewModel
import com.example.moneymindermobile.data.api.ApiEndpoints
import com.example.moneymindermobile.ui.components.EntityImage
import com.example.moneymindermobile.ui.components.FilePickingOrCamera
import com.example.moneymindermobile.ui.components.convertImageByteArrayToBitmap
import com.example.type.ExpenseType
import com.example.type.KeyValuePairOfGuidAndNullableOfDecimalInput
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState
import kotlinx.coroutines.CoroutineScope
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

    val sheetStateOptions = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var addUser by rememberSaveable { mutableStateOf(false) }
    var isOptionsSheetOpen by rememberSaveable { mutableStateOf(false) }

    val sheetStateMembers = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isMembersSheetOpen by rememberSaveable { mutableStateOf(false) }

    var isAddExpenseSheetOpen by rememberSaveable { mutableStateOf(false) }

    val getUsersByUsernameTextField = rememberSaveable { mutableStateOf("") }
    val getUsersByUsernameResponse = viewModel.getUsersByUsernameResponse.collectAsState()

    val scope = rememberCoroutineScope()
    val groupNameTextField = rememberSaveable { mutableStateOf("default name") }
    val groupDescriptionTextField = rememberSaveable { mutableStateOf("default description") }

    var isGraphOpened by rememberSaveable { mutableStateOf(false) }

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
                        var isChangingPicture by rememberSaveable { mutableStateOf(false) }
                        var imageByteArray: ByteArray? by rememberSaveable {
                            mutableStateOf(
                                byteArrayOf()
                            )
                        }

                        if (!isChangingPicture) {

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {


                                Card(
                                    onClick = {
                                        isChangingPicture = true
                                        imageByteArray = byteArrayOf()
                                    },
                                    modifier = Modifier
                                        .size(64.dp)
                                        .padding(0.dp)
                                ) {
                                    if (groupById.groupImageUrl.isNullOrEmpty() && (imageByteArray?.size == 0 || imageByteArray == null)) {
                                        Icon(
                                            imageVector = Icons.Filled.AccountBox,
                                            contentDescription = "${groupById.name} default image",
                                            modifier = Modifier.size(64.dp)
                                        )
                                    } else {
                                        if (groupById.groupImageUrl.isNullOrEmpty()) {
                                            val bitmapImage =
                                                imageByteArray?.let {
                                                    convertImageByteArrayToBitmap(
                                                        it
                                                    )
                                                }

                                            if (bitmapImage != null) {
                                                Image(
                                                    bitmap = bitmapImage.asImageBitmap(),
                                                    contentDescription = "User file",
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .aspectRatio(1f)
                                                        .clip(shape = RoundedCornerShape(8.dp)),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }

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
                                    }
                                }

                                Spacer(modifier = Modifier.padding(8.dp))

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
                                        .invokeOnCompletion {
                                            isOptionsSheetOpen = false
                                            if (imageByteArray?.size != 0 && imageByteArray != null) {
                                                val group: String = groupId!!
                                                viewModel.uploadGroupImagePicture(
                                                    groupId = group,
                                                    imageByteArray!!
                                                )
                                                refreshTrigger.intValue++
                                            }
                                        }
                                }) {
                                    Text(text = "Submit changes")
                                }
                            }
                        } else {
                            FilePickingOrCamera(
                                fileType = listOf(
                                    "png",
                                    "jpg",
                                    "jpeg"
                                )
                            ) { outputByteArray ->
                                imageByteArray = outputByteArray
                            }

                            LaunchedEffect(key1 = imageByteArray) {
                                if (imageByteArray?.size != 0 && imageByteArray != null) {
                                    isChangingPicture = false
                                }
                            }

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
                    groupState = groupById,
                    scope = scope
                ) { isDismissed -> isAddExpenseSheetOpen = isDismissed }

                Row {
                    Button(
                        onClick = { navController.navigate(route = Routes.HOME) },
                        contentPadding = PaddingValues(1.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go Back to Home"
                        )
                    }
                    Spacer(modifier = Modifier.padding(5.dp))
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
                        Spacer(modifier = Modifier.padding(8.dp))
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
                                    if (groupById.expenses.isEmpty()) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(text = "This group has no expenses, but you can add one !")
                                        }


                                    } else {
                                        ViewExpenses(
                                            groupState = groupById,
                                            navController = navController
                                        )
                                    }
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
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Button(onClick = { isGraphOpened = true }) {
                                        Icon(
                                            imageVector = Icons.Filled.List,
                                            contentDescription = "Show Graph"
                                        )
                                        Text(text = "Show Graph")
                                    }

                                    val currentUserGroup =
                                        groupById.userGroups.filter { it.user.id == currentUserId }
                                    val currentPayDueTo = currentUserGroup.first().payTo

                                    DisplayPayDueTo(
                                        payDueTo = currentPayDueTo,
                                        viewModel = viewModel,
                                        groupId = groupById.id.toString()
                                    )

                                    if (isGraphOpened) {
                                        AlertDialog(
                                            onDismissRequest = { isGraphOpened = false },
                                            confirmButton = {
                                                Button(onClick = {
                                                    isGraphOpened = false
                                                }) { Text(text = "Close") }
                                            },
                                            title = { Text(text = "Group Balance") },
                                            text = { BalanceGraph(groupById.userGroups) },
                                            modifier = Modifier
                                                .padding(8.dp)
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
fun DisplayPayDueTo(payDueTo: GetGroupByIdQuery.PayTo, viewModel: MainViewModel, groupId: String) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (payDueTo.payToUser != null) {
            Text(text = "⚠\uFE0F You owe ${payDueTo.amountToPay} € to ${payDueTo.payToUser.userName}")
            Button(onClick = {
                viewModel.OpenPaymentUrlIfNeeded(
                    groupId = groupId,
                    context = context
                )
            }) {
                Text(text = "Pay now")
            }
        } else
            Text(text = "✅ You don't owe anything to anyone")
    }
}

@Composable
fun BalanceGraph(userGroups: List<GetGroupByIdQuery.UserGroup>) {
    val entries = userGroups.mapIndexed { index, userGroup ->
        BarEntry(index.toFloat(), userGroup.balance.toFloat())
    }

    val barDataSet = BarDataSet(entries, "Balances")
    barDataSet.setColors(
        userGroups.map { userGroup ->
            if (userGroup.balance < 0) Color.Red.toArgb() else Color.Green.toArgb()
        }
    )

    val barData = BarData(barDataSet)
    barData.barWidth = 0.5f

    val selectedUserName = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            contentAlignment = Alignment.Center
        ) {
            if (selectedUserName.value != "") {
                Text(text = selectedUserName.value)
            }
        }

        AndroidView(
            factory = { context ->
                HorizontalBarChart(context).apply {
                    data = barData
                    setFitBars(true)
                    animateY(500)
                    xAxis.setDrawGridLines(false)
                    xAxis.setDrawAxisLine(false)
                    xAxis.setDrawLabels(false)
                    axisLeft.setDrawGridLines(false)
                    axisLeft.setDrawAxisLine(false)
                    axisLeft.setDrawLabels(false)
                    axisRight.setDrawGridLines(false)
                    axisRight.setDrawAxisLine(false)
                    axisRight.setDrawLabels(false)
                    description.isEnabled = false
                    setScaleEnabled(false)
                    legend.isEnabled = false

                    setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                        override fun onValueSelected(e: Entry?, h: Highlight?) {
                            val index = e?.x?.toInt()
                            if (index != null) {
                                selectedUserName.value = userGroups[index].user.userName ?: ""
                            }
                        }

                        override fun onNothingSelected() {
                            selectedUserName.value = ""
                        }

                    })

                    invalidate()
                }
            }, modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        )
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
    scope: CoroutineScope,
    onSheetDismissed: (Boolean) -> Unit
) {
    val sheetStateAddExpense = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val expenseTitleTextField = rememberSaveable { mutableStateOf("") }
    val expenseAmountField = rememberSaveable { mutableStateOf("") }
    val datePickerState = rememberDatePickerState()
    var isDatePickerOpen = rememberSaveable { mutableStateOf(false) }
    var byteArrayJustification: ByteArray? by rememberSaveable {
        mutableStateOf(
            byteArrayOf()
        )
    }

    val tabItems = listOf(
        TabItem(
            title = "Expense Info",
            unselectedIcon = Icons.Outlined.Info,
            selectedIcon = Icons.Filled.Info
        ), TabItem(
            title = "Justification",
            unselectedIcon = Icons.Outlined.CheckCircle,
            selectedIcon = Icons.Filled.CheckCircle
        )
    )

    ModalBottomSheet(
        onDismissRequest = { onSheetDismissed(false) },
        sheetState = sheetStateAddExpense
    ) {
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
        ) { index ->
            run {
                if (index == 0) {
                    var isSubmitEnabled by rememberSaveable { mutableStateOf(false) }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Add Expense",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp)
                        )
                        TextField(
                            value = expenseTitleTextField.value,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = "Description"
                                )
                            },
                            onValueChange = {
                                expenseTitleTextField.value = it
                                isSubmitEnabled =
                                    if (expenseTitleTextField.value != "" && expenseAmountField.value.toFloatOrNull() != null) true else false
                            },
                            label = { Text("Description") },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                        )

                        TextField(
                            value = expenseAmountField.value,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = "Total Amount"
                                )
                            },
                            onValueChange = {
                                expenseAmountField.value = it
                                isSubmitEnabled =
                                    if (expenseTitleTextField.value != "" && expenseAmountField.value.toFloatOrNull() != null) true else false
                            },
                            label = { Text("Total Amount") },
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                                autoCorrect = false,
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        // ExpenseType
                        val expenseType = rememberSaveable { mutableStateOf(ExpenseType.OTHER) }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(text = "Expense Type", modifier = Modifier.padding(8.dp))
                            ExpenseType.entries.forEach { type ->
                                if (type != ExpenseType.UNKNOWN__)
                                    LazyRow(
                                        modifier = Modifier
                                            .align(Alignment.Start)
                                    ) {
                                        items(listOf(type)) { type ->
                                            Checkbox(
                                                checked = expenseType.value == type,
                                                onCheckedChange = {
                                                    expenseType.value = type
                                                }
                                            )
                                            Text(text = type.name)
                                        }
                                    }
                            }
                        }

                        //DatePickerDialog(onDismissRequest = {}, confirmButton = {}) { DatePicker(state = datePickerState)}
                        Text(text = "Sharing", modifier = Modifier.padding(8.dp))
                        var expenseList: List<KeyValuePairOfGuidAndNullableOfDecimalInput> =
                            emptyList()
                        var userlist: List<Boolean> = emptyList()

                        AddExpenseUserList(
                            groupState = groupState,
                            expenseDetailsList = { list -> expenseList = list },
                            userlist = { list -> userlist = list })
                        Button(onClick = {
                            val expenseListFinal =
                                matchListUserAndExpenses(userlist, expenseList)
                            scope.launch {
                                sheetStateAddExpense.hide()
                                onSheetDismissed(false)
                            }
                            viewModel.addUserExpense(
                                amount = expenseAmountField.value.toFloat(),
                                description = expenseTitleTextField.value,
                                groupId = groupState.id.toString(),
                                expenseType = expenseType.value,
                                userAmountsList = expenseListFinal
                            )

                            //if (viewModel.addUserExpenseResponse?.value.
                        }, enabled = isSubmitEnabled) {
                            Text(text = "Submit")
                        }

                        Spacer(modifier = Modifier.padding(30.dp))
                    }
                }
                if (index == 1) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.padding(8.dp))
                        FilePickingOrCamera(
                            listOf(
                                "jpg",
                                "jpeg",
                                "png",
                                "pdf"
                            )
                        ) { outputArray ->
                            byteArrayJustification = outputArray
                        }
                        if (byteArrayJustification?.isEmpty() == false) {
                            if (determineFileExtension(byteArrayJustification!!) == "jpg" || determineFileExtension(
                                    byteArrayJustification!!
                                ) == "png"
                            ) {
                                val bitmapImage =
                                    byteArrayJustification?.let {
                                        convertImageByteArrayToBitmap(
                                            it
                                        )
                                    }
                                if (bitmapImage != null) {
                                    Spacer(modifier = Modifier.padding(8.dp))
                                    Image(
                                        bitmap = bitmapImage.asImageBitmap(),
                                        contentDescription = "User file"
                                    )
                                }
                            }
                            if (determineFileExtension(byteArrayJustification!!) == "pdf") {
                                Text(text = "Pdf picked")
                                val pdfState = rememberVerticalPdfReaderState(
                                    resource = ResourceType.Base64(
                                        convertByteArrayToBase64(
                                            byteArrayJustification!!
                                        )
                                    ),
                                    isZoomEnable = true
                                )
                                VerticalPDFReader(
                                    state = pdfState,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.padding(30.dp))
                    }
                }
            }
        }
    }
}

data class TabItem(
    val title: String, val unselectedIcon: ImageVector, val selectedIcon: ImageVector
)

@Composable
fun AddExpenseUserList(
    groupState: GetGroupByIdQuery.GroupById,
    expenseDetailsList: (List<KeyValuePairOfGuidAndNullableOfDecimalInput>) -> Unit,
    userlist: (List<Boolean>) -> Unit
) {
    val members = groupState.userGroups
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        val updatedExpenseList =
            mutableStateListOf<KeyValuePairOfGuidAndNullableOfDecimalInput>().apply {
                repeat(members.size) { index ->
                    add(
                        KeyValuePairOfGuidAndNullableOfDecimalInput(
                            key = members[index].user.id,
                            value = Optional.present(null)
                        )
                    )
                }
            }
        val checkedUserList = mutableStateListOf<Boolean>().apply {
            repeat(members.size) { _ ->
                add(
                    true
                )
            }
        }

        itemsIndexed(members) { index, member ->
            var amountUserInput by rememberSaveable { mutableStateOf("") }
            var checked by remember { mutableStateOf(true) }
            //val currentValue = expenseList.getOrNull(index)?.value?.toString() ?: ""
            //var amountUserInput by rememberSaveable { mutableStateOf(currentValue) }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Card {
                    Column {
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            EntityImage(
                                imageLink = member.user.avatarUrl,
                                title = member.user.userName
                            )
                            member.user.userName?.let { Text(text = it) }
                            //Text(text = "${index}")
                            Checkbox(
                                checked = checked,
                                onCheckedChange = {
                                    checked = it
                                    checkedUserList[index] = it
                                    Log.d("userchecked", "${it}")
                                },
                            )
                        }
                        Row {
                            TextField(
                                value = amountUserInput, onValueChange = {
                                    amountUserInput = it
                                    //Log.d("exp", "${it.toIntOrNull()}")
                                    updatedExpenseList.set(
                                        index = index,
                                        element = KeyValuePairOfGuidAndNullableOfDecimalInput(
                                            member.user.id,
                                            Optional.present(it.toFloatOrNull())
                                        )
                                    )
                                }, modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                    autoCorrect = false,
                                    keyboardType = KeyboardType.Number
                                )
                            )
                        }
                        LaunchedEffect(key1 = checkedUserList) {
                            userlist(checkedUserList)
                            checkedUserList.forEach { item ->
                                Log.d("user_boolean_list_launched", "$item")
                            }
                        }
                    }
                }

                LaunchedEffect(updatedExpenseList) {
                    expenseDetailsList(updatedExpenseList)
                }
            }
            Spacer(modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun ViewExpenses(groupState: GetGroupByIdQuery.GroupById, navController: NavHostController) {

    LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
        items(groupState.expenses) { item ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .height(80.dp), onClick = {
                    navController.navigate("${Routes.EXPENSE_DETAILS}/${item.id}")
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.CenterStart)) {
                        Text(
                            text = item.description,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(
                            text = "Paid by ${item.createdBy.userName}",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    Text(text = "${item.amount} €", modifier = Modifier.align(Alignment.CenterEnd))
                }
            }
        }
    }
}

fun determineFileExtension(bytes: ByteArray): String? {
    return when {
        bytes.size >= 2 && bytes[0] == 0xFF.toByte() && bytes[1] == 0xD8.toByte() -> "jpg"
        bytes.size >= 3 && bytes[0] == 0x89.toByte() && bytes[1] == 0x50.toByte() && bytes[2] == 0x4E.toByte() -> "png"
        bytes.size >= 4 && bytes[0] == 0x25.toByte() && bytes[1] == 0x50.toByte() && bytes[2] == 0x44.toByte() && bytes[3] == 0x46.toByte() -> "pdf"
        else -> null
    }
}

fun convertByteArrayToBase64(byteArray: ByteArray): String {
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun matchListUserAndExpenses(
    userList: List<Boolean>,
    expenseList: List<KeyValuePairOfGuidAndNullableOfDecimalInput>
): List<KeyValuePairOfGuidAndNullableOfDecimalInput> {
    val matchedList = mutableListOf<KeyValuePairOfGuidAndNullableOfDecimalInput>()

    for (i in userList.indices) {
        Log.d(
            "Expense_User",
            "id ${expenseList[i].key} ,value ${expenseList[i].value} , boolean ${userList[i]}"
        )
        if (userList[i]) {
            val expense = expenseList.getOrNull(i)
            matchedList.add(
                expense ?: KeyValuePairOfGuidAndNullableOfDecimalInput(
                    key = Any(),
                    value = Optional.Absent
                )
            )
        }
    }

    return matchedList
}

@Composable
fun GroupBalance() {

}