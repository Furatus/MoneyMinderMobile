@file:OptIn(ExperimentalFoundationApi::class)

package com.example.moneymindermobile.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Star
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

@Composable
fun UserPaymentScreen(viewModel: MainViewModel, navController: NavHostController) {

    val scope = rememberCoroutineScope()
    val currentUser = viewModel.currentUserResponse.collectAsState().value?.currentUser

    if (currentUser != null) {
        Column {

            val tabItems = listOf(
                TabItem(
                    title = "Bank Details",
                    unselectedIcon = Icons.Outlined.Email,
                    selectedIcon = Icons.Filled.Email
                ), TabItem(
                    title = "Payment Service",
                    unselectedIcon = Icons.Outlined.Star,
                    selectedIcon = Icons.Filled.Star
                )
            )

            var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
            val pagerState = rememberPagerState(pageCount = { tabItems.size })

            LaunchedEffect(pagerState.currentPage) {

                selectedTabIndex = pagerState.currentPage
            }

            Button(onClick = { navController.navigate(Routes.HOME) }, contentPadding = PaddingValues(1.dp), modifier = Modifier.padding(8.dp)) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back to main menu icon")
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
                        if (currentUser.ribExtension != null) {

                        } else {
                            Column(
                                Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "No bank details found")
                                Spacer(modifier = Modifier.padding(8.dp))
                                Button(onClick = { /*TODO*/ }) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "add bank details icon"
                                    )
                                    Text(text = "Add Details")
                                }
                            }
                        }
                    }
                    if (index == 1) {
                        Text(text = "Not implemented yet")
                    }
                }

            }
        }
    }
    else Text(text = "Error : failed to fetch current User")
}