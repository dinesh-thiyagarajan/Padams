package com.app.padams.domain.repository

import androidx.paging.PagingData
import com.app.padams.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getPagedPhotos(): Flow<PagingData<Photo>>
    suspend fun getPhotoByUri(uri: String): Photo?
    suspend fun deletePhoto(uri: String): Boolean
    fun getAllImageUris(): Flow<List<Pair<String, Long>>>
}
