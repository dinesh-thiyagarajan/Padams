package com.app.padams.presentation.screens.people

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.padams.domain.model.FaceGroup
import com.app.padams.presentation.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleScreen(
    onPersonClick: (Long) -> Unit,
    viewModel: PeopleViewModel = hiltViewModel()
) {
    val faceGroups by viewModel.faceGroups.collectAsStateWithLifecycle()
    val scanProgress by viewModel.scanProgress.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("People") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (scanProgress.isScanning) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Scanning faces: ${scanProgress.progress}/${scanProgress.total}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = {
                            if (scanProgress.total > 0) {
                                scanProgress.progress.toFloat() / scanProgress.total
                            } else 0f
                        },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            if (faceGroups.isEmpty() && !scanProgress.isScanning) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    EmptyState(
                        icon = Icons.Outlined.People,
                        title = "No people found",
                        subtitle = "Start a face scan to group photos by person",
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { viewModel.startFaceScan() },
                        modifier = Modifier.padding(bottom = 32.dp)
                    ) {
                        Text("Start Face Scan")
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = faceGroups,
                        key = { it.id }
                    ) { faceGroup ->
                        FaceGroupItem(
                            faceGroup = faceGroup,
                            onClick = { onPersonClick(faceGroup.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FaceGroupItem(
    faceGroup: FaceGroup,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        if (faceGroup.representativeFaceUri != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(Uri.parse(faceGroup.representativeFaceUri))
                    .size(200)
                    .crossfade(true)
                    .build(),
                contentDescription = faceGroup.name ?: "Person",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
        } else {
            Icon(
                Icons.Filled.Face,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = faceGroup.name ?: "Unknown",
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            text = "${faceGroup.photoCount} photos",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
