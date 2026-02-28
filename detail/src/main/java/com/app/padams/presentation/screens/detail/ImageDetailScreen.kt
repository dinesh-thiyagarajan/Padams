package com.app.padams.presentation.screens.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.padams.presentation.components.ErrorState
import com.app.padams.presentation.components.LoadingState
import com.app.padams.presentation.components.ZoomableImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDetailScreen(
    onBackClick: () -> Unit,
    viewModel: ImageDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        if (uiState is ImageDetailUiState.Deleted) {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val state = uiState
                    if (state is ImageDetailUiState.Success) {
                        Text(
                            text = state.photo.displayName,
                            maxLines = 1,
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.5f)
                )
            )
        },
        bottomBar = {
            if (uiState is ImageDetailUiState.Success) {
                BottomAppBar(
                    containerColor = Color.Black.copy(alpha = 0.5f)
                ) {
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "image/*"
                                putExtra(Intent.EXTRA_STREAM, Uri.parse(viewModel.imageUri))
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share photo"))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = "Share", tint = Color.White)
                    }
                    IconButton(
                        onClick = { viewModel.toggleFavorite() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.White
                        )
                    }
                    IconButton(
                        onClick = { viewModel.deletePhoto() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.White)
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is ImageDetailUiState.Loading -> LoadingState()
                is ImageDetailUiState.Error -> ErrorState(
                    message = state.message,
                    onRetry = {}
                )
                is ImageDetailUiState.Success -> {
                    ZoomableImage(
                        imageUri = viewModel.imageUri,
                        contentDescription = state.photo.displayName,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is ImageDetailUiState.Deleted -> {}
            }
        }
    }
}
