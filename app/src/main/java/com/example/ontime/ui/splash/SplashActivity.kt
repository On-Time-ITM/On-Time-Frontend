package com.example.ontime.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ontime.ui.auth.login.LoginActivity
import com.example.ontime.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Android 12 이상에서만 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                splashScreenView.remove()
            }
        }

        super.onCreate(savedInstanceState)
        setContent {
            val authState by viewModel.authState.collectAsState()

            SplashScreen(
                onSplashFinished = {
                    viewModel.checkAuthState()
                }
            )

            LaunchedEffect(authState) {
                when (authState) {
                    is AuthState.Authenticated -> {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        finish()
                    }

                    is AuthState.Unauthenticated -> {
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        finish()
                    }

                    else -> {} // Loading 상태
                }
            }
        }
    }
}
