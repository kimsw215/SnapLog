package com.example.snaplog.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaplog.domain.model.Photo
import com.example.snaplog.domain.usecase.DeletePhotoUseCase
import com.example.snaplog.domain.usecase.GetPhotoDetailUseCase
import com.example.snaplog.domain.usecase.UpdatePhotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val isLoading: Boolean = true,
    val photo: Photo? = null,
    val memo: String = "",
    val tag: String = ""
)

@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    private val getPhotoDetailUseCase: GetPhotoDetailUseCase,
    private val updatePhotoUseCase: UpdatePhotoUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState

    fun load(photoId: Long) {
        viewModelScope.launch {
            val photo = getPhotoDetailUseCase(photoId)
            _uiState.value = DetailUiState(
                isLoading = false,
                photo = photo,
                memo = photo?.memo.orEmpty(),
                tag = photo?.tag.orEmpty()
            )
        }
    }

    fun onMemoChange(text: String) {
        _uiState.value = _uiState.value.copy(memo = text)
    }

    fun onTagChange(tag: String) {
        _uiState.value = _uiState.value.copy(tag = tag)
    }

    fun save(onDone: () -> Unit) {
        val current = _uiState.value
        val photo = current.photo ?: return

        viewModelScope.launch {
            updatePhotoUseCase(
                photo.copy(
                    memo = current.memo,
                    tag = current.tag
                )
            )
            onDone()
        }
    }

    fun delete(onDone: () -> Unit) {
        val photo = _uiState.value.photo ?: return
        viewModelScope.launch {
            deletePhotoUseCase(photo)
            onDone()
        }
    }
}