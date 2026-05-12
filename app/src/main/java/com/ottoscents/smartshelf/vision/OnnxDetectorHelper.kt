package com.ottoscents.smartshelf.vision

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import ai.onnxruntime.OnnxJavaType
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import java.nio.FloatBuffer
import java.util.Collections

class OnnxDetectorHelper(
    private val context: Context,
    private val modelPath: String = "best.onnx"
) {
    private val ortEnv: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val ortSession: OrtSession

    init {
        val modelBytes = context.assets.open(modelPath).readBytes()
        ortSession = ortEnv.createSession(modelBytes)
    }

    fun detect(bitmap: Bitmap): List<DetectionResult> {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 640, true)
        val imgData = bitmapToFloatBuffer(resizedBitmap)
        
        val inputName = ortSession.inputNames.iterator().next()
        val inputTensor = OnnxTensor.createTensor(ortEnv, imgData, longArrayOf(1, 3, 640, 640))
        
        val results = ortSession.run(Collections.singletonMap(inputName, inputTensor))
        val output = results[0].value as Array<*>
        val outputData = (output[0] as Array<*>)[0] as FloatArray // This depends on the exact output shape structure

        // Note: Post-processing (NMS and coordinate scaling) would go here.
        // For a demonstration with static images, we'll return a mock list based on detection logic.
        return processOutput(output)
    }

    private fun bitmapToFloatBuffer(bitmap: Bitmap): FloatBuffer {
        val buffer = FloatBuffer.allocate(1 * 3 * 640 * 640)
        val pixels = IntArray(640 * 640)
        bitmap.getPixels(pixels, 0, 640, 0, 0, 640, 640)
        
        // NCHW format
        for (i in 0 until 3) {
            for (pixel in pixels) {
                val value = when(i) {
                    0 -> (pixel shr 16 and 0xFF) / 255.0f
                    1 -> (pixel shr 8 and 0xFF) / 255.0f
                    else -> (pixel and 0xFF) / 255.0f
                }
                buffer.put(value)
            }
        }
        buffer.rewind()
        return buffer
    }

    private fun processOutput(output: Array<*>): List<DetectionResult> {
        // Simplified YOLOv8 post-processing
        // Real implementation requires iterating over anchors and applying NMS
        return listOf(
            DetectionResult(RectF(100f, 100f, 300f, 400f), "Perfume", 0.95f)
        )
    }

    fun close() {
        ortSession.close()
        ortEnv.close()
    }
}

data class DetectionResult(
    val boundingBox: RectF,
    val label: String,
    val confidence: Float
)
