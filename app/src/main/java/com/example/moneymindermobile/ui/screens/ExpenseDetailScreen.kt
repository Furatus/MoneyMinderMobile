package com.example.moneymindermobile.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.moneymindermobile.Routes
import com.example.moneymindermobile.data.MainViewModel
import com.example.moneymindermobile.ui.components.EntityImage
import com.example.moneymindermobile.ui.components.FilePickingOrCamera
import com.example.moneymindermobile.ui.components.convertImageByteArrayToBitmap
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpenseDetailScreen(
    expenseId: String?,
    viewModel: MainViewModel,
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()
    val groupById = viewModel.groupByIdResponse.collectAsState().value?.groupById
    val expense = groupById?.expenses?.filter { it.id == expenseId }

    if (!expense.isNullOrEmpty()) {
        val expenseIndex = groupById.expenses.indexOf(expense[0])

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                onClick = { navController.navigate("${Routes.GROUP_DETAILS}/${groupById.id}") },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.fillMaxWidth(0.3f)
            ) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "Close icon")
            }
            Spacer(modifier = Modifier.padding(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { navController.navigate("${Routes.EXPENSE_DETAILS}/${groupById.expenses[expenseIndex - 1].id}") },
                    modifier = Modifier.align(Alignment.CenterStart),
                    enabled = expenseIndex > 0
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "previous expense icon"
                    )
                }
                Text(text = expense[0].description, modifier = Modifier.align(Alignment.Center))
                Button(
                    onClick = { navController.navigate("${Routes.EXPENSE_DETAILS}/${groupById.expenses[expenseIndex + 1].id}") },
                    modifier = Modifier.align(Alignment.CenterEnd),
                    enabled = expenseIndex + 1 < groupById.expenses.size
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "next expense icon"
                    )
                }
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
                        Column(modifier = Modifier.fillMaxSize()) {
                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "${expense[0].amount} €", fontSize = 30.sp)
                                    Spacer(modifier = Modifier.padding(8.dp))
                                    EntityImage(
                                        imageLink = expense[0].createdBy.avatarUrl,
                                        title = "expense author"
                                    )
                                    Text(
                                        text = "Paid by ${expense[0].createdBy.userName}",
                                        modifier = Modifier.padding(4.dp)
                                    )
                                    Text(text = "on ${parseUtcDate(expense[0].createdAt as String).toString()}")
                                    Spacer(modifier = Modifier.padding(8.dp))
                                    Text(text = "Category : ${expense[0].expenseType}")
                                    Spacer(modifier = Modifier.padding(4.dp))
                                }
                            }

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                itemsIndexed(expense[0].userExpenses) { _, member ->

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Card(modifier = Modifier.fillMaxWidth()) {
                                            Box(Modifier.fillMaxSize()) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    EntityImage(
                                                        imageLink = member.user.avatarUrl,
                                                        title = member.user.userName
                                                    )
                                                    Spacer(modifier = Modifier.padding(8.dp))
                                                    member.user.userName?.let { Text(text = it) }

                                                }
                                                Text(
                                                    modifier = Modifier
                                                        .align(Alignment.CenterEnd)
                                                        .padding(8.dp), text = "${member.amount} €"
                                                )
                                            }
                                        }


                                    }
                                    Spacer(modifier = Modifier.padding(8.dp))
                                }
                            }
                        }
                    }
                    if (index == 1) {
                        if (expense[0].justificationExtension != null) {
                            if (expenseId != null) {
                                val currentExpenseId = rememberSaveable { mutableStateOf("") }
                                if (currentExpenseId.value != expenseId) {
                                    currentExpenseId.value = expenseId
                                    viewModel.expenseJustification(expenseId)
                                }
                                val expenseByteArray by viewModel.expenseJustificationArray.collectAsState()
                                if (expenseByteArray != null) {
                                    if (determineFileExtension(expenseByteArray!!) == "jpg" || determineFileExtension(
                                            expenseByteArray!!
                                        ) == "png"
                                    ) {
                                        val bitmapImage =
                                            expenseByteArray?.let { convertImageByteArrayToBitmap(it) }
                                        if (bitmapImage != null) {
                                            Spacer(modifier = Modifier.padding(8.dp))
                                            Image(
                                                bitmap = bitmapImage.asImageBitmap(),
                                                contentDescription = "User file"
                                            )
                                        }
                                    }
                                    if (determineFileExtension(expenseByteArray!!) == "pdf") {
                                        val pdfState = rememberVerticalPdfReaderState(
                                            resource = ResourceType.Base64(
                                                convertByteArrayToBase64(
                                                    expenseByteArray!!
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
                                    Button(onClick = {
                                        viewModel.uploadExpenseJustification(expenseId, expenseByteArray!!)
                                        navController.navigate("${Routes.EXPENSE_DETAILS}/${expenseId}")
                                    }) {
                                        Text(text = "Submit")
                                    }
                                } else {

                                    Text(text = "Failed to fetch Image")
                                }
                            } else {
                                Text(text = "failed to fetch expense id")
                            }
                        } else {
                            var byteArrayJustification: ByteArray? by rememberSaveable {
                                mutableStateOf(
                                    byteArrayOf()
                                )
                            }

                            Column {
                                if (byteArrayJustification == null || byteArrayJustification?.isEmpty() == true) {
                                    Text(text = "No justification for this expense, but you can add one !")
                                    Spacer(modifier = Modifier.padding(8.dp))
                                }
                                FilePickingOrCamera(
                                    fileType = listOf(
                                        "jpg",
                                        "jpeg",
                                        "png",
                                        "pdf"
                                    )
                                ) { bytes ->
                                    byteArrayJustification = bytes
                                }

                                if (byteArrayJustification?.isEmpty() == false) {

                                    Button(onClick = {
                                        if (expenseId != null) {
                                            viewModel.uploadExpenseJustification(
                                                expenseId,
                                                byteArrayJustification!!
                                            )
                                            navController.navigate("${Routes.EXPENSE_DETAILS}/${expense[0].id}")
                                        }
                                    }, modifier = Modifier.fillMaxWidth()) {
                                        Text(text = "Submit justification")
                                    }

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
                                                contentDescription = "User file",
                                                modifier = Modifier.fillMaxHeight(0.6f)
                                            )
                                        }
                                    }
                                    if (determineFileExtension(byteArrayJustification!!) == "pdf") {
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
                            }

                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
fun parseUtcDate(utcString: String): Date? {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    dateFormat.timeZone = TimeZone.getTimeZone("UTC+2")
    return dateFormat.parse(utcString)
}