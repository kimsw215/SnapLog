package com.example.snaplog.presentation.capture

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaplog.domain.model.Photo
import com.example.snaplog.domain.usecase.SavePhotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CaptureReviewViewModel @Inject constructor(
    private val savePhotoUseCase: SavePhotoUseCase
) : ViewModel() {

    var uiState = mutableStateOf(CaptureUiState())
        private set

    fun onMemoChange(text: String) {
        uiState.value = uiState.value.copy(memo = text)
    }

    fun onTagChange(tag: String) {
        uiState.value = uiState.value.copy(tag = tag)
    }

    fun savaPhoto(
        imagePath: String,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            val photo = Photo(
                id = 0L,
                imagePath = imagePath,
                memo = uiState.value.memo,
                tag = uiState.value.tag,
                createdAt = System.currentTimeMillis()
            )
            savePhotoUseCase(photo)
            onSaved()
        }
    }
}

data class CaptureUiState(
    val memo: String = "",
    val tag: String = "All"
)