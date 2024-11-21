package com.example.ontime.data.auth

import android.content.Context
import android.util.Log
import javax.inject.Inject

class AuthManager @Inject constructor(
    private val context: Context
) {
    //
    private val prefs = context.getSharedPreferences("authInfo", Context.MODE_PRIVATE)

    //
//
    fun saveAuthInfo(accessToken: String, refreshToken: String, expiresIn: Int, userId: String) {
        Log.d("ITM", "Saving user ID: $userId")

        // SharedPreferences 사용 (웹의 local storage와 비슷)
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putInt(
                KEY_EXPIRES_IN, expiresIn
            ).putString(KEY_USER_ID, userId)
            .apply()
        val savedUserId = getUserId()
        Log.d("ITM", "Verified saved user ID: $savedUserId")

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

    //    토큰 가져오기
    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)

    fun getExpiresIn(): Long = prefs.getLong(KEY_EXPIRES_IN, 0)

    // 로그아웃 (토큰 삭제)
    fun clearAuthInfo() {
        prefs.edit().clear().apply()
    }


    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_EXPIRES_IN = "expires_in"
        private const val KEY_USER_ID = "user_id"
    }
}