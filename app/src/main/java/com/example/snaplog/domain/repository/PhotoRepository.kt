package com.example.snaplog.domain.repository

import com.example.snaplog.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getAllPhotos(): Flow<List<Photo>>
    fun getPhotosByTag(tag: String): Flow<List<Photo>>
    suspend fun getPhoto(id: Long): Photo?
    suspend fun savePhoto(photo: Photo): Long
    suspend fun updatePhoto(photo: Photo)
    suspend fun deletePhoto(photo: Photo)
}