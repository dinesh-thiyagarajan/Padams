package com.app.padams.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.padams.data.local.entity.AlbumImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumImageDao {
    @Query("SELECT image_uri FROM album_images WHERE album_id = :albumId ORDER BY added_at DESC")
    fun getAlbumImageUris(albumId: Long): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM album_images WHERE album_id = :albumId")
    fun getAlbumImageCount(albumId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addImageToAlbum(albumImage: AlbumImageEntity)

    @Query("DELETE FROM album_images WHERE album_id = :albumId AND image_uri = :imageUri")
    suspend fun removeImageFromAlbum(albumId: Long, imageUri: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addImagesToAlbum(albumImages: List<AlbumImageEntity>)
}
