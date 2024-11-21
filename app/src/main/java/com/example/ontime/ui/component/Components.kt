package com.example.ontime.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ontime.R
import com.example.ontime.ui.theme.ButtonText
import com.example.ontime.ui.theme.ErrorColor
import com.example.ontime.ui.theme.InputBackground
import com.example.ontime.ui.theme.MainColor
import com.example.ontime.ui.theme.body_large
import com.example.ontime.ui.theme.shadow

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
            .height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MainColor,
            contentColor = ButtonText
        ),
        enabled = enabled && !isLoading // 로딩 중일 때 버튼 비활성화
    ) {
        if (isLoading) {
            // 로딩 중일 때 CircularProgressIndicator 표시
            CircularProgressIndicator(
                color = ButtonText,
                modifier = Modifier.size(24.dp)
            )
        } else {
            // 로딩 중이 아닐 때 텍스트 표시
            Text(text = text, fontSize = body_large)
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
        Image(
            painter = painterResource(id = R.drawable.profile_icon),
            contentDescription = "Settings",
            modifier = Modifier.size(28.dp)
        )
    }
}