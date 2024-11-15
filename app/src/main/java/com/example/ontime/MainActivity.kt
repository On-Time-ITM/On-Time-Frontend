package com.example.ontime

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ontime.ui.theme.MainColor
import com.example.ontime.ui.theme.OnTimeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            OnTimeTheme {
//                MyButton()
                MyButton("Login")
                MyButton("Sign Up")
            }
        }
    }
}

@Composable
fun MyButton(text: String, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    val context = LocalContext.current // Provides context to start new activity

    Button(
        onClick = {  val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent) // Launches activity when card is clicked
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MainColor,  // 버튼 배경 색상
            contentColor = Color.White    // 버튼 텍스트 색상
        ),
        shape = RoundedCornerShape(size = 10.dp),
        modifier = modifier
            .padding(start = 14.dp, top = 20.dp, end = 14.dp, bottom = 20.dp) // 버튼 내부 패딩
            .width(315.dp)   // 버튼 너비
            .height(38.dp)   // 버튼 높이

    ) {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OnTimeTheme {
        Column() {
            MyButton("Login")
            MyButton("Sign Up")
        }
    }
}