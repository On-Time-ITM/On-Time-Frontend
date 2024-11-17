package com.example.ontime.data.api

import com.example.ontime.data.model.request.SignupRequest
import com.example.ontime.data.model.response.SignupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/v1/auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>
}