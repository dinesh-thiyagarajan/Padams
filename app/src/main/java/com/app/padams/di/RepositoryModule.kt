package com.app.padams.di

import com.app.padams.data.repository.AlbumRepositoryImpl
import com.app.padams.data.repository.FaceRepositoryImpl
import com.app.padams.data.repository.FavoriteRepositoryImpl
import com.app.padams.data.repository.PhotoRepositoryImpl
import com.app.padams.domain.repository.AlbumRepository
import com.app.padams.domain.repository.FaceRepository
import com.app.padams.domain.repository.FavoriteRepository
import com.app.padams.domain.repository.PhotoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPhotoRepository(impl: PhotoRepositoryImpl): PhotoRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindAlbumRepository(impl: AlbumRepositoryImpl): AlbumRepository

    @Binds
    @Singleton
    abstract fun bindFaceRepository(impl: FaceRepositoryImpl): FaceRepository
}
