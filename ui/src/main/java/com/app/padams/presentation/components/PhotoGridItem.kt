package com.app.padams.presentation.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.padams.domain.model.Photo

@Composable
fun PhotoGridItem(
    photo: Photo,
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(Uri.parse(photo.uri))
                .size(300)
                .crossfade(true)
                .memoryCacheKey(photo.uri)
                .diskCacheKey(photo.uri)
                .build(),
            contentDescription = photo.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (isFavorite) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Favorite",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .size(16.dp)
            )
        }
    }
}
