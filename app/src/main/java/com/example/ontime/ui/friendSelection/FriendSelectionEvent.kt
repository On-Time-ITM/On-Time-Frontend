package com.example.ontime.ui.friendSelection

import com.example.ontime.data.model.response.FriendResponse

sealed class FriendSelectionEvent {
    data class FriendsConfirmed(
        val selectedFriends: List<String>, // friend IDs
        val selectedFriendsList: List<FriendResponse> // friend Data
    ) : FriendSelectionEvent()
}
