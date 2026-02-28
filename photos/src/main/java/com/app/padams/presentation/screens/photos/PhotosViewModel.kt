package com.app.padams.presentation.screens.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.app.padams.domain.model.Photo
import com.app.padams.domain.repository.FavoriteRepository
import com.app.padams.domain.usecase.favorite.ToggleFavoriteUseCase
import com.app.padams.domain.usecase.photo.GetPagedPhotosUseCase
import com.app.padams.util.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PhotoListItem {
    data class PhotoItem(val photo: Photo) : PhotoListItem()
    data class DateHeader(val label: String) : PhotoListItem()
}

@HiltViewModel
class PhotosViewModel @Inject constructor(
    getPagedPhotosUseCase: GetPagedPhotosUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    favoriteRepository: FavoriteRepository
) : ViewModel() {

    val pagedItems: Flow<PagingData<PhotoListItem>> = getPagedPhotosUseCase()
        .map { pagingData ->
            pagingData.map { PhotoListItem.PhotoItem(it) as PhotoListItem }
        }
        .map { pagingData ->
            pagingData.insertSeparators { before, after ->
                val beforeDate = (before as? PhotoListItem.PhotoItem)?.photo?.dateTaken
                    ?.let { DateFormatter.formatDateLabel(it) }
                val afterDate = (after as? PhotoListItem.PhotoItem)?.photo?.dateTaken
                    ?.let { DateFormatter.formatDateLabel(it) }
                if (afterDate != null && afterDate != beforeDate) {
                    PhotoListItem.DateHeader(afterDate)
                } else null
            }
        }
        .cachedIn(viewModelScope)

    val favoriteUris: StateFlow<Set<String>> = favoriteRepository
        .getAllFavoriteUris()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun toggleFavorite(photo: Photo) {
        viewModelScope.launch {
            toggleFavoriteUseCase(photo)
        }
    }
}
