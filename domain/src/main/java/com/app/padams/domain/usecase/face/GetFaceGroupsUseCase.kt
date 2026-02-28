package com.app.padams.domain.usecase.face

import com.app.padams.domain.model.FaceGroup
import com.app.padams.domain.repository.FaceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFaceGroupsUseCase @Inject constructor(
    private val faceRepository: FaceRepository
) {
    operator fun invoke(): Flow<List<FaceGroup>> {
        return faceRepository.getFaceGroups()
    }
}
