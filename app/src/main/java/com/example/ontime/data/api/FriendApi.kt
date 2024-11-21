package com.example.ontime.data.api

import com.example.ontime.data.model.request.AddFriendRequest
import com.example.ontime.data.model.response.FriendResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FriendApi {
    @GET("/friends")
    suspend fun getFriends(): Response<List<FriendResponse>>

    @POST("/api/v1/friendship")
    suspend fun addFriend(@Body request: AddFriendRequest): Response<Unit>

}