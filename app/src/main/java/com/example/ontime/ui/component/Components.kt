package com.example.ontime.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.ontime.R
import com.example.ontime.ui.theme.ButtonText
import com.example.ontime.ui.theme.ErrorColor
import com.example.ontime.ui.theme.InputBackground
import com.example.ontime.ui.theme.MainColor
import com.example.ontime.ui.theme.body_medium
import com.example.ontime.ui.theme.shadow
import com.example.ontime.ui.theme.surfaceContainerLowest

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) }, // 텍스트 필드 라벨
            singleLine = true, // 한 줄 입력만 가능
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = InputBackground, // 비포커스 시 배경색
                focusedContainerColor = InputBackground // 포커스 시 배경색
            ),
            shape = RoundedCornerShape(15.dp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType, // 키보드 타입 설정
                imeAction = imeAction // IME 액션 설정
            ),
            keyboardActions = KeyboardActions(
                onDone = { onImeAction() } // IME 액션 처리
            ),
            isError = error != null, // 에러 여부
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
        )
        // 에러 메시지 표시
        if (error != null) {
            Text(
                text = error,
                color = ErrorColor,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}


@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MainColor,
            contentColor = ButtonText,
            disabledContainerColor = MainColor.copy(alpha = 0.6f),
            disabledContentColor = ButtonText.copy(alpha = 0.8f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 1.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        ),
        enabled = enabled && !isLoading
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = ButtonText,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}

@Composable
fun AppBar() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ontime_logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(41.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            "OnTime!",
            fontSize = 20.sp,
            color = shadow,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
//        Image(
//            painter = painterResource(id = R.drawable.profile_icon),
//            contentDescription = "Settings",
//            modifier = Modifier.size(28.dp)
//        )
    }
}


@Composable
fun CustomDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    title: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    confirmEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            title = {
                Column {
                    Text(
                        title,
                        fontSize = 20.sp,
                        color = shadow,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                }
            },
            text = { content() },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
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
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Cancel", fontSize = body_medium)
                        }
                    }

                    Button(
                        onClick = onConfirm,
                        enabled = confirmEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainColor,
                            contentColor = ButtonText,
                            disabledContainerColor = MainColor.copy(alpha = 0.6f)
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

@Composable
fun TitleInputDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    currentTitle: String,
    onTitleChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    var tempTitle by remember { mutableStateOf(currentTitle) }

    CustomDialog(
        showDialog = showDialog,
        onDismiss = onDismiss,
        title = "Enter Schedule Title",
        onConfirm = {
            onTitleChange(tempTitle)
            onConfirm()
        }
    ) {
        TextField(
            value = tempTitle,
            onValueChange = { tempTitle = it },
            placeholder = { Text("Enter title", color = Color.Gray) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0x1FE9E9E9),
                focusedContainerColor = Color(0x1FE9E9E9),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}
