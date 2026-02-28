package com.app.padams.domain.repository

import com.app.padams.domain.model.Album
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    fun getAlbums(): Flow<List<Album>>
    suspend fun getAlbumById(id: Long): Album?
    suspend fun createAlbum(name: String): Long
    suspend fun deleteAlbum(id: Long)
    fun getAlbumPhotos(albumId: Long): Flow<List<String>>
    suspend fun addPhotosToAlbum(albumId: Long, photoUris: List<String>)
    suspend fun removePhotoFromAlbum(albumId: Long, photoUri: String)
    fun getAlbumImageCount(albumId: Long): Flow<Int>
}
