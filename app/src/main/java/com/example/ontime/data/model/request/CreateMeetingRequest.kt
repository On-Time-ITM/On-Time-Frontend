package com.example.ontime.data.model.request

data class CreateMeetingRequest(
    val name: String,
    val meetingDateTime: String, // ISO 8601 형식의 문자열
    val location: Location?,
    val lateFee: Int,
    val accountInfo: AccountInfo,
    val hostId: String, // UUID를 String으로 수정
    val participantIds: List<String>, // UUID를 String으로 수정
    val profileImage: String
)

data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String
)
