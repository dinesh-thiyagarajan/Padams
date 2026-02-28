package com.app.padams.domain.usecase.album

import com.app.padams.domain.repository.AlbumRepository
import javax.inject.Inject

class CreateAlbumUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    suspend operator fun invoke(name: String): Result<Long> {
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("Album name cannot be empty"))
        }
        return try {
            val id = albumRepository.createAlbum(name.trim())
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
