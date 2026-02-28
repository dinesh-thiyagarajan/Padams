package com.app.padams.presentation.screens.albums

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.padams.presentation.components.EmptyState
import com.app.padams.presentation.components.PhotoGridItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    onPhotoClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: AlbumDetailViewModel = hiltViewModel()
) {
    val album by viewModel.album.collectAsStateWithLifecycle()
    val photos by viewModel.photos.collectAsStateWithLifecycle()
    val favoriteUris by viewModel.favoriteUris.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(album?.name ?: "Album") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        if (photos.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.Photo,
                title = "No photos in this album",
                subtitle = "Add photos to this album from the Photos tab"
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(2.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = photos,
                    key = { it.uri }
                ) { photo ->
                    PhotoGridItem(
                        photo = photo,
                        isFavorite = photo.uri in favoriteUris,
                        onClick = { onPhotoClick(photo.uri) },
                        modifier = Modifier.padding(1.dp)
                    )
                }
            }
        }
    }
}
