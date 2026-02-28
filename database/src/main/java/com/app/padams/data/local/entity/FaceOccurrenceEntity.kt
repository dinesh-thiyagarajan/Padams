package com.app.padams.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "face_occurrences",
    foreignKeys = [
        ForeignKey(
            entity = FaceGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["face_group_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("face_group_id"), Index("image_uri")]
)
data class FaceOccurrenceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "face_group_id")
    val faceGroupId: Long,

    @ColumnInfo(name = "image_uri")
    val imageUri: String,

    @ColumnInfo(name = "face_bounds_left")
    val faceBoundsLeft: Float,

    @ColumnInfo(name = "face_bounds_top")
    val faceBoundsTop: Float,

    @ColumnInfo(name = "face_bounds_right")
    val faceBoundsRight: Float,

    @ColumnInfo(name = "face_bounds_bottom")
    val faceBoundsBottom: Float,

    @ColumnInfo(name = "embedding")
    val embedding: FloatArray,

    @ColumnInfo(name = "confidence")
    val confidence: Float,

    @ColumnInfo(name = "detected_at")
    val detectedAt: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FaceOccurrenceEntity
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
