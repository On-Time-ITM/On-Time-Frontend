package com.example.ontime.ui.friend.contactList


import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.ontime.ui.component.AppBar
import com.example.ontime.ui.theme.MainColor

@Composable
fun ContactListScreen(
    viewModel: ContactListViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    val uiState = viewModel.uiState

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.fetchContacts()
        }
    }

    LaunchedEffect(Unit) {
        if (viewModel.checkPermission()) {
            viewModel.fetchContacts()
        } else {
            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppBar()
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = "Add Friends",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
            )
            Text(
                text = "Send a request to your friends!",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        color = MainColor
                    )
                }

                uiState.contacts.isEmpty() -> {
                    EmptyContactsList()
                }

                else -> {
                    LazyColumn {
                        items(uiState.contacts) { contact ->
                            ContactRow(
                                contact = contact,
                                onAddFriend = { viewModel.addFriend(contact.phoneNumber) }
                            )
                            HorizontalDivider(
                                color = Color.LightGray.copy(alpha = 0.5f),
                                thickness = 0.5.dp
                            )
                        }
                    }
                }
            }
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
            title = when {
                error.contains("Duplicate friend request") -> "Already Friends"
                else -> "Notice"
            },
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
            onDismiss = { viewModel.clearSuccess() }
        )
    }
}

@Composable
private fun EmptyContactsList() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.Phone,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(bottom = 8.dp),
            tint = Color.Gray
        )
        Text(
            text = "No contacts found",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

// 성공/실패에 따라 다른 아이콘을 표시하는 다이얼로그
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
//                Icon(
//                    imageVector = if (isSuccess) Icons.Rounded.CheckCircle else Icons.Rounded.Warning,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(48.dp)
//                        .padding(bottom = 8.dp),
//                    tint = if (isSuccess) MainColor else Color.Red
//                )
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
fun ContactRow(
    contact: Contact,
    onAddFriend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = contact.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = contact.phoneNumber,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        ElevatedButton(
            onClick = onAddFriend,
            modifier = Modifier
                .wrapContentWidth()
                .height(36.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MainColor,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 16.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 4.dp
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = "Send Request",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}