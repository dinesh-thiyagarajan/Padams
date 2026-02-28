package com.app.padams.ml

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class FaceDetectorWrapper @Inject constructor() {

    data class DetectedFace(
        val boundingBox: RectF,
        val confidence: Float,
        val croppedFaceBitmap: Bitmap
    )

    private val detector: FaceDetector by lazy {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setMinFaceSize(0.1f)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .build()
        FaceDetection.getClient(options)
    }

    suspend fun detectFaces(bitmap: Bitmap): List<DetectedFace> {
        return suspendCancellableCoroutine { continuation ->
            val inputImage = InputImage.fromBitmap(bitmap, 0)
            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    val results = faces.mapNotNull { face ->
                        val box = face.boundingBox
                        val normalizedBox = RectF(
                            box.left.toFloat() / bitmap.width,
                            box.top.toFloat() / bitmap.height,
                            box.right.toFloat() / bitmap.width,
                            box.bottom.toFloat() / bitmap.height
                        )
                        val croppedFace = cropAndResizeFace(bitmap, box) ?: return@mapNotNull null
                        DetectedFace(
                            boundingBox = normalizedBox,
                            confidence = 0.95f,
                            croppedFaceBitmap = croppedFace
                        )
                    }
                    continuation.resume(results)
                }
                .addOnFailureListener {
                    continuation.resume(emptyList())
                }
        }
    }

    private fun cropAndResizeFace(bitmap: Bitmap, box: Rect): Bitmap? {
        val padding = 0.2f
        val padX = (box.width() * padding).toInt()
        val padY = (box.height() * padding).toInt()

        val left = (box.left - padX).coerceAtLeast(0)
        val top = (box.top - padY).coerceAtLeast(0)
        val right = (box.right + padX).coerceAtMost(bitmap.width)
        val bottom = (box.bottom + padY).coerceAtMost(bitmap.height)

        val width = right - left
        val height = bottom - top
        if (width <= 0 || height <= 0) return null

        return try {
            val cropped = Bitmap.createBitmap(bitmap, left, top, width, height)
            Bitmap.createScaledBitmap(cropped, 112, 112, true)
        } catch (e: Exception) {
            null
        }
    }

    fun close() {
        detector.close()
    }
}
