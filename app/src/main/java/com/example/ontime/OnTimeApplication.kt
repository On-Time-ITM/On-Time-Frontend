package com.example.ontime

import android.app.Application
import android.util.Log
import com.example.ontime.data.api.FcmApi
import com.example.ontime.data.auth.AuthManager
import com.example.ontime.data.model.request.FcmTokenRequest
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class OnTimeApplication : Application() {
    @Inject
    lateinit var fcmApi: FcmApi

    @Inject
    lateinit var authManager: AuthManager

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("ITM", "FCM Token: $token")

                // userId가 있을 때만 토큰 전송
                authManager.getUserId()?.let { userId ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val request = FcmTokenRequest(userId, token)
                            Log.d("ITM", "Attempting to save token: $request")
                            val response = fcmApi.saveToken(request)

                            if (response.isSuccessful) {
                                Log.d("ITM", "Token successfully sent to server")
                            } else {
                                Log.d("ITM", "Failed to send token to server: ${response.code()}")
                            }
                        } catch (e: Exception) {
                            Log.d("ITM", "Error sending token to server", e)
                        }
                    }
                } ?: Log.d("ITM", "UserId is null, cannot save token")
            } else {
                Log.d("ITM", "Failed to get FCM token", task.exception)
            }
        }
    }
}