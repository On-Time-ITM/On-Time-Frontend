package com.example.ontime.ui.friend.contactList


import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ontime.ui.component.AppBar
import com.example.ontime.ui.theme.ButtonText
import com.example.ontime.ui.theme.MainColor

@Composable
fun ContactListScreen(
    viewModel: ContactListViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit // 뒤로가기 네비게이션
) {
    val uiState = viewModel.uiState

    // 권한 요청을 위한 launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.fetchContacts()
        }
    }

    // 화면 진입시 권한 체크 및 연락처 가져오기
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
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
            )
            Text(
                text = "Send a request to your friends!",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (uiState.isLoading) {
                // 로딩 표시
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            } else if (uiState.contacts.isEmpty()) {
                Text(
                    text = "No contacts found.",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                )
            } else {
                LazyColumn {
                    items(uiState.contacts) { contact ->
                        ContactRow(
                            contact = contact,
                            onAddFriend = { viewModel.addFriend(contact.phoneNumber) }
                        )
                        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
                    }
                }
            }
        }

    }

    // 에러 다이얼로그
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

    // 성공 다이얼로그
    if (uiState.isSuccess) {
        AlertDialog(
            onDismissRequest = { /* viewModel.clearSuccess() */ },
            title = { Text("Success") },
            text = { Text("Friend added successfully") },
            confirmButton = {
                Button(onClick = { /* viewModel.clearSuccess() */ }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun ContactRow(
    contact: Contact,
    onAddFriend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onAddFriend() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = contact.name,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = contact.phoneNumber,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(end = 16.dp)
        )
        Button(
            onClick = onAddFriend,
            modifier = Modifier
                .wrapContentWidth()
                .height(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MainColor,
                contentColor = ButtonText
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "Add",
                fontSize = 10.sp,
                modifier = Modifier.wrapContentHeight(),
                textAlign = TextAlign.Center
            )
        }
    }
}

//
//@Preview(showBackground = true)
//@Composable
//fun ContactListScreenPreview() {
//    OnTimeTheme {
//        ContactListScreen(
//            viewModel = previewContactListViewModel(),
//            onNavigateBack = {}
//        )
//    }
//}