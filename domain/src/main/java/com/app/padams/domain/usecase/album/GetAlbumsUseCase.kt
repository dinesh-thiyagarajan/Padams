package com.app.padams.domain.usecase.album

import com.app.padams.domain.model.Album
import com.app.padams.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    operator fun invoke(): Flow<List<Album>> {
        return albumRepository.getAlbums()
    }
}
