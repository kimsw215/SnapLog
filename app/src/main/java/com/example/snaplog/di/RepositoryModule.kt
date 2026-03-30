package com.example.snaplog.di

import com.example.snaplog.data.local.PhotoDao
import com.example.snaplog.domain.repository.PhotoRepository
import com.example.snaplog.domain.repository.PhotoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePhotoRepository(
        photoDto: PhotoDao
    ): PhotoRepository {
        return PhotoRepositoryImpl(photoDto)
    }
}