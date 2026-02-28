package com.app.padams.domain.usecase.photo

import com.app.padams.domain.model.Photo
import com.app.padams.domain.repository.PhotoRepository
import javax.inject.Inject

class GetPhotoByUriUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(uri: String): Photo? {
        return photoRepository.getPhotoByUri(uri)
    }
}
