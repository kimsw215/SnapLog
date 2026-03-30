package com.example.snaplog.domain.usecase

import com.example.snaplog.domain.model.Photo
import com.example.snaplog.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow

class GetPhotoListUseCase(
    private val photoRepository: PhotoRepository
) {
    operator fun invoke(tag: String?): Flow<List<Photo>> {
        return if(tag == null || tag =="ALL") {
            photoRepository.getAllPhotos()
        } else {
            photoRepository.getPhotosByTag(tag)
        }
    }
}