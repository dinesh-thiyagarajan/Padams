package com.app.padams.ml

import javax.inject.Inject

class FaceClusterEngine @Inject constructor() {

    companion object {
        const val SIMILARITY_THRESHOLD = 0.65f
    }

    data class ClusterResult(
        val clusterId: Int,
        val occurrenceIds: List<Long>,
        val centroidEmbedding: FloatArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as ClusterResult
            return clusterId == other.clusterId
        }

        override fun hashCode(): Int = clusterId
    }

    fun cluster(occurrences: List<Pair<Long, FloatArray>>): List<ClusterResult> {
        if (occurrences.isEmpty()) return emptyList()

        val clusters = occurrences.indices.map { mutableListOf(it) }.toMutableList()
        val embeddings = occurrences.map { it.second }
        val n = embeddings.size

        val similarities = Array(n) { i ->
            FloatArray(n) { j ->
                if (i == j) 1.0f
                else EmbeddingUtils.cosineSimilarity(embeddings[i], embeddings[j])
            }
        }

        while (true) {
            var bestSim = -1.0f
            var bestI = -1
            var bestJ = -1

            for (i in clusters.indices) {
                if (clusters[i].isEmpty()) continue
                for (j in i + 1 until clusters.size) {
                    if (clusters[j].isEmpty()) continue
                    val avgSim = averageLinkage(clusters[i], clusters[j], similarities)
                    if (avgSim > bestSim) {
                        bestSim = avgSim
                        bestI = i
                        bestJ = j
                    }
                }
            }

            if (bestSim < SIMILARITY_THRESHOLD || bestI == -1) break

            clusters[bestI].addAll(clusters[bestJ])
            clusters[bestJ].clear()
        }

        return clusters
            .filter { it.isNotEmpty() }
            .mapIndexed { clusterId, memberIndices ->
                val ids = memberIndices.map { occurrences[it].first }
                val memberEmbeddings = memberIndices.map { embeddings[it] }
                val centroid = EmbeddingUtils.computeCentroid(memberEmbeddings)
                ClusterResult(clusterId, ids, centroid)
            }
    }

    fun findBestGroup(
        embedding: FloatArray,
        groupCentroids: List<Pair<Long, FloatArray>>
    ): Long? {
        var bestGroupId: Long? = null
        var bestSimilarity = SIMILARITY_THRESHOLD

        for ((groupId, centroid) in groupCentroids) {
            val sim = EmbeddingUtils.cosineSimilarity(embedding, centroid)
            if (sim > bestSimilarity) {
                bestSimilarity = sim
                bestGroupId = groupId
            }
        }
        return bestGroupId
    }

    private fun averageLinkage(
        cluster1: List<Int>,
        cluster2: List<Int>,
        similarities: Array<FloatArray>
    ): Float {
        var sum = 0f
        var count = 0
        for (i in cluster1) {
            for (j in cluster2) {
                sum += similarities[i][j]
                count++
            }
        }
        return if (count > 0) sum / count else 0f
    }
}
