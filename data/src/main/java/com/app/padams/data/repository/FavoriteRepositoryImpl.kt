package com.app.padams.data.repository

import com.app.padams.data.local.dao.FavoriteDao
import com.app.padams.data.local.entity.FavoriteEntity
import com.app.padams.domain.model.Photo
import com.app.padams.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override fun getFavoriteUris(): Flow<List<String>> {
        return favoriteDao.getAllFavoriteUris()
    }

    override fun isFavorite(uri: String): Flow<Boolean> {
        return favoriteDao.isFavorite(uri)
    }

    override suspend fun toggleFavorite(photo: Photo) {
        val isFav = favoriteDao.isFavorite(photo.uri).first()
        if (isFav) {
            favoriteDao.removeFavorite(photo.uri)
        } else {
            favoriteDao.addFavorite(
                FavoriteEntity(
                    imageUri = photo.uri,
                    mediaStoreId = photo.id,
                    dateAdded = System.currentTimeMillis()
                )
            )
        }
    }

    override fun getAllFavoriteUris(): Flow<Set<String>> {
        return favoriteDao.getAllFavoriteUris().map { it.toSet() }
    }
}
