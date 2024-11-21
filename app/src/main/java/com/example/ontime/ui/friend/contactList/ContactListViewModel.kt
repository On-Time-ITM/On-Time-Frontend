package com.example.ontime.ui.friend.contactList

import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ontime.ui.friend.usecase.AddFriendUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val addFriendUseCase: AddFriendUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {
    data class UiState(
        val contacts: List<Contact> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val isSuccess: Boolean = false,
        val isPermissionGranted: Boolean = false
    )

    var uiState by mutableStateOf(UiState())
        private set

    // 권한 체크 함수 추가
    fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 연락처 가져오는 로직을 suspend 함수로 변경하고 상태 관리 추가
    fun fetchContacts() {
        viewModelScope.launch {
            try {
                if (!checkPermission()) {
                    uiState = uiState.copy(
                        error = "Contacts permission not granted",
                        isPermissionGranted = false
                    )
                    return@launch
                }

                uiState = uiState.copy(isLoading = true)
                val contacts = mutableListOf<Contact>()

                val cursor = context.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    ),
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
                )

                cursor?.use {
                    while (it.moveToNext()) {
                        val name = it.getString(0)
                        val phoneNumber = it.getString(1)
                        contacts.add(Contact(name, phoneNumber))
                    }
                }

                uiState = uiState.copy(
                    contacts = contacts,
                    isLoading = false,
                    isPermissionGranted = true
                )

            } catch (e: Exception) {
                uiState = uiState.copy(
                    error = "Failed to fetch contacts: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    // 친구 추가 기능을 수행하는 함수
    fun addFriend(phoneNumber: String) {
        // 코루틴 스코프에서 비동기 작업 실행
        viewModelScope.launch {
            try {
                // 로딩 상태 시작
                uiState = uiState.copy(isLoading = true)

                // UseCase에 친구 추가 요청을 보내고 결과를 받아옴
                // Result 타입으로 성공/실패 여부를 받게 됨
                val result = addFriendUseCase.addFriend(phoneNumber)

                // Result가 성공인 경우
                if (result.isSuccess) {
                    // UI 상태를 성공으로 업데이트
                    // isSuccess를 true로, 로딩은 false로 변경
                    uiState = uiState.copy(
                        isSuccess = true,
                        isLoading = false
                    )
                } else {
                    // Result가 실패인 경우
                    // 에러 메시지를 상태에 저장하고 로딩은 false로 변경
                    // exceptionOrNull()은 실패 원인을 가져오는 함수
                    uiState = uiState.copy(
                        error = result.exceptionOrNull()?.message ?: "Unknown error occurred",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                // 예상치 못한 에러 발생 시 에러 메시지를 상태에 저장
                uiState = uiState.copy(
                    error = e.message ?: "Unknown error occurred",
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }
}