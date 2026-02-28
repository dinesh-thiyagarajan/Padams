package com.app.padams.data.mediastore

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.padams.domain.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaStorePagingSource(
    private val contentResolver: ContentResolver
) : PagingSource<Int, Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val page = params.key ?: 0
        val pageSize = params.loadSize

        return try {
            val images = queryMediaStore(
                offset = page * pageSize,
                limit = pageSize
            )

            LoadResult.Page(
                data = images,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (images.size < pageSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    private suspend fun queryMediaStore(offset: Int, limit: Int): List<Photo> {
        return withContext(Dispatchers.IO) {
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

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

            val images = mutableListOf<Photo>()

            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val queryArgs = Bundle().apply {
                    putString(
                        ContentResolver.QUERY_ARG_SQL_SORT_ORDER,
                        "${MediaStore.Images.Media.DATE_TAKEN} DESC"
                    )
                    putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                    putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
                }
                contentResolver.query(collection, projection, queryArgs, null)
            } else {
                contentResolver.query(
                    collection,
                    projection,
                    null,
                    null,
                    "$sortOrder LIMIT $limit OFFSET $offset"
                )
            }?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateTakenCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val dateModCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val widthCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
                val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
                val bucketCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val contentUri = ContentUris.withAppendedId(collection, id)
                    images.add(
                        Photo(
                            id = id,
                            uri = contentUri.toString(),
                            displayName = cursor.getString(nameCol) ?: "",
                            dateTaken = cursor.getLong(dateTakenCol),
                            dateModified = cursor.getLong(dateModCol),
                            size = cursor.getLong(sizeCol),
                            width = cursor.getInt(widthCol),
                            height = cursor.getInt(heightCol),
                            mimeType = cursor.getString(mimeCol) ?: "image/*",
                            bucketName = cursor.getString(bucketCol)
                        )
                    )
                }
            }
            images
        }
    }
}
