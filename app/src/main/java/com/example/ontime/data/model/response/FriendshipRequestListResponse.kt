package com.example.ontime.data.model.response

import com.example.ontime.ui.friend.requestAccpet.Requester

data class FriendshipRequestListResponse(
    val friendshipId: String,
    val requester: Requester,
    val createdAt: String,
)
