package com.example.ontime.data.auth

import android.content.Context
import javax.inject.Inject

class AuthManager @Inject constructor(
    private val context: Context
) {
    //
//    private val tokenPreferences = context.getSharedPreferences("tokens", Context.MODE_PRIVATE)
//
//
    fun saveTokens(accessToken: String, refreshToken: String) {
        // SharedPreferences나 EncryptedSharedPreferences 사용
        return
    }
//
//    // API 호출 시 토큰 확인 및 갱신
//    suspend fun getValidAccessToken(): String {
//        if (isAccessTokenExpired()) {
//            // 리프레시 토큰으로 새 액세스 토큰 요청
//            return refreshAccessToken()
//        }
//        return currentAccessToken
//    }
//
//    // 액세스 토큰이 만료되었을 때
//    suspend fun refreshAccessToken() {
//        val refreshToken = getRefreshToken()
//        try {
//            val response = api.refreshToken(refreshToken)
//            saveTokens(response.accessToken, response.refreshToken)
//        } catch (e: Exception) {
//            // 리프레시 토큰도 만료된 경우 로그아웃 처리
//            logout()
//        }
//    }
}