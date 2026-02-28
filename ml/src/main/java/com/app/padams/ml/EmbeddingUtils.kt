package com.app.padams.ml

import kotlin.math.sqrt

object EmbeddingUtils {
    fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        require(a.size == b.size) { "Embedding dimensions must match" }
        var dot = 0f
        var normA = 0f
        var normB = 0f
        for (i in a.indices) {
            dot += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }
        val denom = sqrt(normA) * sqrt(normB)
        return if (denom > 0) dot / denom else 0f
    }

    fun computeCentroid(embeddings: List<FloatArray>): FloatArray {
        if (embeddings.isEmpty()) return FloatArray(0)
        val dim = embeddings.first().size
        val centroid = FloatArray(dim)
        for (emb in embeddings) {
            for (i in centroid.indices) centroid[i] += emb[i]
        }
        for (i in centroid.indices) centroid[i] /= embeddings.size
        val norm = sqrt(centroid.map { it * it }.sum())
        if (norm > 0) for (i in centroid.indices) centroid[i] /= norm
        return centroid
    }
}
