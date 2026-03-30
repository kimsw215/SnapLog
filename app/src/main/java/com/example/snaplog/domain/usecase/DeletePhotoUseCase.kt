package com.example.snaplog.domain.usecase

import com.example.snaplog.domain.model.Photo
import com.example.snaplog.domain.repository.PhotoRepository

class DeletePhotoUseCase(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(photo: Photo) {
        return photoRepository.deletePhoto(photo)
    }
}