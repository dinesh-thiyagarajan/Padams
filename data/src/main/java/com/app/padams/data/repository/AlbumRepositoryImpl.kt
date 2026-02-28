package com.app.padams.data.repository

import com.app.padams.data.local.dao.AlbumDao
import com.app.padams.data.local.dao.AlbumImageDao
import com.app.padams.data.local.entity.AlbumEntity
import com.app.padams.data.local.entity.AlbumImageEntity
import com.app.padams.domain.model.Album
import com.app.padams.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlbumRepositoryImpl @Inject constructor(
    private val albumDao: AlbumDao,
    private val albumImageDao: AlbumImageDao
) : AlbumRepository {

    override fun getAlbums(): Flow<List<Album>> {
        return albumDao.getAllAlbums().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAlbumById(id: Long): Album? {
        return albumDao.getAlbumById(id)?.toDomain()
    }

    override suspend fun createAlbum(name: String): Long {
        val now = System.currentTimeMillis()
        return albumDao.insertAlbum(
            AlbumEntity(
                name = name,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    override suspend fun deleteAlbum(id: Long) {
        albumDao.deleteAlbum(id)
    }

    override fun getAlbumPhotos(albumId: Long): Flow<List<String>> {
        return albumImageDao.getAlbumImageUris(albumId)
    }

    override suspend fun addPhotosToAlbum(albumId: Long, photoUris: List<String>) {
        val now = System.currentTimeMillis()
        val entities = photoUris.map { uri ->
            AlbumImageEntity(
                albumId = albumId,
                imageUri = uri,
                addedAt = now
            )
        }
        albumImageDao.addImagesToAlbum(entities)
        if (photoUris.isNotEmpty()) {
            albumDao.updateCover(albumId, photoUris.first(), now)
        }
    }

    override suspend fun removePhotoFromAlbum(albumId: Long, photoUri: String) {
        albumImageDao.removeImageFromAlbum(albumId, photoUri)
    }

    override fun getAlbumImageCount(albumId: Long): Flow<Int> {
        return albumImageDao.getAlbumImageCount(albumId)
    }

    private fun AlbumEntity.toDomain(): Album {
        return Album(
            id = id,
            name = name,
            coverImageUri = coverImageUri,
            photoCount = 0,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
