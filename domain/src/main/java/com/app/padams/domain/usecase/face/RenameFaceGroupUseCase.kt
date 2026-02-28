package com.app.padams.domain.usecase.face

import com.app.padams.domain.repository.FaceRepository
import javax.inject.Inject

class RenameFaceGroupUseCase @Inject constructor(
    private val faceRepository: FaceRepository
) {
    suspend operator fun invoke(groupId: Long, name: String) {
        faceRepository.renameFaceGroup(groupId, name.trim())
    }
}
