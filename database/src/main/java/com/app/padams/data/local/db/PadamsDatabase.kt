package com.app.padams.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.padams.data.local.dao.AlbumDao
import com.app.padams.data.local.dao.AlbumImageDao
import com.app.padams.data.local.dao.FaceGroupDao
import com.app.padams.data.local.dao.FaceOccurrenceDao
import com.app.padams.data.local.dao.FavoriteDao
import com.app.padams.data.local.dao.ProcessedImageDao
import com.app.padams.data.local.entity.AlbumEntity
import com.app.padams.data.local.entity.AlbumImageEntity
import com.app.padams.data.local.entity.FaceGroupEntity
import com.app.padams.data.local.entity.FaceOccurrenceEntity
import com.app.padams.data.local.entity.FavoriteEntity
import com.app.padams.data.local.entity.ProcessedImageEntity

@Database(
    entities = [
        FavoriteEntity::class,
        AlbumEntity::class,
        AlbumImageEntity::class,
        FaceGroupEntity::class,
        FaceOccurrenceEntity::class,
        ProcessedImageEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class PadamsDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun albumDao(): AlbumDao
    abstract fun albumImageDao(): AlbumImageDao
    abstract fun faceGroupDao(): FaceGroupDao
    abstract fun faceOccurrenceDao(): FaceOccurrenceDao
    abstract fun processedImageDao(): ProcessedImageDao
}
