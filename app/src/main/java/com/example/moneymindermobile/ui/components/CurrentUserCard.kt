package com.example.moneymindermobile.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.CurrentUserQuery
import com.example.moneymindermobile.data.MainViewModel
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
                val currentUser = currentUserQueryData?.currentUser
                if (currentUser != null) {
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