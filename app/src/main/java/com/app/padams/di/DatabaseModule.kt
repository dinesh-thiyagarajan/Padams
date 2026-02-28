package com.app.padams.di

import android.content.Context
import androidx.room.Room
import com.app.padams.data.local.dao.AlbumDao
import com.app.padams.data.local.dao.AlbumImageDao
import com.app.padams.data.local.dao.FaceGroupDao
import com.app.padams.data.local.dao.FaceOccurrenceDao
import com.app.padams.data.local.dao.FavoriteDao
import com.app.padams.data.local.dao.ProcessedImageDao
import com.app.padams.data.local.db.PadamsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): PadamsDatabase {
        return Room.databaseBuilder(
            context,
            PadamsDatabase::class.java,
            "padams_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideFavoriteDao(db: PadamsDatabase): FavoriteDao = db.favoriteDao()

    @Provides
    fun provideAlbumDao(db: PadamsDatabase): AlbumDao = db.albumDao()

    @Provides
    fun provideAlbumImageDao(db: PadamsDatabase): AlbumImageDao = db.albumImageDao()

    @Provides
    fun provideFaceGroupDao(db: PadamsDatabase): FaceGroupDao = db.faceGroupDao()

    @Provides
    fun provideFaceOccurrenceDao(db: PadamsDatabase): FaceOccurrenceDao = db.faceOccurrenceDao()

    @Provides
    fun provideProcessedImageDao(db: PadamsDatabase): ProcessedImageDao = db.processedImageDao()
}
