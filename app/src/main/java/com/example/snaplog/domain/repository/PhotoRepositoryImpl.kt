package com.example.snaplog.domain.repository

import com.example.snaplog.data.local.PhotoDao
import com.example.snaplog.data.mapper.toDomain
import com.example.snaplog.data.mapper.toEntity
import com.example.snaplog.domain.model.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PhotoRepositoryImpl(
    private val photoDao: PhotoDao
): PhotoRepository {
    override fun getAllPhotos(): Flow<List<Photo>> {
        return photoDao.getAllPhotos().map { list -> list.map{ it.toDomain() } }
    }

    override fun getPhotosByTag(tag: String): Flow<List<Photo>> {
        return photoDao.getPhotosByTag(tag).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getPhoto(id: Long): Photo? {
        return photoDao.getPhotoById(id)?.toDomain()
    }

    override suspend fun savePhoto(photo: Photo): Long {
        return photoDao.insertPhoto(photo.toEntity())
    }

    override suspend fun updatePhoto(photo: Photo) {
        return photoDao.updatePhoto(photo.toEntity())
    }

    override suspend fun deletePhoto(photo: Photo) {
        return photoDao.deletePhoto(photo.toEntity())
    }
}