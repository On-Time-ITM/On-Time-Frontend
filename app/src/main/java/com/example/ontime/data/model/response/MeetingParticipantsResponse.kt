package com.example.ontime.data.model.response

import com.example.ontime.data.model.request.LocationInfo


data class MeetingParticipantsResponse(
    val meetingId: String,
    val participantLocationInfos: List<ParticipantLocationInfo>
)

data class ParticipantLocationInfo(
    val participantId: String,
    val participantLocation: LocationInfo
)
