package com.example.snaplog.domain.usecase

import com.example.snaplog.domain.model.Photo
import com.example.snaplog.domain.repository.PhotoRepository

class SavePhotoUseCase(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(photo: Photo): Long {
        return photoRepository.savePhoto(photo)
    }
}