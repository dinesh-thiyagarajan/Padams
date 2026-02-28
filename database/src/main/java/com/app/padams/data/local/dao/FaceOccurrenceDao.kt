package com.app.padams.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.app.padams.data.local.entity.FaceOccurrenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FaceOccurrenceDao {
    @Query("SELECT DISTINCT image_uri FROM face_occurrences WHERE face_group_id = :groupId")
    fun getImageUrisForGroup(groupId: Long): Flow<List<String>>

    @Insert
    suspend fun insertOccurrence(occurrence: FaceOccurrenceEntity): Long

    @Insert
    suspend fun insertOccurrences(occurrences: List<FaceOccurrenceEntity>)

    @Query("SELECT * FROM face_occurrences WHERE image_uri = :imageUri")
    suspend fun getOccurrencesForImage(imageUri: String): List<FaceOccurrenceEntity>

    @Query("SELECT * FROM face_occurrences WHERE face_group_id = :groupId")
    suspend fun getAllOccurrencesInGroup(groupId: Long): List<FaceOccurrenceEntity>

    @Query("UPDATE face_occurrences SET face_group_id = :targetGroupId WHERE face_group_id = :sourceGroupId")
    suspend fun mergeGroups(sourceGroupId: Long, targetGroupId: Long)

    @Query("SELECT * FROM face_occurrences WHERE face_group_id = 0")
    suspend fun getUnassignedOccurrences(): List<FaceOccurrenceEntity>

    @Query("UPDATE face_occurrences SET face_group_id = :groupId WHERE id = :occurrenceId")
    suspend fun assignToGroup(occurrenceId: Long, groupId: Long)
}
