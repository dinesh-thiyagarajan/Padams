package com.app.padams.domain.usecase.photo

import com.app.padams.domain.repository.PhotoRepository
import javax.inject.Inject

class DeletePhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(uri: String): Boolean {
        return photoRepository.deletePhoto(uri)
    }
}
