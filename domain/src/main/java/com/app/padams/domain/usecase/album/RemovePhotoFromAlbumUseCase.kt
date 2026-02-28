package com.app.padams.domain.usecase.album

import com.app.padams.domain.repository.AlbumRepository
import javax.inject.Inject

class RemovePhotoFromAlbumUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    suspend operator fun invoke(albumId: Long, photoUri: String) {
        albumRepository.removePhotoFromAlbum(albumId, photoUri)
    }
}
