package com.example.ontime.ui.team

import com.example.ontime.data.model.account.AccountData

data class TeamFormationData(
    val title: String = "",
    val members: List<String> = emptyList(),
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val bankAccount: AccountData? = null,  // AccountData를 포함
    val date: String = "",
    val time: String = "",
)


