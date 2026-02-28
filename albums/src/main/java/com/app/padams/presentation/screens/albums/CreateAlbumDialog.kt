package com.app.padams.presentation.screens.albums

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateAlbumDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var albumName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Album") },
        text = {
            Column {
                Text("Enter a name for the new album")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = albumName,
                    onValueChange = { albumName = it },
                    label = { Text("Album name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (albumName.isNotBlank()) onCreate(albumName) },
                enabled = albumName.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
