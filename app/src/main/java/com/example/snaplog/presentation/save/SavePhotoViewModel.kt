package com.example.snaplog.presentation.save

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaplog.domain.model.Photo
import com.example.snaplog.domain.usecase.SavePhotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavePhotoViewModel @Inject constructor(
    private val savePhotoUseCase: SavePhotoUseCase
) : ViewModel() {
    var uiState = mutableStateOf(SavePhotoUiState())
        private set

    fun onImagePathClear() {
        uiState.value = uiState.value.copy(imagePath = "")
    }

    fun onImagePathChange(path: String) {
        uiState.value = uiState.value.copy(imagePath = path)
    }

    fun onMemoChange(text: String) {
        uiState.value = uiState.value.copy(memo = text)
    }

    fun onTagChange(tag: String) {
        uiState.value = uiState.value.copy(tag = tag)
    }

    fun savaPhoto(onSaved: () -> Unit) {
        val currentState = uiState.value
        viewModelScope.launch {
            val photo = Photo(
                id = 0L,
                imagePath = currentState.imagePath,
                memo = currentState.memo,
                tag = currentState.tag,
                createdAt = System.currentTimeMillis()
            )
            savePhotoUseCase(photo)
            onSaved()
        }
    }
}

data class SavePhotoUiState(
    val imagePath: String = "",
    val memo: String = "",
    val tag: String = "All"
)