package com.example.snaplog.presentation.update

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaplog.domain.model.Photo
import com.example.snaplog.domain.usecase.DeletePhotoUseCase
import com.example.snaplog.domain.usecase.GetPhotoDetailUseCase
import com.example.snaplog.domain.usecase.UpdatePhotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoUpdateViewModel @Inject constructor(
    private val getPhotoUseCase: GetPhotoDetailUseCase,
    private val updatePhotoUseCase: UpdatePhotoUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase
) : ViewModel() {
    var uiState = mutableStateOf(PhotoUpdateUiState())
        private set

    fun load(photoId: Long) {
        viewModelScope.launch {

            val photo = getPhotoUseCase(photoId) ?: return@launch
            uiState.value = uiState.value.copy(
                photo = photo,
                memo = photo.memo,
                tag = photo.tag
            )
        }
    }

    fun onImagePathClear() {
        val photo = uiState.value.photo ?: return
        uiState.value = uiState.value.copy(
            photo = photo.copy(imagePath = "")
        )
    }

    fun onImagePathChange(path: String) {
        val photo = uiState.value.photo ?: return
        uiState.value = uiState.value.copy(
            photo = photo.copy(imagePath = path)
        )
    }

    fun onMemoChange(text: String) {
        uiState.value = uiState.value.copy(memo = text)
    }

    fun onTagChange(tag: String) {
        uiState.value = uiState.value.copy(tag = tag)
    }

    fun update(onModified: () -> Unit) {
        val current = uiState.value
        val photo = current.photo ?: return
        viewModelScope.launch {
            updatePhotoUseCase(
                photo.copy(
                    memo = current.memo,
                    tag = current.tag,
                    createdAt = System.currentTimeMillis()
                )
            )
            onModified()
        }
    }

    fun delete(onDeleted: () -> Unit) {
        val photo = uiState.value.photo ?: return
        viewModelScope.launch {
            deletePhotoUseCase(photo)
            onDeleted()
        }
    }
}

data class PhotoUpdateUiState(
    val photo: Photo? = null,
    val memo: String = "",
    val tag: String = "All"
)