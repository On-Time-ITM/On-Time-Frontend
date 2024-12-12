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
            val request = AddFriendRequest(
                requesterId = userId.toString(),
                receiverPhoneNumber = phoneNumber
            )
            Log.d("ITM", "Request: $request")

            val response = friendApi.addFriend(request)

            if (response.isSuccessful) {


                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = when (response.code()) {
                    400 -> "Invalid request or self-friend request"
                    404 -> "User not found"
                    409 -> "Duplicate friend request"
                    else -> "Unknown error occurred"
                }
                Log.d("ITM", "Status Code: ${response.code()}")
                Log.d("ITM", "Error Body: $errorBody")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("ITM", "Exception occurred", e)
            Result.failure(e)
        }
    }

    fun validatePhoneNumber(phone: String): String? {
        Log.d("ITM", "Validating phone number: $phone") // 전화번호 검증 로깅
        return when {
            phone.isEmpty() -> "Phone number is required"
//            !phone.matches(Regex("^\\d{11}$")) -> "Please enter a 11-digit phone number"
            !phone.matches(Regex("^01[0-9]-\\d{4}-\\d{4}$")) -> "Please enter a 11-digit phone number"
            else -> null
        }
    }
}