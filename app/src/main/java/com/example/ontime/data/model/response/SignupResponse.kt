package com.example.ontime.data.model.response

data class SignupResponse(
    val userInfo: UserInfoResponse,
    val tokenInfo: TokenInfoResponse
) {
    // 토큰 만료 시점 계산
//    val expiresAt: Long
//        get() = System.currentTimeMillis() + (expiresIn * 1000L)
//
//    // 토큰 만료 여부 확인
//    fun isTokenExpired(): Boolean {
//        return System.currentTimeMillis() >= expiresAt
//    }
}