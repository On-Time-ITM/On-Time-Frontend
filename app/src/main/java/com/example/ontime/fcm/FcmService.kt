package com.example.ontime.fcm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ontime.R
import com.example.ontime.data.api.FcmApi
import com.example.ontime.data.auth.AuthManager
import com.example.ontime.data.model.request.FcmTokenRequest
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {
    @Inject
    lateinit var fcmApi: FcmApi

    @Inject
    lateinit var authManager: AuthManager

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        authManager.getUserId()?.let { userId ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = FcmTokenRequest(userId, token)
                    Log.d("ITM", "Saving FCM token: $request")
                    val response = fcmApi.saveToken(request)
                    if (response.isSuccessful) {
                        Log.d("ITM", "Successfully saved FCM token")
                    } else {
                        Log.e("ITM", "Failed to save FCM token: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("ITM", "Failed to save FCM token", e)
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("ITM", "Received FCM message: ${message.data}")

        // 친구 요청 알림 처리
        val title = message.data["title"] ?: message.notification?.title
        val body = message.data["body"] ?: message.notification?.body

        if (!title.isNullOrEmpty() && !body.isNullOrEmpty()) {
            createNotification(title, body)
        }
    }

    private fun createNotification(title: String, body: String) {
        val channelId = "friend_requests"
        createNotificationChannel(channelId)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.profile_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            if (checkNotificationPermission()) {
                NotificationManagerCompat.from(this)
                    .notify(System.currentTimeMillis().toInt(), notification)
                Log.d("ITM", "Notification shown successfully")
            } else {
                Log.d("ITM", "Notification permission not granted")
            }
        } catch (e: Exception) {
            Log.e("ITM", "Failed to show notification", e)
        }
    }

    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Friend Requests",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Friend request notifications"
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            Log.d("ITM", "Notification channel created: $channelId")
        }
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
