package com.example.ontime.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface StabilityApi {
    @Headers("Content-Type: application/json")
    @POST("v1/generation/stable-diffusion-xl-1024-v1-0/text-to-image")
    suspend fun generateImage(
        @Body request: ImageGenerationRequest
    ): Response<ImageGenerationResponse>
}

data class ImageGenerationRequest(
    @SerializedName("text_prompts")
    val textPrompts: List<TextPrompt>,
    @SerializedName("cfg_scale")
    val cfgScale: Int = 7,
    @SerializedName("height")
    val height: Int = 1024,
    @SerializedName("width")
    val width: Int = 1024,
    @SerializedName("samples")
    val samples: Int = 1,
    @SerializedName("steps")
    val steps: Int = 30
)

data class TextPrompt(
    @SerializedName("text")
    val text: String,
    @SerializedName("weight")
    val weight: Int = 1
)

data class ImageGenerationResponse(
    @SerializedName("artifacts")
    val artifacts: List<Artifact>
)

data class Artifact(
    @SerializedName("base64")
    val base64: String,
    @SerializedName("seed")
    val seed: Long,
    @SerializedName("finish_reason")
    val finishReason: String
)
