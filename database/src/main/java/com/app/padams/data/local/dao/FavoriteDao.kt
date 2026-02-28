package com.app.padams.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.padams.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY date_added DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE image_uri = :uri)")
    fun isFavorite(uri: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE image_uri = :uri")
    suspend fun removeFavorite(uri: String)

    @Query("SELECT image_uri FROM favorites")
    fun getAllFavoriteUris(): Flow<List<String>>
}
