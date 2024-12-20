package com.example.ontime.data.api

import com.example.ontime.data.model.request.LoginRequest
import com.example.ontime.data.model.request.SignupRequest
import com.example.ontime.data.model.response.LoginResponse
import com.example.ontime.data.model.response.SignupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("/api/v1/auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>

    @POST("/api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/v1/auth/logout")
    suspend fun logout(@Query("userId") userId: String): Response<Unit>
}