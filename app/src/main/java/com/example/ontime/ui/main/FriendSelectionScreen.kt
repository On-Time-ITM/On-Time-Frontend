package com.example.ontime.ui.main


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ontime.data.model.response.FriendResponse
import com.example.ontime.ui.component.AppBar
import com.example.ontime.ui.friend.friendList.FriendListViewModel
import com.example.ontime.ui.theme.MainColor

//
//@Composable
//fun FriendSelectionScreen(
//    onBackClick: () -> Unit,
//    onFriendsSelected: (List<String>) -> Unit,
//    viewModel: InviteFriendsViewModel = hiltViewModel()
//) {
//    val uiState by viewModel.uiState.collectAsState()
//    val context = LocalContext.current
//
//    LaunchedEffect(uiState.isSuccess, uiState.errorMessage) {
//        when {
//            uiState.isSuccess -> {
//                // 선택된 친구들의 ID 리스트를 전달
//                val selectedFriendIds = uiState.friends
//                    .filter { it.isSelected }
//                    .map { it.id }
//                onFriendsSelected(selectedFriendIds)
//                onBackClick()
//            }
//
//            uiState.errorMessage != null -> {
//                Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(surfaceContainerLowest)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f)
//                .padding(horizontal = 24.dp)
//        ) {
//            Text(
//                text = "Friends",
//                style = MaterialTheme.typography.titleMedium,
//                modifier = Modifier.padding(vertical = 16.dp)
//            )
//
//            if (uiState.isLoading) {
//                CircularProgressIndicator(
//                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                )
//            } else {
//                LazyColumn {
//                    items(
//                        items = uiState.friends,
//                        key = { it.id }
//                    ) { friend ->
//                        FriendItem(
//                            friend = friend,
//                            onFriendClick = { viewModel.toggleFriendSelection(friend.id) }
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.weight(1f))
//
//            Button(
//                onClick = { viewModel.inviteSelectedFriends("meeting_id") },
//                enabled = !uiState.isLoading && uiState.friends.any { it.isSelected },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 16.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MainColor,
//                    disabledContainerColor = MainColor.copy(alpha = 0.5f)
//                )
//            ) {
//                if (uiState.isLoading) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(24.dp),
//                        color = MaterialTheme.colorScheme.onPrimary
//                    )
//                } else {
//                    Text(
//                        text = "Confirm",
//                        color = surfaceContainerLowest,
//                        fontSize = body_large,
//                        modifier = Modifier.padding(vertical = 8.dp)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun FriendItem(
//    friend: FriendUiState,
//    onFriendClick: (String) -> Unit
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Checkbox(
//            checked = friend.isSelected,
//            onCheckedChange = { onFriendClick(friend.id) }
//        )
//
//        Column(
//            modifier = Modifier
//                .weight(1f)
//                .padding(start = 16.dp)
//        ) {
//            Text(
//                text = friend.name,
//                fontSize = title_medium,
//                color = shadow
//            )
//            Text(
//                text = "${friend.tardinessRate}%",
//                fontSize = label_small,
//                color = shadow
//            )
//        }
//    }
//}

@Composable
fun FriendSelectionScreen(
    viewModel: FriendListViewModel,
    modifier: Modifier = Modifier,
//    onNavigateToDirectAdd: () -> Unit,

) {
    val uiState = viewModel.uiState
    LaunchedEffect(Unit) {
        viewModel.getFriendsList()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppBar()
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Friends",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
//                // 친구 요청 확인 버튼을 헤더 옆으로 이동
//                RequestButton(
//                    onClick = onNavigateToRequests,
//                    requestCount = 3   // 요청 개수를 viewModel에서 가져오도록 수정 필요
//                )
            }

            // 친구 추가 버튼 그룹
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
//                AddFriendButton(
//                    onClick = onNavigateToDirectAdd,
//                    icon = Icons.Rounded.Person,
//                    text = "Add by Number",
//                    modifier = Modifier.weight(1f)
//                )

//                AddFriendButton(
//                    onClick = onNavigateToContactList,
//                    icon = Icons.Rounded.Call,
//                    text = "From Contacts",
//                    modifier = Modifier.weight(1f)
//                )
            }

            // 친구 목록
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            } else if (uiState.friends.isEmpty()) {
                EmptyFriendsList()
            } else {
                LazyColumn {
                    items(uiState.friends) { friend ->
                        FriendsRow(friends = friend)
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

@Composable
private fun RequestButton(
    onClick: () -> Unit,
    requestCount: Int,
    modifier: Modifier = Modifier
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = if (requestCount > 0) MainColor else Color.Gray.copy(alpha = 0.6f),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Notifications,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        if (requestCount > 0) {
//            Text(
//                text = requestCount.toString(),
//                fontSize = 14.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(start = 4.dp)
//            )
        }
        Text(
            text = "requests",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
private fun AddFriendButton(
    onClick: () -> Unit,
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MainColor
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EmptyFriendsList() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.Person,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(bottom = 8.dp),
            tint = Color.Gray
        )
        Text(
            text = "No friends yet",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}
//
//@Composable
//fun FriendListScreen(
//    viewModel: FriendListViewModel,
//    modifier: Modifier = Modifier,
////    onNavigateBack: () -> Unit,
//    onNavigateToDirectAdd: () -> Unit, // 직접 전화번호 입력 페이지로 이동
//    onNavigateToContactList: () -> Unit, // 연락처 리스트 페이지로 이동
//    onNavigateToRequests: () -> Unit // 친구 요청 페이지로 이동
//) {
//    val uiState = viewModel.uiState
//    LaunchedEffect(Unit) {
//        viewModel.getFriendsList()
//    }
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background)
//    ) {
//        AppBar()
//        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
//            Text(
//                text = "Friends",
//                fontSize = 24.sp,
//                color = MaterialTheme.colorScheme.onBackground,
//                modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
//            )
//
//
//            // 기존 친구 목록 표시 부분
//            if (uiState.isLoading) {
//                CircularProgressIndicator(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .wrapContentWidth(Alignment.CenterHorizontally)
//                )
//            } else if (uiState.friends.isEmpty()) {
//                Text(
//                    text = "No contacts found.",
//                    fontSize = 16.sp,
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 32.dp)
//                )
//            } else {
//                LazyColumn {
//                    items(uiState.friends) { friend ->
//                        FriendsRow(friends = friend)
//                        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
//                    }
//                }
//            }
//            // 버튼 그룹
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 16.dp),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                // 직접 추가 버튼
//                FriendActionButton(
//                    onClick = onNavigateToDirectAdd,
//                    icon = Icons.Rounded.Person,
//                    text = "Add Friend",
//                    modifier = Modifier.weight(1f)
//                )
//
//                // 연락처에서 추가 버튼
//                FriendActionButton(
//                    onClick = onNavigateToContactList,
//                    icon = Icons.Rounded.Call,
//                    text = "From Contacts",
//                    modifier = Modifier.weight(1f)
//                )
//
//
//            }
//            Row {
//                FriendActionButton(
//                    onClick = onNavigateToRequests,
//                    icon = Icons.Rounded.Check,
//                    text = "Accept Requests",
//                    modifier = Modifier.weight(1f)
//                )
//            }
//            // 친구 요청 확인 버튼
//
//        }
//    }
//}
//
//@Composable
//private fun FriendActionButton(
//    onClick: () -> Unit,
//    icon: ImageVector,
//    text: String,
//    modifier: Modifier = Modifier
//) {
//    ElevatedButton(
//        onClick = onClick,
//        modifier = modifier
//            .height(48.dp),
//        colors = ButtonDefaults.elevatedButtonColors(
//            containerColor = MainColor,
//            contentColor = Color.White
//        ),
//        elevation = ButtonDefaults.elevatedButtonElevation(
//            defaultElevation = 6.dp
//        )
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
////            Icon(
////                imageVector = icon,
////                contentDescription = null,
////                modifier = Modifier.size(24.dp)
////            )
//            Text(
//                text = text,
//                fontSize = 12.sp,
//                modifier = Modifier.padding(top = 4.dp)
//            )
//        }
//    }
//}

@Composable
fun FriendsRow(
    friends: FriendResponse
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = friends.name,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = friends.phoneNumber,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(end = 16.dp)
        )
    }
}
