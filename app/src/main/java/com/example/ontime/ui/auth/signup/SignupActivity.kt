package com.example.ontime.ui.auth.signup

import CustomButton
import CustomTextField
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ontime.ui.main.MainActivity
import com.example.ontime.ui.theme.OnTimeTheme
import com.example.ontime.ui.theme.body_medium
import com.example.ontime.ui.theme.headline_large
import com.example.ontime.ui.theme.onSurface
import com.example.ontime.ui.theme.onSurfaceVariant
import com.example.ontime.ui.theme.surfaceContainerLowest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupActivity : ComponentActivity() {
    private val viewModel: SignupViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnTimeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        SignUp(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun SignUp(viewModel: SignupViewModel) {
    // 사용자 입력값을 저장할 상태 변수들
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // 로딩 상태

    // 각 입력필드의 에러 상태를 저장할 변수들
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // ViewModel의 회원가입 상태를 관찰
    val signupState by viewModel.signupState.collectAsState()

    val context = LocalContext.current

    // 회원가입 상태에 따른 UI 업데이트 처리. useEffect와 비슷한 역할
    LaunchedEffect(signupState) {
        when (signupState) {
            is SignupState.Loading -> {
                isLoading = true  // 로딩 시작
            }

            is SignupState.Success -> {
                isLoading = false  // 로딩 종료
                // MainActivity로 이동하고 현재 액티비티 종료
                context.startActivity(Intent(context, MainActivity::class.java).apply {
                    // 백스택 클리어 (뒤로 가기 했을 때 회원가입 화면으로 돌아오지 않도록)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }

            is SignupState.Error -> {
                isLoading = false  // 로딩 종료
                // 에러 발생 시 로그 출력
//                Log.d("ITM", "Error: ${(signupState as SignupState.Error).message}")
                // 에러 메시지를 토스트로 표시
                Toast.makeText(
                    context,
                    (signupState as SignupState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                isLoading = false
            }
        }
    }

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

        // 회원가입 버튼
        CustomButton(
            text = "Sign Up",
            onClick = {
                // 입력값 유효성 검사
                var hasError = false

                // 이름 필드 검사
                if (name.isBlank()) {
                    nameError = "Name is required"
                    hasError = true
                }

                // 전화번호 필드 검사
                if (phone.isBlank()) {
                    phoneError = "Phone number is required"
                    hasError = true
                }

                // 비밀번호 필드 검사
                if (password.isBlank()) {
                    passwordError = "Password is required"
                    hasError = true
                }

                // 비밀번호 확인 필드 검사
                if (confirmPassword.isBlank()) {
                    confirmPasswordError = "Please confirm your password"
                    hasError = true
                } else if (password != confirmPassword) {
                    confirmPasswordError = "Passwords do not match"
                    hasError = true
                }

                // 에러가 없으면 회원가입 API 호출
                if (!hasError) {
                    viewModel.signup(name = name, phoneNumber = phone, password = password)
                }
            },
            isLoading = isLoading,
            // 모든 필드가 입력되었을 때만 버튼 활성화
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
//                SignUp()
            }
        }
    }
}