package com.example.ontime.ui.team

// TeamFormationData.kt
data class TeamFormationData(
    val title: String = "",
    val members: List<String> = emptyList(),
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val bankAccount: String = "",
    val date: String = "",
    val time: String = "",
)


