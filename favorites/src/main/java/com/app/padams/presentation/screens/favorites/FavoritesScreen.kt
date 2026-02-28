package com.app.padams.presentation.screens.favorites

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
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
fun FavoritesScreen(
    onPhotoClick: (String) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val photos by viewModel.favoritePhotos.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Favorites") })

        if (photos.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.FavoriteBorder,
                title = "No favorites yet",
                subtitle = "Tap the heart icon on a photo to add it to favorites"
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
                        isFavorite = true,
                        onClick = { onPhotoClick(photo.uri) },
                        modifier = Modifier.padding(1.dp)
                    )
                }
            }
        }
    }
}
