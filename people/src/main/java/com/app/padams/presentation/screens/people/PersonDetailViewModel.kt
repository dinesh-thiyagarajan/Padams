package com.app.padams.presentation.screens.people

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.padams.domain.model.FaceGroup
import com.app.padams.domain.model.Photo
import com.app.padams.domain.repository.FaceRepository
import com.app.padams.domain.repository.FavoriteRepository
import com.app.padams.domain.usecase.face.RenameFaceGroupUseCase
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
class PersonDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val faceRepository: FaceRepository,
    private val renameFaceGroupUseCase: RenameFaceGroupUseCase,
    private val getPhotosByUrisUseCase: GetPhotosByUrisUseCase,
    favoriteRepository: FavoriteRepository
) : ViewModel() {

    val personId: Long = savedStateHandle.get<Long>("personId") ?: 0L

    private val _faceGroup = MutableStateFlow<FaceGroup?>(null)
    val faceGroup: StateFlow<FaceGroup?> = _faceGroup.asStateFlow()

    val photos: StateFlow<List<Photo>> = faceRepository.getPhotosForFaceGroup(personId)
        .map { uris -> getPhotosByUrisUseCase(uris) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteUris: StateFlow<Set<String>> = favoriteRepository.getAllFavoriteUris()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    init {
        viewModelScope.launch {
            _faceGroup.value = faceRepository.getFaceGroupById(personId)
        }
    }

    fun renamePerson(name: String) {
        viewModelScope.launch {
            renameFaceGroupUseCase(personId, name)
            _faceGroup.value = faceRepository.getFaceGroupById(personId)
        }
    }
}
