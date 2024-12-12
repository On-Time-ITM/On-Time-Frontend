package com.example.ontime.data.auth

import android.content.Context
import android.util.Log
import com.example.ontime.data.model.response.TokenInfoResponse
import com.example.ontime.data.model.response.UserInfoResponse
import javax.inject.Inject

class AuthManager @Inject constructor(
    private val context: Context
) {
    //
    private val prefs = context.getSharedPreferences("authInfo", Context.MODE_PRIVATE)

    // 토큰 가져오기
    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)

    fun getExpiresIn(): Int = prefs.getInt(KEY_EXPIRES_IN, 0)

    // 새로 추가된 사용자 정보 가져오기
    fun getName(): String? = prefs.getString(KEY_NAME, null)

    fun getPhoneNumber(): String? = prefs.getString(KEY_PHONE_NUMBER, null)

    fun getTardinessRate(): Float = prefs.getFloat(KEY_TARDINESS_RATE, 0f)

    private fun getLoginTimestamp(): Long = prefs.getLong(KEY_LOGIN_TIMESTAMP, 0L)


    fun saveAuthInfo(
        userInfo: UserInfoResponse,
        tokenInfo: TokenInfoResponse
    ) {
        Log.d("ITM", "Saving user info - ID: ${userInfo.id}, Name: ${userInfo.name}")
    
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, tokenInfo.accessToken)
            .putString(KEY_REFRESH_TOKEN, tokenInfo.refreshToken.token)
            .putInt(KEY_EXPIRES_IN, tokenInfo.accessTokenExpiresIn)
            .putString(KEY_USER_ID, userInfo.id)
            .putString(KEY_NAME, userInfo.name)
            .putString(KEY_PHONE_NUMBER, userInfo.phoneNumber)
            .putFloat(KEY_TARDINESS_RATE, userInfo.statistics.lateRate.toFloat())
            .putLong(KEY_LOGIN_TIMESTAMP, System.currentTimeMillis())
            .apply()
    
        val savedUserId = getUserId()
        Log.d("ITM", "Logged in successfully!")
        Log.d("ITM", "Verified saved user ID: $savedUserId")
    }

    fun isLoggedIn(): Boolean {
        val accessToken = getAccessToken()
        val loginTimestamp = getLoginTimestamp()
        val expiresIn = getExpiresIn()

        Log.d(
            "ITM",
            "Checking login status - Token: ${accessToken != null}, Timestamp: $loginTimestamp"
        )

        if (accessToken.isNullOrBlank()) {
            Log.d("ITM", "No access token found")
            return false
        }

        // 토큰 만료 시간 체크
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - loginTimestamp
        val expirationTime = expiresIn * 1000L // 초를 밀리초로 변환

        val isValid = elapsedTime < expirationTime
//        val isValid = true
        Log.d(
            "ITM",
            "Token validity check - Elapsed: $elapsedTime, Expiration: $expirationTime, IsValid: $isValid"
        )

        return isValid
    }
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

    // 사용자 정보를 한 번에 가져오는 데이터 클래스
    data class UserInfo(
        val userId: String,
        val name: String,
        val phoneNumber: String,
        val tardinessRate: Float
    )

    // 저장된 모든 사용자 정보를 한 번에 가져오기
    fun getUserInfo(): UserInfo? {
        val userId = getUserId() ?: return null
        val name = getName() ?: return null
        val phoneNumber = getPhoneNumber() ?: return null
        val tardinessRate = getTardinessRate()

        return UserInfo(userId, name, phoneNumber, tardinessRate)
    }

    // 로그아웃 (모든 정보 삭제)
    fun clearAuthInfo() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_EXPIRES_IN = "expires_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_NAME = "name"
        private const val KEY_PHONE_NUMBER = "phone_number"
        private const val KEY_TARDINESS_RATE = "tardiness_rate"  // 이 필드는 아직 필요한가요?
        private const val KEY_LOGIN_TIMESTAMP = "login_timestamp"
    }
}