package com.app.padams.presentation.screens.detail

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.padams.domain.model.Photo
import com.app.padams.domain.repository.FavoriteRepository
import com.app.padams.domain.usecase.favorite.ToggleFavoriteUseCase
import com.app.padams.domain.usecase.photo.DeletePhotoUseCase
import com.app.padams.domain.usecase.photo.GetPhotoByUriUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ImageDetailUiState {
    data object Loading : ImageDetailUiState()
    data class Success(val photo: Photo) : ImageDetailUiState()
    data class Error(val message: String) : ImageDetailUiState()
    data object Deleted : ImageDetailUiState()
}

@HiltViewModel
class ImageDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPhotoByUriUseCase: GetPhotoByUriUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase,
    favoriteRepository: FavoriteRepository
) : ViewModel() {

    val imageUri: String = Uri.decode(savedStateHandle.get<String>("imageUri") ?: "")

    val isFavorite: StateFlow<Boolean> = favoriteRepository.isFavorite(imageUri)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _uiState = MutableStateFlow<ImageDetailUiState>(ImageDetailUiState.Loading)
    val uiState: StateFlow<ImageDetailUiState> = _uiState.asStateFlow()

    init {
        loadPhoto()
    }

    private fun loadPhoto() {
        viewModelScope.launch {
            try {
                val photo = getPhotoByUriUseCase(imageUri)
                if (photo != null) {
                    _uiState.value = ImageDetailUiState.Success(photo)
                } else {
                    _uiState.value = ImageDetailUiState.Error("Photo not found")
                }
            } catch (e: Exception) {
                _uiState.value = ImageDetailUiState.Error(e.message ?: "Failed to load photo")
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state is ImageDetailUiState.Success) {
                toggleFavoriteUseCase(state.photo)
            }
        }
    }

    fun deletePhoto() {
        viewModelScope.launch {
            try {
                val deleted = deletePhotoUseCase(imageUri)
                if (deleted) {
                    _uiState.value = ImageDetailUiState.Deleted
                }
            } catch (e: Exception) {
                // SecurityException on Android Q+ requires user confirmation via system dialog
            }
        }
    }
}
