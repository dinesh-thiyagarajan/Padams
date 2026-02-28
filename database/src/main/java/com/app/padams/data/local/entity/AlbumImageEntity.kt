package com.app.padams.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "album_images",
    primaryKeys = ["album_id", "image_uri"],
    foreignKeys = [
        ForeignKey(
            entity = AlbumEntity::class,
            parentColumns = ["id"],
            childColumns = ["album_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("album_id"), Index("image_uri")]
)
data class AlbumImageEntity(
    @ColumnInfo(name = "album_id")
    val albumId: Long,

    @ColumnInfo(name = "image_uri")
    val imageUri: String,

    @ColumnInfo(name = "added_at")
    val addedAt: Long
)
