package com.app.padams.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "processed_images")
data class ProcessedImageEntity(
    @PrimaryKey
    @ColumnInfo(name = "image_uri")
    val imageUri: String,

    @ColumnInfo(name = "media_store_id")
    val mediaStoreId: Long,

    @ColumnInfo(name = "face_count")
    val faceCount: Int,

    @ColumnInfo(name = "processed_at")
    val processedAt: Long
)
