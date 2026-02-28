package com.app.padams.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.app.padams.data.local.entity.FaceGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FaceGroupDao {
    @Query("SELECT * FROM face_groups WHERE photo_count > 0 ORDER BY photo_count DESC")
    fun getAllFaceGroups(): Flow<List<FaceGroupEntity>>

    @Query("SELECT * FROM face_groups WHERE id = :groupId")
    suspend fun getFaceGroupById(groupId: Long): FaceGroupEntity?

    @Insert
    suspend fun insertFaceGroup(group: FaceGroupEntity): Long

    @Update
    suspend fun updateFaceGroup(group: FaceGroupEntity)

    @Query("DELETE FROM face_groups WHERE id = :groupId")
    suspend fun deleteFaceGroup(groupId: Long)

    @Query("UPDATE face_groups SET name = :name, updated_at = :timestamp WHERE id = :groupId")
    suspend fun renameFaceGroup(groupId: Long, name: String, timestamp: Long)

    @Query("UPDATE face_groups SET photo_count = (SELECT COUNT(DISTINCT image_uri) FROM face_occurrences WHERE face_group_id = :groupId), updated_at = :timestamp WHERE id = :groupId")
    suspend fun recalculatePhotoCount(groupId: Long, timestamp: Long)

    @Query("SELECT * FROM face_groups")
    suspend fun getAllFaceGroupsList(): List<FaceGroupEntity>
}
