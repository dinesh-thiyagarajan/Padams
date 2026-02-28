package com.app.padams.ml

import android.content.Context
import android.graphics.Bitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import kotlin.math.sqrt

class FaceEmbeddingExtractor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val MODEL_FILE = "mobilefacenet.tflite"
        private const val INPUT_SIZE = 112
        private const val EMBEDDING_DIM = 192
    }

    private var interpreter: Interpreter? = null

    private fun getInterpreter(): Interpreter {
        if (interpreter == null) {
            interpreter = try {
                val model = loadModelFile()
                val options = Interpreter.Options().apply {
                    setNumThreads(4)
                }
                Interpreter(model, options)
            } catch (e: Exception) {
                throw RuntimeException("Failed to load TFLite model: ${e.message}", e)
            }
        }
        return interpreter!!
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(MODEL_FILE)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun getEmbeddingDimension(): Int {
        return try {
            val interp = getInterpreter()
            val outputTensor = interp.getOutputTensor(0)
            outputTensor.shape().last()
        } catch (e: Exception) {
            EMBEDDING_DIM
        }
    }

    fun getEmbedding(faceBitmap: Bitmap): FloatArray {
        val interp = getInterpreter()
        val inputBuffer = preprocessBitmap(faceBitmap)
        val embeddingDim = getEmbeddingDimension()
        val outputBuffer = Array(1) { FloatArray(embeddingDim) }
        interp.run(inputBuffer, outputBuffer)
        return l2Normalize(outputBuffer[0])
    }

    private fun preprocessBitmap(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        val resized = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)
        val input = Array(1) { Array(INPUT_SIZE) { Array(INPUT_SIZE) { FloatArray(3) } } }

        for (y in 0 until INPUT_SIZE) {
            for (x in 0 until INPUT_SIZE) {
                val pixel = resized.getPixel(x, y)
                input[0][y][x][0] = ((pixel shr 16 and 0xFF) / 127.5f) - 1.0f
                input[0][y][x][1] = ((pixel shr 8 and 0xFF) / 127.5f) - 1.0f
                input[0][y][x][2] = ((pixel and 0xFF) / 127.5f) - 1.0f
            }
        }
        return input
    }

    private fun l2Normalize(embedding: FloatArray): FloatArray {
        val norm = sqrt(embedding.map { it * it }.sum())
        return if (norm > 0) embedding.map { it / norm }.toFloatArray() else embedding
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }
}
