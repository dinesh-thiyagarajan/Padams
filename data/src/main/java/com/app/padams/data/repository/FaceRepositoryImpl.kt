package com.app.padams.data.repository

import com.app.padams.data.local.dao.FaceGroupDao
import com.app.padams.data.local.dao.FaceOccurrenceDao
import com.app.padams.data.local.dao.ProcessedImageDao
import com.app.padams.domain.model.FaceGroup
import com.app.padams.domain.repository.FaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FaceRepositoryImpl @Inject constructor(
    private val faceGroupDao: FaceGroupDao,
    private val faceOccurrenceDao: FaceOccurrenceDao,
    private val processedImageDao: ProcessedImageDao
) : FaceRepository {

    override fun getFaceGroups(): Flow<List<FaceGroup>> {
        return faceGroupDao.getAllFaceGroups().map { entities ->
            entities.map { entity ->
                FaceGroup(
                    id = entity.id,
                    name = entity.name,
                    representativeFaceUri = entity.representativeFaceUri,
                    photoCount = entity.photoCount,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    override suspend fun getFaceGroupById(id: Long): FaceGroup? {
        return faceGroupDao.getFaceGroupById(id)?.let { entity ->
            FaceGroup(
                id = entity.id,
                name = entity.name,
                representativeFaceUri = entity.representativeFaceUri,
                photoCount = entity.photoCount,
                createdAt = entity.createdAt
            )
        }
    }

    override fun getPhotosForFaceGroup(groupId: Long): Flow<List<String>> {
        return faceOccurrenceDao.getImageUrisForGroup(groupId)
    }

    override suspend fun renameFaceGroup(groupId: Long, name: String) {
        faceGroupDao.renameFaceGroup(groupId, name, System.currentTimeMillis())
    }

    override suspend fun mergeGroups(sourceId: Long, targetId: Long) {
        faceOccurrenceDao.mergeGroups(sourceId, targetId)
        faceGroupDao.recalculatePhotoCount(targetId, System.currentTimeMillis())
        faceGroupDao.deleteFaceGroup(sourceId)
    }

    override fun getScanProgress(): Flow<Int> {
        return processedImageDao.getProcessedCount()
    }
}
