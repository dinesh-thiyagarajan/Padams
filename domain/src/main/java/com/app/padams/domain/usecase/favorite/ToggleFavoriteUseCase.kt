package com.app.padams.domain.usecase.favorite

import com.app.padams.domain.model.Photo
import com.app.padams.domain.repository.FavoriteRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(photo: Photo) {
        favoriteRepository.toggleFavorite(photo)
    }
}
