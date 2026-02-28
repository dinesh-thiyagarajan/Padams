package com.app.padams.presentation.screens.people

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.padams.presentation.components.EmptyState
import com.app.padams.presentation.components.PhotoGridItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
    onPhotoClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: PersonDetailViewModel = hiltViewModel()
) {
    val faceGroup by viewModel.faceGroup.collectAsStateWithLifecycle()
    val photos by viewModel.photos.collectAsStateWithLifecycle()
    val favoriteUris by viewModel.favoriteUris.collectAsStateWithLifecycle()
    var showRenameDialog by remember { mutableStateOf(false) }

    if (showRenameDialog) {
        var name by remember { mutableStateOf(faceGroup?.name ?: "") }
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename Person") },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.renamePerson(name)
                            showRenameDialog = false
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) { Text("Cancel") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(faceGroup?.name ?: "Person") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { showRenameDialog = true }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Rename")
                }
            }
        )

        if (photos.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.Photo,
                title = "No photos",
                subtitle = "No photos found for this person"
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
