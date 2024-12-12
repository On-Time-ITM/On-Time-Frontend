package com.example.ontime.ui.team

import com.example.ontime.data.model.account.AccountData
import com.example.ontime.data.model.response.FriendResponse

data class TeamFormationData(
    val title: String = "",
    val meetingDateTime: String = "", // ISO 8601 형식의 날짜/시간
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val membersList: List<FriendResponse> = emptyList(),
    val bankAccount: AccountData? = null,
    val lateFee: Int = 0,
    val date: String = "",
    val time: String = "",
    val logoUrl: String? = null,
)


