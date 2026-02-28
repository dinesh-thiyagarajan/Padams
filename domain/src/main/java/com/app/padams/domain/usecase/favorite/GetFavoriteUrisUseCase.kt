package com.app.padams.domain.usecase.favorite

import com.app.padams.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteUrisUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(): Flow<Set<String>> {
        return favoriteRepository.getAllFavoriteUris()
    }
}
