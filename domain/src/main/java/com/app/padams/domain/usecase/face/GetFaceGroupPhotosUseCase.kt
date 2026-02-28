package com.app.padams.domain.usecase.face

import com.app.padams.domain.repository.FaceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFaceGroupPhotosUseCase @Inject constructor(
    private val faceRepository: FaceRepository
) {
    operator fun invoke(groupId: Long): Flow<List<String>> {
        return faceRepository.getPhotosForFaceGroup(groupId)
    }
}
