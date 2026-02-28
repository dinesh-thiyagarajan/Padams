package com.app.padams.data.mediastore

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.app.padams.domain.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MediaStoreDataSource @Inject constructor(
    private val contentResolver: ContentResolver
) {
    fun getAllImageUris(): Flow<List<Pair<String, Long>>> = flow {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
        )

        val uris = mutableListOf<Pair<String, Long>>()

        contentResolver.query(
            collection,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val contentUri = ContentUris.withAppendedId(collection, id)
                uris.add(contentUri.toString() to id)
            }
        }
        emit(uris)
    }.flowOn(Dispatchers.IO)

    suspend fun getPhotoByUri(uriString: String): Photo? {
        val uri = Uri.parse(uriString)
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        return contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateTakenCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val dateModCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val widthCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
                val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
                val bucketCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

                Photo(
                    id = cursor.getLong(idCol),
                    uri = uriString,
                    displayName = cursor.getString(nameCol) ?: "",
                    dateTaken = cursor.getLong(dateTakenCol),
                    dateModified = cursor.getLong(dateModCol),
                    size = cursor.getLong(sizeCol),
                    width = cursor.getInt(widthCol),
                    height = cursor.getInt(heightCol),
                    mimeType = cursor.getString(mimeCol) ?: "image/*",
                    bucketName = cursor.getString(bucketCol)
                )
            } else null
        }
    }
}
