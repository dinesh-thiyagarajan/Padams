package com.app.padams.data.repository

import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.app.padams.data.mediastore.MediaStoreDataSource
import com.app.padams.data.mediastore.MediaStorePagingSource
import com.app.padams.domain.model.Photo
import com.app.padams.domain.repository.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    private val mediaStoreDataSource: MediaStoreDataSource
) : PhotoRepository {

    override fun getPagedPhotos(): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 80,
                prefetchDistance = 40,
                initialLoadSize = 80,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MediaStorePagingSource(contentResolver) }
        ).flow
    }

    override suspend fun getPhotoByUri(uri: String): Photo? {
        return withContext(Dispatchers.IO) {
            mediaStoreDataSource.getPhotoByUri(uri)
        }
    }

    override suspend fun deletePhoto(uri: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val photoUri = Uri.parse(uri)
                contentResolver.delete(photoUri, null, null) > 0
            } catch (e: SecurityException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && e is RecoverableSecurityException) {
                    throw e
                }
                false
            }
        }
    }

    override fun getAllImageUris(): Flow<List<Pair<String, Long>>> {
        return mediaStoreDataSource.getAllImageUris()
    }
}
