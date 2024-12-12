package com.example.ontime.data.api

import com.example.ontime.data.model.request.FcmTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface FcmApi {
    @POST("/api/v1/messages/token")
    suspend fun saveToken(@Body request: FcmTokenRequest): Response<Unit>

}
