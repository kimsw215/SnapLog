package com.example.snaplog.di

import com.example.snaplog.domain.repository.PhotoRepository
import com.example.snaplog.domain.usecase.DeletePhotoUseCase
import com.example.snaplog.domain.usecase.GetPhotoDetailUseCase
import com.example.snaplog.domain.usecase.GetPhotoListUseCase
import com.example.snaplog.domain.usecase.SavePhotoUseCase
import com.example.snaplog.domain.usecase.UpdatePhotoUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideSavePhotoUseCase(repo: PhotoRepository) = SavePhotoUseCase(repo)

    @Provides
    fun provideGetPhotoListUseCase(repo: PhotoRepository) = GetPhotoListUseCase(repo)

    @Provides
    fun provideGetPhotoDetailUseCase(repo: PhotoRepository) = GetPhotoDetailUseCase(repo)

    @Provides
    fun provideUpdatePhotoUseCase(repo: PhotoRepository) = UpdatePhotoUseCase(repo)

    @Provides
    fun provideDeletePhotoUseCase(repo: PhotoRepository) = DeletePhotoUseCase(repo)
}