package com.example.moneymindermobile.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.moneymindermobile.Routes
import com.example.moneymindermobile.data.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpenseDetailScreen(expenseId: String?, viewModel: MainViewModel, navController: NavHostController ) {
    val scope = rememberCoroutineScope()
    val groupById = viewModel.groupByIdResponse.collectAsState().value?.groupById
    val expense = groupById?.expenses?.filter { it.id == expenseId }

    if (!expense.isNullOrEmpty()) {
        val expenseIndex = groupById.expenses.indexOf(expense[0])

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                Button(onClick = {navController.navigate("${Routes.GROUP_DETAILS}/${groupById.id}") }, contentPadding = PaddingValues(0.dp), modifier = Modifier.fillMaxWidth(0.3f)) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close icon")
                }
            Spacer(modifier = Modifier.padding(8.dp))

            Box (modifier = Modifier.fillMaxWidth())  {
                Button(onClick = { navController.navigate("${Routes.EXPENSE_DETAILS}/${groupById.expenses[expenseIndex - 1].id}") }, modifier = Modifier.align(Alignment.CenterStart), enabled = expenseIndex > 0) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "previous expense icon" )
                }
                Text(text = expense[0].description, modifier = Modifier.align(Alignment.Center))
                Button(onClick = { navController.navigate("${Routes.EXPENSE_DETAILS}/${groupById.expenses[expenseIndex + 1].id}") }, modifier = Modifier.align(Alignment.CenterEnd), enabled = expenseIndex + 1 < groupById.expenses.size) {
                    Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "next expense icon" )
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
                        Text(text = "i'm a custom text")
                    }
                    if (index == 1) {
                        Text(text = "I'm another custom text")

                    }
                }

            }

        }
    }
}