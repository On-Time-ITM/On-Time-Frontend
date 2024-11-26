// FriendUiState.kt 파일을 생성하거나
// 기존 파일의 최상위 레벨에 추가
data class FriendUiState(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val tardinessRate: Float,
    val profileImage: String,
    val isSelected: Boolean = false
)