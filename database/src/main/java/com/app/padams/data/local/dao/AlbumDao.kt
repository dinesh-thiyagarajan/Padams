package com.app.padams.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.app.padams.data.local.entity.AlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Query("SELECT * FROM albums ORDER BY updated_at DESC")
    fun getAllAlbums(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums WHERE id = :albumId")
    suspend fun getAlbumById(albumId: Long): AlbumEntity?

    @Insert
    suspend fun insertAlbum(album: AlbumEntity): Long

    @Update
    suspend fun updateAlbum(album: AlbumEntity)

    @Query("DELETE FROM albums WHERE id = :albumId")
    suspend fun deleteAlbum(albumId: Long)

    @Query("UPDATE albums SET cover_image_uri = :uri, updated_at = :timestamp WHERE id = :albumId")
    suspend fun updateCover(albumId: Long, uri: String, timestamp: Long)
}
