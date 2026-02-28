package com.app.padams.domain.model

data class Album(
    val id: Long,
    val name: String,
    val coverImageUri: String?,
    val photoCount: Int,
    val createdAt: Long,
    val updatedAt: Long
)
