package com.app.padams.domain.repository

import com.app.padams.domain.model.FaceGroup
import kotlinx.coroutines.flow.Flow

interface FaceRepository {
    fun getFaceGroups(): Flow<List<FaceGroup>>
    suspend fun getFaceGroupById(id: Long): FaceGroup?
    fun getPhotosForFaceGroup(groupId: Long): Flow<List<String>>
    suspend fun renameFaceGroup(groupId: Long, name: String)
    suspend fun mergeGroups(sourceId: Long, targetId: Long)
    fun getScanProgress(): Flow<Int>
}
