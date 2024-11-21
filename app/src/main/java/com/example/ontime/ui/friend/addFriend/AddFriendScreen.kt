package com.example.ontime.ui.friend.addFriend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.ontime.test.TestViewModel
import com.example.ontime.ui.component.AppBar
import com.example.ontime.ui.component.CustomButton
import com.example.ontime.ui.theme.ButtonText
import com.example.ontime.ui.theme.MainColor
import com.example.ontime.ui.theme.OnTimeTheme
import com.example.ontime.ui.theme.body_medium
import com.example.ontime.ui.theme.shadow
import com.example.ontime.ui.theme.surfaceContainerLowest


@Composable
fun AddFriendScreen(
    viewModel: AddFriendViewModel,
    modifier: Modifier = Modifier
) {
    var uiState = viewModel.uiState
    var showPhoneNumberDialog by remember { mutableStateOf(false) }


    // 전화번호 포맷팅 (예: 010-1234-5678)
    fun formatPhoneNumber(number: String): String {
        return when (number.length) {
            11 -> "${number.substring(0, 3)}-${number.substring(3, 7)}-${number.substring(7)}"
            else -> number
        }
    }

    // 성공 또는 에러 메시지를 보여주기 위한 상태
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            // 성공 시 다이얼로그 닫기
            showPhoneNumberDialog = false
            // TODO: 성공 메시지 표시
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceContainerLowest)
    ) {
        AppBar()

//        Button(onClick = { viewModel.addFriend(phoneNumber = phoneNumber) }) {
//
//        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            PhoneNumberCard(
                text = if (uiState.phoneNumber.isBlank()) "Enter your friend's phone number!" else formatPhoneNumber(
                    uiState.phoneNumber
                ),
                onClick = { showPhoneNumberDialog = true })
            Spacer(modifier = modifier.height(50.dp))
            CustomButton(
                text = "Send Request",
                onClick = { viewModel.addFriend(phoneNumber = uiState.phoneNumber) },
                isLoading = uiState.isLoading,
                enabled = uiState.phoneNumber.length == 11,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = modifier.height(100.dp))
        }
    }
    // 에러 메시지 표시
    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                Button(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }
    PhoneNumberInputDialog(
        showDialog = showPhoneNumberDialog,
        onDismiss = { showPhoneNumberDialog = false },
        currentPhoneNumber = uiState.phoneNumber,
        onPhoneNumberChange = { viewModel.onPhoneNumberChange(it) },
        phoneNumberError = viewModel.uiState.phoneNumberError,
        onConfirm = {
            showPhoneNumberDialog = false
        }
    )

}


@Composable
private fun PhoneNumberCard(
    text: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(Color.Transparent)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center  // Box 내부 콘텐츠를 중앙 정렬
        ) {
            Text(
                text = text,
                color = shadow,
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )

        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = 5.dp))
    }
}

@Composable
private fun PhoneNumberInputDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    currentPhoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    onConfirm: () -> Unit,
    phoneNumberError: String? = null  // 에러 메시지 파라미터 추가
) {

    // 전화번호 포맷 검증 함수
    fun isValidPhoneNumber(number: String): Boolean {
        return number.matches(Regex("^\\d{11}$"))  // 예: 01012345678
    }

    val isValidNumber = currentPhoneNumber.length == 11


    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            title = {
                Column {
                    Text(
                        "Enter Phone Number",
                        fontSize = 20.sp,
                        color = shadow,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                }
            },
            text = {
                Column {
                    TextField(
                        value = currentPhoneNumber,
                        onValueChange = onPhoneNumberChange,
                        placeholder = { Text("Enter Phone Number", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone
                        ),
                        isError = phoneNumberError != null,  // 에러 상태 표시
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0x1FE9E9E9),
                            focusedContainerColor = Color(0x1FE9E9E9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorContainerColor = Color(0x1FE9E9E9),  // 에러 상태의 배경색
                            errorIndicatorColor = Color.Red  // 에러 상태의 테두리 색
                        ),
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                    if (phoneNumberError != null) {
                        Text(
                            text = phoneNumberError,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .width(100.dp)
                            .border(1.dp, MainColor, RoundedCornerShape(10.dp))
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MainColor
                            ),
                            modifier = Modifier.fillMaxSize(), // Box 크기에 맞춤
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Cancel", fontSize = body_medium)
                        }
                    }

                    Button(
                        onClick = onConfirm,
                        enabled = isValidNumber,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainColor,
                            contentColor = ButtonText,
                            disabledContainerColor = MainColor.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .height(40.dp)
                            .width(100.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Confirm", fontSize = body_medium)
                    }
                }
            },
            containerColor = surfaceContainerLowest,
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Preview
@Composable
fun preview() {
    val viewModel = TestViewModel()
    OnTimeTheme {
//        AddFriendScreen(onLogout = { /*TODO*/ })
    }
}