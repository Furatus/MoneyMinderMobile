@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.moneymindermobile.ui.components.camera

import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.example.moneymindermobile.ui.components.camera.no_permission.NoPermissionScreen
import com.example.moneymindermobile.ui.components.camera.photo_capture.CameraScreen
import com.example.moneymindermobile.ui.components.camera.photo_capture.CameraViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Camera() {

    val cameraPermissionState: PermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    MainContent(
        hasPermission = cameraPermissionState.status.isGranted,
        onRequestPermission = cameraPermissionState::launchPermissionRequest
    )
}

@Composable
private fun MainContent(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit
) {

    if (hasPermission) {
        val viewmodel  = CameraViewModel()
        CameraScreen(viewModel = viewmodel)
    } else {
        NoPermissionScreen(onRequestPermission)
    }
}
