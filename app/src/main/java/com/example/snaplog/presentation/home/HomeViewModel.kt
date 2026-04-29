package com.example.snaplog.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaplog.domain.usecase.GetPhotoListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPhotoUseCase: GetPhotoListUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadPhotos()
    }

    fun selectTag(tag: String) {
        _uiState.value = _uiState.value.copy(selectedTag = tag)
        loadPhotos()
    }

    private fun loadPhotos() {
        viewModelScope.launch {
            getPhotoUseCase(_uiState.value.selectedTag).collectLatest { list ->
                _uiState.value = _uiState.value.copy(
                    photos = list,
                    isLoading = false
                )
            }
        }
    }
}