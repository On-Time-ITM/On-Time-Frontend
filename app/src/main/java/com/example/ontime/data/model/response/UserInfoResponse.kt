package com.example.ontime.data.model.response

import com.example.ontime.data.model.auth.UserStatistics

data class UserInfoResponse(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val statistics: UserStatistics
)
