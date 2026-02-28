package com.app.padams.ml

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.app.padams.data.local.dao.FaceGroupDao
import com.app.padams.data.local.dao.FaceOccurrenceDao
import com.app.padams.data.local.dao.ProcessedImageDao
import com.app.padams.data.local.entity.FaceGroupEntity
import com.app.padams.data.local.entity.FaceOccurrenceEntity
import com.app.padams.data.local.entity.ProcessedImageEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class FaceScanWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val contentResolver: ContentResolver,
    private val faceDetector: FaceDetectorWrapper,
    private val embeddingExtractor: FaceEmbeddingExtractor,
    private val clusterEngine: FaceClusterEngine,
    private val processedImageDao: ProcessedImageDao,
    private val faceOccurrenceDao: FaceOccurrenceDao,
    private val faceGroupDao: FaceGroupDao
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_PROGRESS = "scan_progress"
        const val KEY_TOTAL = "scan_total"
        private const val BATCH_SIZE = 20
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.Default) {
            try {
                val allImageUris = getAllImageUris()
                val processedUris = processedImageDao.getAllProcessedUris().toSet()
                val unprocessed = allImageUris.filter { it.first !in processedUris }

                if (unprocessed.isEmpty()) {
                    return@withContext Result.success()
                }

                val total = unprocessed.size
                var processed = 0

                unprocessed.chunked(BATCH_SIZE).forEach { batch ->
                    batch.forEach { (uriString, mediaStoreId) ->
                        processSingleImage(uriString, mediaStoreId)
                        processed++
                        setProgress(
                            workDataOf(
                                KEY_PROGRESS to processed,
                                KEY_TOTAL to total
                            )
                        )
                    }
                }

                runClustering()
                Result.success()
            } catch (e: Exception) {
                if (runAttemptCount < 3) Result.retry()
                else Result.failure()
            } finally {
                faceDetector.close()
                embeddingExtractor.close()
            }
        }
    }

    private suspend fun processSingleImage(uriString: String, mediaStoreId: Long) {
        val uri = Uri.parse(uriString)
        val bitmap = loadScaledBitmap(uri, 1024) ?: run {
            processedImageDao.markProcessed(
                ProcessedImageEntity(uriString, mediaStoreId, 0, System.currentTimeMillis())
            )
            return
        }

        try {
            val faces = faceDetector.detectFaces(bitmap)

            if (faces.isEmpty()) {
                processedImageDao.markProcessed(
                    ProcessedImageEntity(uriString, mediaStoreId, 0, System.currentTimeMillis())
                )
                return
            }

            val occurrences = faces.mapNotNull { face ->
                try {
                    val embedding = embeddingExtractor.getEmbedding(face.croppedFaceBitmap)
                    val occurrence = FaceOccurrenceEntity(
                        faceGroupId = 0,
                        imageUri = uriString,
                        faceBoundsLeft = face.boundingBox.left,
                        faceBoundsTop = face.boundingBox.top,
                        faceBoundsRight = face.boundingBox.right,
                        faceBoundsBottom = face.boundingBox.bottom,
                        embedding = embedding,
                        confidence = face.confidence,
                        detectedAt = System.currentTimeMillis()
                    )
                    face.croppedFaceBitmap.recycle()
                    occurrence
                } catch (e: Exception) {
                    face.croppedFaceBitmap.recycle()
                    null
                }
            }

            if (occurrences.isNotEmpty()) {
                faceOccurrenceDao.insertOccurrences(occurrences)
            }
            processedImageDao.markProcessed(
                ProcessedImageEntity(uriString, mediaStoreId, faces.size, System.currentTimeMillis())
            )
        } finally {
            bitmap.recycle()
        }
    }

    private suspend fun runClustering() {
        val unassigned = faceOccurrenceDao.getUnassignedOccurrences()
        if (unassigned.isEmpty()) return

        val existingGroups = faceGroupDao.getAllFaceGroupsList()
        val groupCentroids = existingGroups.mapNotNull { group ->
            group.representativeEmbedding?.let { group.id to it }
        }

        if (groupCentroids.isEmpty()) {
            val occurrencePairs = unassigned.map { it.id to it.embedding }
            val clusters = clusterEngine.cluster(occurrencePairs)

            for (cluster in clusters) {
                val now = System.currentTimeMillis()
                val firstOccurrence = unassigned.first { it.id == cluster.occurrenceIds.first() }
                val groupId = faceGroupDao.insertFaceGroup(
                    FaceGroupEntity(
                        name = null,
                        representativeFaceUri = firstOccurrence.imageUri,
                        representativeEmbedding = cluster.centroidEmbedding,
                        photoCount = cluster.occurrenceIds.size,
                        createdAt = now,
                        updatedAt = now
                    )
                )
                for (occId in cluster.occurrenceIds) {
                    faceOccurrenceDao.assignToGroup(occId, groupId)
                }
            }
        } else {
            for (occurrence in unassigned) {
                val bestGroup = clusterEngine.findBestGroup(occurrence.embedding, groupCentroids)
                if (bestGroup != null) {
                    faceOccurrenceDao.assignToGroup(occurrence.id, bestGroup)
                    faceGroupDao.recalculatePhotoCount(bestGroup, System.currentTimeMillis())
                } else {
                    val now = System.currentTimeMillis()
                    val groupId = faceGroupDao.insertFaceGroup(
                        FaceGroupEntity(
                            name = null,
                            representativeFaceUri = occurrence.imageUri,
                            representativeEmbedding = occurrence.embedding,
                            photoCount = 1,
                            createdAt = now,
                            updatedAt = now
                        )
                    )
                    faceOccurrenceDao.assignToGroup(occurrence.id, groupId)
                }
            }
        }
    }

    private fun loadScaledBitmap(uri: Uri, maxDimension: Int): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            }
            val (w, h) = options.outWidth to options.outHeight
            if (w <= 0 || h <= 0) return null

            var sampleSize = 1
            while (w / sampleSize > maxDimension || h / sampleSize > maxDimension) {
                sampleSize *= 2
            }
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, decodeOptions)
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getAllImageUris(): List<Pair<String, Long>> {
        return withContext(Dispatchers.IO) {
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            val projection = arrayOf(MediaStore.Images.Media._ID)
            val uris = mutableListOf<Pair<String, Long>>()

            contentResolver.query(
                collection,
                projection,
                null,
                null,
                "${MediaStore.Images.Media.DATE_TAKEN} DESC"
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val contentUri = ContentUris.withAppendedId(collection, id)
                    uris.add(contentUri.toString() to id)
                }
            }
            uris
        }
    }
}
