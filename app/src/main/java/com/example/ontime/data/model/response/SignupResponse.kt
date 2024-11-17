package com.example.ontime.data.model.response


// SignupResponse.kt
data class SignupResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int,
    val userId: String
)