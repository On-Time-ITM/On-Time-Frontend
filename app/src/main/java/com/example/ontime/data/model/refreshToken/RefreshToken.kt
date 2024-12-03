package com.example.ontime.data.model.refreshToken

data class RefreshToken(
    val id: String,
    val createdDate: String,
    val updatedAt: String?,
    val userId: String,
    val token: String,
    val expiresAt: String,
    val new: Boolean
)

