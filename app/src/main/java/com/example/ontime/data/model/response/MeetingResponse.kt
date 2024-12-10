package com.example.ontime.data.model.response

import com.example.ontime.data.model.request.AccountInfoRequest

data class MeetingResponse(
    val id: String,
    val name: String,
    val meetingDateTime: String,
    val location: Location,
    val lateFee: Int,
    val accountInfo: AccountInfoRequest,
    val profileImage: String,
    val participantCount: Int
)

data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String
)

