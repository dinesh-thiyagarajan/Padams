package com.app.padams.domain.usecase.album

import com.app.padams.domain.repository.AlbumRepository
import javax.inject.Inject

class AddPhotosToAlbumUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    suspend operator fun invoke(albumId: Long, photoUris: List<String>) {
        albumRepository.addPhotosToAlbum(albumId, photoUris)
    }
}
