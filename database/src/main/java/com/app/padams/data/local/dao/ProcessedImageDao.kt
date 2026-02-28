package com.app.padams.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.padams.data.local.entity.ProcessedImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProcessedImageDao {
    @Query("SELECT EXISTS(SELECT 1 FROM processed_images WHERE image_uri = :uri)")
    suspend fun isProcessed(uri: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markProcessed(entity: ProcessedImageEntity)

    @Query("SELECT COUNT(*) FROM processed_images")
    fun getProcessedCount(): Flow<Int>

    @Query("SELECT image_uri FROM processed_images")
    suspend fun getAllProcessedUris(): List<String>
}
