package com.example.snaplog.di

import android.content.Context
import androidx.room.Room
import com.example.snaplog.data.local.PhotoDao
import com.example.snaplog.data.local.SnapLogDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SnapLogDatabase {
        return Room.databaseBuilder(
            context,
            SnapLogDatabase::class.java,
            "snaplog.db"
        ).build()
    }

    @Provides
    fun providePhotoDao(db: SnapLogDatabase): PhotoDao {
        return db.photoDao()
    }
}