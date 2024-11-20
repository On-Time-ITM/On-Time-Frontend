package com.example.ontime.data.api

import com.example.ontime.data.model.response.FriendResponse
import retrofit2.Response
import retrofit2.http.GET

interface FriendApi {
    @GET("/friends")
    suspend fun getFriends(): Response<List<FriendResponse>>
}