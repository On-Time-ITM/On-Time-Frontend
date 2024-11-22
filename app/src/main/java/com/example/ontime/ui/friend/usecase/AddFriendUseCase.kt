package com.example.ontime.ui.friend.usecase

import android.util.Log
import com.example.ontime.data.api.FriendApi
import com.example.ontime.data.auth.AuthManager
import com.example.ontime.data.model.request.AddFriendRequest
import javax.inject.Inject

class AddFriendUseCase @Inject constructor(
    private val friendApi: FriendApi,
    private val authManager: AuthManager
) {
    suspend fun addFriend(phoneNumber: String): Result<Unit> {
        return try {
            val userId = authManager.getUserId()
            val request =
                AddFriendRequest(requesterId = userId.toString(), receiverPhoneNumber = phoneNumber)
            Log.d("ITM", "Request: $request") // 요청 데이터 로깅

            val response = friendApi.addFriend(request)

            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    Log.d("ITM", "Response: $responseBody") // 성공 응답 로깅
                }
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = when (response.code()) {
                    400 -> "Invalid request or self-friend request"
                    404 -> "User not found" // A005
                    409 -> "Duplicate friend request" // F002
                    else -> "Unknown error occurred"
                }
                Log.d("ITM", "Status Code: ${response.code()}") // 에러 상태 코드 로깅
                Log.d("ITM", "Error Body: $errorBody") // 에러 응답 로깅
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("ITM", "Exception occurred", e) // 예외 로깅
            Result.failure(e)
        }
    }

    fun validatePhoneNumber(phone: String): String? {
        Log.d("ITM", "Validating phone number: $phone") // 전화번호 검증 로깅
        return when {
            phone.isEmpty() -> "Phone number is required"
            !phone.matches(Regex("^\\d{11}$")) -> "Please enter a 11-digit phone number"
            else -> null
        }
    }
}