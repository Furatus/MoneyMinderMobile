package com.example.moneymindermobile.ui.components.camera.photo_capture

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.yanneckreiss.cameraxtutorial.ui.features.camera.photo_capture.CameraState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CameraViewModel() : ViewModel() {

    private val _state = MutableStateFlow(CameraState())
    val state = _state.asStateFlow()

    fun storePhotoInGallery(bitmap: Bitmap) {
        viewModelScope.launch {
            updateCapturedPhotoState(bitmap)
        }
    }

    private fun updateCapturedPhotoState(updatedPhoto: Bitmap?) {
        _state.value.capturedImage?.recycle()
        _state.value = _state.value.copy(capturedImage = updatedPhoto)
    }

    override fun onCleared() {
        _state.value.capturedImage?.recycle()
        super.onCleared()
    }
}
