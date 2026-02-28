package com.app.padams.domain.repository

import com.app.padams.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavoriteUris(): Flow<List<String>>
    fun isFavorite(uri: String): Flow<Boolean>
    suspend fun toggleFavorite(photo: Photo)
    fun getAllFavoriteUris(): Flow<Set<String>>
}
