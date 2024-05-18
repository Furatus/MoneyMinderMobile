@file:OptIn(ExperimentalFoundationApi::class)

package com.example.moneymindermobile.ui.screens

import android.net.Uri
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
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.example.moneymindermobile.Routes
import com.example.moneymindermobile.data.MainViewModel
import com.example.moneymindermobile.ui.components.readBytesFromUri
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState
import kotlinx.coroutines.launch

@Composable
fun UserPaymentScreen(viewModel: MainViewModel, navController: NavHostController) {

    val scope = rememberCoroutineScope()
    val currentUser = viewModel.currentUserResponse.collectAsState().value?.currentUser
    val context = LocalContext.current
    var showFilePicker by rememberSaveable { mutableStateOf(false) }

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

            Button(
                onClick = { navController.navigate(Routes.HOME) },
                contentPadding = PaddingValues(1.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "back to main menu icon"
                )
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

                            val currentUserLoc = rememberSaveable { mutableStateOf("") }

                            if (currentUserLoc.value != currentUser.id) {
                                currentUserLoc.value = currentUser.id as String
                                viewModel.userRib(currentUser.id)
                            }

                            val documentViewByteArray =
                                viewModel.userRibResponse.collectAsState().value
                            var documentByteArray: ByteArray? by rememberSaveable {
                                mutableStateOf(
                                    byteArrayOf()
                                )
                            }
                            Column (horizontalAlignment = Alignment.CenterHorizontally) {

                                FilePicker(
                                    show = showFilePicker,
                                    fileExtensions = listOf("pdf")
                                ) { platformFile ->
                                    showFilePicker = false
                                    if (platformFile != null) scope.launch {
                                        documentByteArray =
                                            readBytesFromUri(
                                                context,
                                                Uri.parse(platformFile.path)
                                            )
                                        documentByteArray?.let { viewModel.uploadUserRib(it) }
                                        navController.navigate(Routes.USER_PAYMENT)
                                    }

                                }

                                if (documentViewByteArray != null) {
                                    val pdfState = rememberVerticalPdfReaderState(
                                        resource = ResourceType.Base64(
                                            convertByteArrayToBase64(
                                                documentViewByteArray
                                            )
                                        ),
                                        isZoomEnable = true
                                    )
                                    Button(onClick = { showFilePicker = true }) {
                                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "change bank details icon")
                                        Text(text = "Change bank details")
                                    }

                                    VerticalPDFReader(
                                        state = pdfState,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                } else {
                                    Text(text = "Unable to fetch Data")
                                }
                            }

                        } else {

                            Column(
                                Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "No bank details found")
                                Spacer(modifier = Modifier.padding(8.dp))
                                Button(onClick = { showFilePicker = true }) {
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
    } else Text(text = "Error : failed to fetch current User")
}