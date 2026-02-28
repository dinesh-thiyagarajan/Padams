package com.app.padams.presentation.screens.albums

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.PhotoAlbum
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.padams.domain.model.Album
import com.app.padams.presentation.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumsScreen(
    onAlbumClick: (Long) -> Unit,
    viewModel: AlbumsViewModel = hiltViewModel()
) {
    val albums by viewModel.albums.collectAsStateWithLifecycle()
    val showCreateDialog by viewModel.showCreateDialog.collectAsStateWithLifecycle()

    if (showCreateDialog) {
        CreateAlbumDialog(
            onDismiss = { viewModel.hideCreateAlbumDialog() },
            onCreate = { viewModel.createAlbum(it) }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Albums") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showCreateAlbumDialog() }) {
                Icon(Icons.Filled.Add, contentDescription = "Create album")
            }
        }
    ) { innerPadding ->
        if (albums.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.PhotoAlbum,
                title = "No albums yet",
                subtitle = "Create an album to organize your photos",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(
                    items = albums,
                    key = { it.id }
                ) { album ->
                    AlbumGridItem(
                        album = album,
                        onClick = { onAlbumClick(album.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AlbumGridItem(
    album: Album,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            ) {
                if (album.coverImageUri != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(Uri.parse(album.coverImageUri))
                            .size(300)
                            .crossfade(true)
                            .build(),
                        contentDescription = album.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.PhotoAlbum,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Text(
                text = album.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
