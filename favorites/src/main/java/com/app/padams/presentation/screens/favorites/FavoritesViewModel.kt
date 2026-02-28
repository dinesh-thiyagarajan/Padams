package com.app.padams.presentation.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.padams.domain.model.Photo
import com.app.padams.domain.repository.FavoriteRepository
import com.app.padams.domain.usecase.favorite.ToggleFavoriteUseCase
import com.app.padams.domain.usecase.photo.GetPhotosByUrisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getPhotosByUrisUseCase: GetPhotosByUrisUseCase
) : ViewModel() {

    val favoritePhotos: StateFlow<List<Photo>> = favoriteRepository.getFavoriteUris()
        .map { uris -> getPhotosByUrisUseCase(uris) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(photo: Photo) {
        viewModelScope.launch {
            toggleFavoriteUseCase(photo)
        }
    }
}
