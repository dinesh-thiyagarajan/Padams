package com.app.padams.presentation.screens.photos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.app.padams.presentation.components.DateHeader
import com.app.padams.presentation.components.EmptyState
import com.app.padams.presentation.components.ErrorState
import com.app.padams.presentation.components.LoadingState
import com.app.padams.presentation.components.PhotoGridItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(
    onPhotoClick: (String) -> Unit,
    viewModel: PhotosViewModel = hiltViewModel()
) {
    val lazyPagingItems = viewModel.pagedItems.collectAsLazyPagingItems()
    val favoriteUris by viewModel.favoriteUris.collectAsStateWithLifecycle()
    val columns = 4

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Photos") })

        when {
            lazyPagingItems.loadState.refresh is LoadState.Loading -> {
                LoadingState()
            }

            lazyPagingItems.loadState.refresh is LoadState.Error -> {
                ErrorState(
                    message = "Failed to load photos",
                    onRetry = { lazyPagingItems.retry() }
                )
            }

            lazyPagingItems.itemCount == 0 &&
                    lazyPagingItems.loadState.refresh is LoadState.NotLoading -> {
                EmptyState(
                    icon = Icons.Outlined.Photo,
                    title = "No photos yet",
                    subtitle = "Photos on your device will appear here"
                )
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    contentPadding = PaddingValues(2.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        count = lazyPagingItems.itemCount,
                        key = { index ->
                            when (val item = lazyPagingItems.peek(index)) {
                                is PhotoListItem.PhotoItem -> "photo_${item.photo.uri}"
                                is PhotoListItem.DateHeader -> "header_${item.label}_$index"
                                null -> "placeholder_$index"
                            }
                        },
                        span = { index ->
                            when (lazyPagingItems.peek(index)) {
                                is PhotoListItem.DateHeader -> GridItemSpan(columns)
                                else -> GridItemSpan(1)
                            }
                        }
                    ) { index ->
                        when (val item = lazyPagingItems[index]) {
                            is PhotoListItem.DateHeader -> {
                                DateHeader(label = item.label)
                            }

                            is PhotoListItem.PhotoItem -> {
                                PhotoGridItem(
                                    photo = item.photo,
                                    isFavorite = item.photo.uri in favoriteUris,
                                    onClick = { onPhotoClick(item.photo.uri) },
                                    modifier = Modifier.padding(1.dp)
                                )
                            }

                            null -> {
                                Box(modifier = Modifier.padding(1.dp))
                            }
                        }
                    }

                    if (lazyPagingItems.loadState.append is LoadState.Loading) {
                        item(span = { GridItemSpan(columns) }) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        }
    }
}
