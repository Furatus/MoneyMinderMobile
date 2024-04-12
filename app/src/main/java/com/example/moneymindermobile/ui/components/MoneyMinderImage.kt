package com.example.moneymindermobile.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.moneymindermobile.data.api.ApiEndpoints

@Composable
fun EntityImage(imageLink:String?, title: String?){
    if (imageLink.isNullOrEmpty())
        Icon(
            imageVector = Icons.Filled.AccountBox,
            contentDescription = "$title default image",
            modifier = Modifier
                .size(64.dp)
        )
    else
        AsyncImage(
            model = imageLink.replace(
                "localhost",
                ApiEndpoints.API_ADDRESS
            ),
            contentDescription = "$title avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
        )
}