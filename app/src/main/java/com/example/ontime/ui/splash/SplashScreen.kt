package com.example.ontime.ui.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ontime.R
import com.example.ontime.ui.theme.MainColor
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // 커스텀 폰트 정의
    val warningFont = FontFamily(
        Font(R.font.bebas_neue_regular)
    )
    val warningFont2 = FontFamily(
        Font(R.font.roboto_bold)
    )

    var startWarningAnimation by remember { mutableStateOf(false) }
    var showLogo by remember { mutableStateOf(false) }

    // Warning text animations
    val warningScale = animateFloatAsState(
        targetValue = if (startWarningAnimation) 1.2f else 0.8f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "Warning Scale"
    )

    val warningAlpha = animateFloatAsState(
        targetValue = if (startWarningAnimation) 1f else 0f,
        animationSpec = tween(500),
        label = "Warning Alpha"
    )

    // Logo and app name animations
    val logoAlpha = animateFloatAsState(
        targetValue = if (showLogo) 1f else 0f,
        animationSpec = tween(1000),
        label = "Logo Alpha"
    )

    val logoScale = animateFloatAsState(
        targetValue = if (showLogo) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "Logo Scale"
    )

    // Animation sequence control
    LaunchedEffect(key1 = true) {
        startWarningAnimation = true
        delay(2000)
        startWarningAnimation = false
        delay(500)
        showLogo = true
        delay(2000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Warning Text with custom font
        if (!showLogo) {
            Text(
                text = "DON'T BE LATE!",
                fontSize = 38.sp,
                fontFamily = warningFont2,
                color = MainColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .scale(warningScale.value)
                    .alpha(warningAlpha.value)
                    .padding(horizontal = 32.dp)
            )
        }

        // Logo and App Name
        if (showLogo) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ontime_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(120.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "On Time!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A4A4A)
                )
            }
        }
    }
}