package com.example.snaplog.presentation.home

import com.example.snaplog.domain.model.Photo

data class HomeUiState(
    val isLoading: Boolean = false,
    val selectedTag: String ="ALL",
    val photos: List<Photo> = emptyList()
)
