package com.example.snaplog.domain.usecase

import com.example.snaplog.domain.model.Photo
import com.example.snaplog.domain.repository.PhotoRepository

class GetPhotoDetailUseCase(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(id: Long): Photo? {
        return photoRepository.getPhoto(id)
    }
}