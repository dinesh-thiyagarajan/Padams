package com.app.padams.domain.usecase.photo

import com.app.padams.domain.model.Photo
import com.app.padams.domain.repository.PhotoRepository
import javax.inject.Inject

class GetPhotosByUrisUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(uris: List<String>): List<Photo> {
        return uris.mapNotNull { photoRepository.getPhotoByUri(it) }
    }
}
