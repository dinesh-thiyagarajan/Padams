package com.app.padams.presentation.screens.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.padams.domain.model.Album
import com.app.padams.domain.usecase.album.CreateAlbumUseCase
import com.app.padams.domain.usecase.album.DeleteAlbumUseCase
import com.app.padams.domain.usecase.album.GetAlbumsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    getAlbumsUseCase: GetAlbumsUseCase,
    private val createAlbumUseCase: CreateAlbumUseCase,
    private val deleteAlbumUseCase: DeleteAlbumUseCase
) : ViewModel() {

    val albums: StateFlow<List<Album>> = getAlbumsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    fun showCreateAlbumDialog() {
        _showCreateDialog.value = true
    }

    fun hideCreateAlbumDialog() {
        _showCreateDialog.value = false
    }

    fun createAlbum(name: String) {
        viewModelScope.launch {
            createAlbumUseCase(name)
            _showCreateDialog.value = false
        }
    }

    fun deleteAlbum(albumId: Long) {
        viewModelScope.launch {
            deleteAlbumUseCase(albumId)
        }
    }
}
