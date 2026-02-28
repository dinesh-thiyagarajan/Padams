package com.app.padams.domain.usecase.album

import com.app.padams.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlbumPhotosUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    operator fun invoke(albumId: Long): Flow<List<String>> {
        return albumRepository.getAlbumPhotos(albumId)
    }
}
