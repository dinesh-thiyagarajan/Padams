package com.app.padams.domain.model

data class Photo(
    val id: Long,
    val uri: String,
    val displayName: String,
    val dateTaken: Long,
    val dateModified: Long,
    val size: Long,
    val width: Int,
    val height: Int,
    val mimeType: String,
    val bucketName: String?,
    val isFavorite: Boolean = false
)
