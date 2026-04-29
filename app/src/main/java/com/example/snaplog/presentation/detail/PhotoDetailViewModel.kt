package com.example.snaplog.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaplog.domain.model.Photo
import com.example.snaplog.domain.usecase.GetPhotoDetailUseCase
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
}