package com.app.padams.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    @ColumnInfo(name = "image_uri")
    val imageUri: String,

    @ColumnInfo(name = "media_store_id")
    val mediaStoreId: Long,

    @ColumnInfo(name = "date_added")
    val dateAdded: Long
)
