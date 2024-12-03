package com.example.ontime.data.model.response

import com.example.ontime.data.model.refreshToken.RefreshToken

data class TokenInfoResponse(
    val accessToken: String,
    val refreshToken: RefreshToken,
    val accessTokenExpiresIn: Int
)
