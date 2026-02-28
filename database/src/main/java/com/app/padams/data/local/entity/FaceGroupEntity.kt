package com.app.padams.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "face_groups")
data class FaceGroupEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String? = null,

    @ColumnInfo(name = "representative_face_uri")
    val representativeFaceUri: String? = null,

    @ColumnInfo(name = "representative_embedding")
    val representativeEmbedding: FloatArray? = null,

    @ColumnInfo(name = "photo_count")
    val photoCount: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FaceGroupEntity
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
