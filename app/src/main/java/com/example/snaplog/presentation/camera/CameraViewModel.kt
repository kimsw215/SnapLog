package com.example.snaplog.presentation.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snaplog.domain.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val repository: PhotoRepository
): ViewModel() {
    val recentPhotoPath = repository.getAllPhotos()
        .map { it.firstOrNull()?.imagePath }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
