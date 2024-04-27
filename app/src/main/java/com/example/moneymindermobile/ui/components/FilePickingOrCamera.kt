package com.example.moneymindermobile.ui.components

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.example.moneymindermobile.ui.components.camera.Camera
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

@Composable
fun FilePickingOrCamera(fileType: List<String>, outputByteArray: (ByteArray?) -> Unit) {
    //Camera()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showFilePicker by rememberSaveable { mutableStateOf(false) }
    var imagebyteArray: ByteArray? by rememberSaveable {
        mutableStateOf(
            byteArrayOf()
        )
    }

    var isFileChosenOrPictureTaken: Boolean by rememberSaveable { mutableStateOf(false) }
    var isTakingPicture: Boolean by rememberSaveable { mutableStateOf(false) }

    if (!isFileChosenOrPictureTaken) {
        if (!isTakingPicture) {
            FilePicker(
                show = showFilePicker,
                fileExtensions = fileType
            ) { platformFile ->
                showFilePicker = false
                if (platformFile != null) scope.launch {
                    imagebyteArray =
                        readBytesFromUri(
                            context,
                            Uri.parse(platformFile.path)
                        )
                    //Log.d("image_bytearray", "${imagebyteArray.isEmpty()}")
                    //Log.d("bytearray", imagebyteArray.decodeToString())
                }

            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {

                Spacer(modifier = Modifier.padding(10.dp))

                Button(onClick = { isTakingPicture = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Take a picture")
                }
                Text(text = "or", Modifier.padding(10.dp))
                Button(onClick = { showFilePicker = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Choose an existing File")
                }

                LaunchedEffect(imagebyteArray) {
                    if (imagebyteArray?.size != 0) isFileChosenOrPictureTaken = true
                }

            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .fillMaxWidth(),
            ) {

                var takenPicture: Bitmap?

                Column(verticalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {
                            isTakingPicture = false
                            isFileChosenOrPictureTaken = false
                            imagebyteArray = byteArrayOf()
                        }, modifier = Modifier
                            .size(50.dp), contentPadding = PaddingValues(1.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Go back")
                    }
                    Spacer(modifier = Modifier.padding(8.dp))
                    Camera { snap ->
                        takenPicture = snap
                        imagebyteArray = takenPicture?.let { convertBitmapToByteArray(it) }
                    }
                }

                LaunchedEffect(imagebyteArray) {
                    if (imagebyteArray?.size != 0 && imagebyteArray != null) {
                        isTakingPicture = false
                        isFileChosenOrPictureTaken = true
                    }
                }
            }
        }
    } else {
        Button(onClick = {
            imagebyteArray = byteArrayOf()
            isFileChosenOrPictureTaken = false
            outputByteArray(byteArrayOf())
        }, modifier = Modifier.fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.Center) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete Justification")
                Text(text = "Delete Justification", modifier = Modifier.padding(8.dp))
            }
        }
        outputByteArray(imagebyteArray)
    }
}

fun convertImageByteArrayToBitmap(imageData: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
}

fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return stream.toByteArray()
}

suspend fun readBytesFromUri(context: Context, uri: Uri): ByteArray? {
    return withContext(Dispatchers.IO) {
        var inputStream: InputStream? = null
        var byteArrayOutputStream: ByteArrayOutputStream? = null
        try {
            val contentResolver: ContentResolver = context.contentResolver
            inputStream = contentResolver.openInputStream(uri)
            byteArrayOutputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream?.read(buffer).also { length = it!! } != -1) {
                byteArrayOutputStream.write(buffer, 0, length)
            }
            byteArrayOutputStream.flush()
            byteArrayOutputStream.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            try {
                inputStream?.close()
                byteArrayOutputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
