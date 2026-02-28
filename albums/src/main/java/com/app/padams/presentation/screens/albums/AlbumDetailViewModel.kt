package com.app.padams.presentation.screens.albums

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.padams.domain.model.Album
import com.app.padams.domain.model.Photo
import com.app.padams.domain.repository.AlbumRepository
import com.app.padams.domain.repository.FavoriteRepository
import com.app.padams.domain.usecase.album.RemovePhotoFromAlbumUseCase
import com.app.padams.domain.usecase.photo.GetPhotosByUrisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val albumRepository: AlbumRepository,
    private val removePhotoFromAlbumUseCase: RemovePhotoFromAlbumUseCase,
    private val getPhotosByUrisUseCase: GetPhotosByUrisUseCase,
    favoriteRepository: FavoriteRepository
) : ViewModel() {

    val albumId: Long = savedStateHandle.get<Long>("albumId") ?: 0L

    private val _album = MutableStateFlow<Album?>(null)
    val album: StateFlow<Album?> = _album.asStateFlow()

    val photos: StateFlow<List<Photo>> = albumRepository.getAlbumPhotos(albumId)
        .map { uris -> getPhotosByUrisUseCase(uris) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteUris: StateFlow<Set<String>> = favoriteRepository.getAllFavoriteUris()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    init {
        viewModelScope.launch {
            _album.value = albumRepository.getAlbumById(albumId)
        }
    }

    fun removePhoto(photoUri: String) {
        viewModelScope.launch {
            removePhotoFromAlbumUseCase(albumId, photoUri)
        }
    }
}
