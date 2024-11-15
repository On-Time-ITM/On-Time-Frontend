package com.example.ontime

import CustomButton
import CustomTextField
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ontime.ui.theme.OnTimeTheme
import com.example.ontime.ui.theme.body_medium
import com.example.ontime.ui.theme.headline_large
import com.example.ontime.ui.theme.onSurface
import com.example.ontime.ui.theme.onSurfaceVariant
import com.example.ontime.ui.theme.surfaceContainerLowest

class SignupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnTimeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        SignUp()
                    }
                }
            }
        }
    }
}

@Composable
fun SignUp() {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Error states
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceContainerLowest)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(), // 왼쪽 정렬을 위한 내부 Column
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Sign Up",
                fontSize = headline_large,
                color = onSurface,
                fontWeight = FontWeight.Bold // 더 bold하게 변경
            )

            Text(
                text = "Welcome to the Tardiness Prevention Challenge!",
                fontSize = body_medium,
                color = onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )
        }


        CustomTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = null
            },
            label = "Name",
            error = nameError,
            imeAction = ImeAction.Next,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CustomTextField(
            value = phone,
            onValueChange = {
                phone = it
                phoneError = null
            },
            label = "Phone Number",
            error = phoneError,
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CustomTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
                if (confirmPassword.isNotEmpty() && it != confirmPassword) {
                    confirmPasswordError = "Passwords do not match"
                } else {
                    confirmPasswordError = null
                }
            },
            label = "Password",
            error = passwordError,
            isPassword = true,
            imeAction = ImeAction.Next,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CustomTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                if (it != password) {
                    confirmPasswordError = "Passwords do not match"
                } else {
                    confirmPasswordError = null
                }
            },
            label = "Confirm Password",
            error = confirmPasswordError,
            isPassword = true,
            imeAction = ImeAction.Done,
            onImeAction = { /* Handle signup */ },
            modifier = Modifier.padding(bottom = 32.dp)
        )

        CustomButton(
            text = "Sign Up",
            onClick = {
                // Validate inputs
                var hasError = false

                if (name.isBlank()) {
                    nameError = "Name is required"
                    hasError = true
                }

                if (phone.isBlank()) {
                    phoneError = "Phone number is required"
                    hasError = true
                }

                if (password.isBlank()) {
                    passwordError = "Password is required"
                    hasError = true
                }

                if (confirmPassword.isBlank()) {
                    confirmPasswordError = "Please confirm your password"
                    hasError = true
                } else if (password != confirmPassword) {
                    confirmPasswordError = "Passwords do not match"
                    hasError = true
                }

                if (!hasError) {
                    isLoading = true
                    // Handle signup
                }
            },
            isLoading = isLoading,
            enabled = name.isNotBlank() &&
                    phone.isNotBlank() &&
                    password.isNotBlank() &&
                    confirmPassword.isNotBlank()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    OnTimeTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding)
            ) {
                SignUp()
            }
        }
    }
}