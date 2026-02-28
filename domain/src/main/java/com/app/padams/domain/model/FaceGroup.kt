package com.app.padams.domain.model

data class FaceGroup(
    val id: Long,
    val name: String?,
    val representativeFaceUri: String?,
    val photoCount: Int,
    val createdAt: Long
)
