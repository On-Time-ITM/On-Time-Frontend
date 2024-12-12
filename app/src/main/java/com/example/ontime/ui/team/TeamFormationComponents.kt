package com.example.ontime.ui.team

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ontime.data.model.account.AccountData
import com.example.ontime.ui.component.CustomDialog
import com.example.ontime.ui.theme.ButtonText
import com.example.ontime.ui.theme.MainColor
import com.example.ontime.ui.theme.body_medium

@Composable
fun AccountInputDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    currentAccount: AccountData?,
    onConfirm: (AccountData) -> Unit
) {
    var bankName by remember { mutableStateOf(currentAccount?.bankName ?: "") }
    var accountNumber by remember { mutableStateOf(currentAccount?.accountNumber ?: "") }
    var accountHost by remember { mutableStateOf(currentAccount?.accountHost ?: "") }
    var lateFee by remember { mutableStateOf(currentAccount?.lateFee?.toString() ?: "") }

    CustomDialog(
        showDialog = showDialog,
        onDismiss = onDismiss,
        title = "Enter Bank Account & Late Fee",
        onConfirm = {
            onConfirm(
                AccountData(
                    bankName = bankName,
                    accountNumber = accountNumber,
                    accountHost = accountHost,
                    lateFee = lateFee.toIntOrNull() ?: 0
                )
            )
            onDismiss()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = bankName,
                onValueChange = { bankName = it },
                placeholder = { Text("Bank name", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0x1FE9E9E9),
                    focusedContainerColor = Color(0x1FE9E9E9),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            TextField(
                value = accountNumber,
                onValueChange = { accountNumber = it },
                placeholder = { Text("Account number", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0x1FE9E9E9),
                    focusedContainerColor = Color(0x1FE9E9E9),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            TextField(
                value = accountHost,
                onValueChange = { accountHost = it },
                placeholder = { Text("Account holder", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0x1FE9E9E9),
                    focusedContainerColor = Color(0x1FE9E9E9),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            TextField(
                value = lateFee,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d+$"))) {
                        lateFee = newValue
                    }
                },
                placeholder = { Text("Late fee (won)", color = Color.Gray) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0x1FE9E9E9),
                    focusedContainerColor = Color(0x1FE9E9E9),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@Composable
fun LogoGenerationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    currentLogo: String?,
    viewModel: TeamFormationViewModel
) {
    var prompt by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var generatedImageUrl by remember { mutableStateOf<String?>(currentLogo) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    CustomDialog(
        showDialog = showDialog,
        onDismiss = onDismiss,
        title = "Generate Team Logo",
        onConfirm = {
            generatedImageUrl?.let { url ->
                onConfirm(url)
            }
            onDismiss()
        },
        confirmEnabled = generatedImageUrl != null && !isLoading
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = prompt,
                onValueChange = { prompt = it },
                placeholder = { Text("Describe your team logo...", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0x1FE9E9E9),
                    focusedContainerColor = Color(0x1FE9E9E9),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    viewModel.generateLogo(prompt) { result ->
                        result.fold(
                            onSuccess = { base64 ->
                                generatedImageUrl = base64
                                isLoading = false
                            },
                            onFailure = { error ->
                                errorMessage = error.message
                                isLoading = false
                            }
                        )
                    }
                },
                enabled = prompt.isNotBlank() && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainColor,
                    contentColor = ButtonText
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = ButtonText,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Generate Logo", fontSize = body_medium)
                }
            }

            generatedImageUrl?.let { base64String ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0x1FE9E9E9), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    SafeBase64Image(
                        base64String = base64String,
                        contentDescription = "Generated Logo",
                        modifier = Modifier.size(180.dp)
                    )
                }
            }

            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
