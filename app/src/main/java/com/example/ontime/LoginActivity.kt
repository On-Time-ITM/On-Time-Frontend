package com.example.ontime

import CustomButton
import CustomTextField
import LoginViewModel
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ontime.ui.theme.OnTimeTheme
import com.example.ontime.ui.theme.surfaceContainerLowest


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val loginViewModel: LoginViewModel by viewModels()
        setContent {
            OnTimeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        LoginScreen(
                            onSignUpClick = {
                                // TODO: Navigate to sign up screen
                                val intent = Intent(this, SignupActivity::class.java)
                                startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onSignUpClick: () -> Unit = {}
) {
    val viewModel = remember { LoginViewModel() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceContainerLowest),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(195.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.ontime_logo),
                contentDescription = "logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(195.dp)
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(34.dp))

            // Phone number input
            CustomTextField(
                value = viewModel.phoneNumber,
                onValueChange = viewModel::onPhoneNumberChanged,
                label = "Phone Number",
                error = viewModel.phoneNumberError,
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
            )

            Spacer(modifier = Modifier.height(19.dp))

            // Password input
            CustomTextField(
                value = viewModel.password,
                onValueChange = viewModel::onPasswordChanged,
                label = "Password",
                error = viewModel.passwordError,
                isPassword = true,
                imeAction = ImeAction.Done,
                onImeAction = {
                    keyboardController?.hide()
                    viewModel.onLoginClick()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Login button
            CustomButton(
                text = "Login",
                onClick = viewModel::onLoginClick,
                isLoading = viewModel.isLoading,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            // Sign up button
            CustomButton(
                text = "Sign Up",
                onClick = onSignUpClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    OnTimeTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Surface(modifier = Modifier.padding(innerPadding)) {
                LoginScreen(
                    onSignUpClick = {
                        // TODO: Navigate to sign up screen
                    }
                )
            }
        }
    }
}