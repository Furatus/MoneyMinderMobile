@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.moneymindermobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.CurrentUserQuery
import com.example.moneymindermobile.Routes
import com.example.moneymindermobile.data.MainViewModel
import com.example.moneymindermobile.data.api.ApiEndpoints
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentUserCard(currentUserQueryData: CurrentUserQuery.Data?, viewModel: MainViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val isCurrentUserSheetOpened = rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = {
            isCurrentUserSheetOpened.value = true
        }
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                val currentUser = currentUserQueryData?.currentUser
                if (currentUser != null) {
                    if (isCurrentUserSheetOpened.value) {
                        ModalBottomSheetCurrentUser(
                            currentUser = currentUser,
                            mainViewModel = viewModel,
                            navController = navController
                        ) { onDismiss ->
                            isCurrentUserSheetOpened.value = onDismiss
                        }
                    }
                    EntityImage(imageLink = currentUser.avatarUrl, title = currentUser.userName)
                    currentUser.userName?.let {
                        Text(
                            text = it,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                } else {
                    Text(text = "Error fetching the connected user")
                }
            }
        }
    }
}


@Composable
fun ModalBottomSheetCurrentUser(
    currentUser: CurrentUserQuery.CurrentUser?,
    mainViewModel: MainViewModel,
    navController: NavHostController,
    onSheetDismiss: (Boolean) -> Unit
) {
    val sheetStateCurrentUser = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var changedUserState by rememberSaveable { mutableStateOf(false) }
    var changedUserPicture by rememberSaveable { mutableStateOf(false) }
    var usernameTextField by rememberSaveable { mutableStateOf(currentUser?.userName ?: "") }
    var passwordTextField by rememberSaveable { mutableStateOf("*******") }
    val scope = rememberCoroutineScope()
    var imageByteArray: ByteArray? by rememberSaveable {
        mutableStateOf(
            byteArrayOf()
        )
    }
    var isChangingPicture by rememberSaveable { mutableStateOf(false) }


    ModalBottomSheet(
        onDismissRequest = { onSheetDismiss(false) },
        sheetState = sheetStateCurrentUser
    ) {

        if (!isChangingPicture) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    text = "Edit ${currentUser?.userName ?: "Failed to fetch user"}",
                    modifier = Modifier.padding(8.dp)
                )

                if (currentUser != null) {

                    Card(
                        onClick = {
                            isChangingPicture = true
                            imageByteArray = byteArrayOf()
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .padding(0.dp)
                    ) {
                        if (currentUser.avatarUrl.isNullOrEmpty() && (imageByteArray?.size == 0 || imageByteArray == null)) {
                            Icon(
                                imageVector = Icons.Filled.AccountBox,
                                contentDescription = "${currentUser.userName} default image",
                                modifier = Modifier.size(64.dp)
                            )
                        } else {
                            if (currentUser.avatarUrl.isNullOrEmpty()) {
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
                                    model = currentUser.avatarUrl.replace(
                                        "localhost", ApiEndpoints.API_ADDRESS
                                    ),
                                    contentDescription = "${currentUser.userName} avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        }
                    }

                    TextField(
                        value = usernameTextField,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Description"
                            )
                        },
                        onValueChange = {
                            usernameTextField = it
                            changedUserState = true
                        },
                        label = { Text("Username") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    )

                    TextField(
                        value = passwordTextField,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Description"
                            )
                        },
                        onValueChange = {
                            passwordTextField = it
                            changedUserState = true
                        },
                        label = { Text("Password") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    Button(onClick = {
                        scope.launch { sheetStateCurrentUser.hide() }.invokeOnCompletion {
                            onSheetDismiss(false)
                            mainViewModel.signOut()
                            navController.navigate(Routes.LOGIN)
                        }
                    }, modifier = Modifier.padding(8.dp)) {
                        Icon(imageVector = Icons.Filled.Clear, contentDescription = "logout icon")
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(text = "Logout")
                    }

                    Button(onClick = {
                        scope.launch { sheetStateCurrentUser.hide() }
                            .invokeOnCompletion {
                                onSheetDismiss(false)
                                if (imageByteArray?.size != 0 && imageByteArray != null && changedUserPicture) {
                                    mainViewModel.uploadProfilePicture(
                                        imageByteArray = imageByteArray!!,
                                        username = currentUser.userName!!
                                    )
                                }
                            }

                    }, modifier = Modifier.padding(8.dp)) {
                        Text(text = "Apply Changes")
                    }
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
                    changedUserPicture = true
                }
            }
        }

        Spacer(modifier = Modifier.padding(30.dp))
    }

}