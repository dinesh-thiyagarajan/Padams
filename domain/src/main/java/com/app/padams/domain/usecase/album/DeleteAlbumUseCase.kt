package com.app.padams.domain.usecase.album

import com.app.padams.domain.repository.AlbumRepository
import javax.inject.Inject

class DeleteAlbumUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    suspend operator fun invoke(albumId: Long) {
        albumRepository.deleteAlbum(albumId)
    }
}
