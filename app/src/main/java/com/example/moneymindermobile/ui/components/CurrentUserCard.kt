package com.example.moneymindermobile.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.CurrentUserQuery
import com.example.moneymindermobile.data.MainViewModel
import com.example.moneymindermobile.data.api.ApiEndpoints
import java.io.File
import java.io.FileInputStream

@Composable
fun CurrentUserCard(currentUserQueryData: CurrentUserQuery.Data?, viewModel: MainViewModel) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                val file = File(context.cacheDir, "tempImage")
                file.outputStream().use { outputStream ->
                    parcelFileDescriptor?.fileDescriptor?.let { fileDescriptor ->
                        FileInputStream(fileDescriptor).use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
                viewModel.uploadProfilePicture(file)
            }
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                if (currentUserQueryData?.currentUser?.avatarUrl.isNullOrEmpty())
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "${currentUserQueryData?.currentUser?.userName} default avatar",
                        modifier = Modifier
                            .size(64.dp)
                            .clickable { launcher.launch("image/*") }
                    )
                else
                    AsyncImage(
                        model = currentUserQueryData?.currentUser?.avatarUrl?.replace(
                            "localhost",
                            ApiEndpoints.API_ADDRESS
                        ),
                        contentDescription = "${currentUserQueryData?.currentUser?.userName} avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clickable { launcher.launch("image/*") }
                    )
                currentUserQueryData?.currentUser?.userName?.let {
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