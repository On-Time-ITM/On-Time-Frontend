package com.example.ontime

import android.app.Application
import com.example.ontime.data.api.FcmApi
import com.example.ontime.data.auth.AuthManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
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
    }
}