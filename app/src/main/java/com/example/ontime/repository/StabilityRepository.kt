package com.example.ontime.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.ontime.data.api.ApiClient
import com.example.ontime.data.api.ImageGenerationRequest
import com.example.ontime.data.api.TextPrompt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StabilityRepository @Inject constructor(
    private val apiClient: ApiClient
) {
    suspend fun generateImage(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = ImageGenerationRequest(
                textPrompts = listOf(
                    TextPrompt(
                        text = "A professional team logo with $prompt, modern minimal design, vector style, clean lines",
                        weight = 1
                    )
                ),
                cfgScale = 7,
                height = 1024,
                width = 1024,
                samples = 1,
                steps = 30
            )

            val response = apiClient.stabilityApi.generateImage(request)

            if (response.isSuccessful && response.body() != null) {
                val base64Image = response.body()!!.artifacts.first().base64
                // base64 이미지를 비트맵으로 변환하고 리사이징
                val bitmap = base64ToBitmap(base64Image)
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 512, 512, true)
                val resizedBase64 = bitmapToBase64(resizedBitmap)

                Result.success(resizedBase64)
            } else {
                Result.failure(
                    Exception(
                        "Failed to generate image: ${
                            response.errorBody()?.string()
                        }"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun base64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}