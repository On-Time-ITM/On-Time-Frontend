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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.ontime.ui.component.AppBar
import com.example.ontime.ui.component.CustomButton
import com.example.ontime.ui.theme.ButtonText
import com.example.ontime.ui.theme.MainColor
import com.example.ontime.ui.theme.body_medium
import com.example.ontime.ui.theme.shadow
import com.example.ontime.ui.theme.surfaceContainerLowest

@Composable
fun AddFriendScreen(
    viewModel: AddFriendViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit  // 뒤로가기 네비게이션 추가
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

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            showPhoneNumberDialog = false
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceContainerLowest)
    ) {
        AppBar()

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
                enabled = validatePhoneNumber(uiState.phoneNumber),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = modifier.height(100.dp))
        }
    }

    // 에러 다이얼로그
    uiState.error?.let { error ->
        val errorMessage = when {
            error.contains("Invalid request") -> "You cannot send a friend request to yourself"
            error.contains("User not found") -> "This phone number is not registered in the app"
            error.contains("Duplicate friend request") -> "You have already sent a friend request or are already friends"
            else -> "An error occurred. Please try again later"
        }

        StatusDialog(
            title = "Notice",
            message = errorMessage,
            isSuccess = false,
            onDismiss = { viewModel.clearError() }
        )
    }

    // 성공 다이얼로그
    if (uiState.isSuccess) {
        StatusDialog(
            title = "Success",
            message = "Friend request sent successfully!",
            isSuccess = true,
            onDismiss = {
                viewModel.clearSuccess()
                onNavigateBack()
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
private fun StatusDialog(
    title: String,
    message: String,
    isSuccess: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(16.dp),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            }
        },
        text = {
            Text(
                text = message,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainColor,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .height(40.dp)
                        .width(100.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "OK",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp)
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

fun validatePhoneNumber(phone: String): Boolean {
    return when {
        phone.matches(Regex("^01[0-9]-\\d{4}-\\d{4}$")) -> true
        else -> false
    }
}

@Composable
private fun PhoneNumberInputDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    currentPhoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    onConfirm: () -> Unit,
    phoneNumberError: String? = null
) {
    val isValidNumber = validatePhoneNumber(currentPhoneNumber)

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
                            errorContainerColor = Color(0x1FE9E9E9),
                            errorIndicatorColor = Color.Red
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
