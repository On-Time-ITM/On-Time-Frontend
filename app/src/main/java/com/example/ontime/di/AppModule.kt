package com.example.ontime.di

import android.content.Context
import com.example.ontime.data.api.ApiClient
import com.example.ontime.data.api.AuthApi
import com.example.ontime.data.api.FriendApi
import com.example.ontime.data.auth.AuthManager
import com.example.ontime.ui.friend.usecase.AddFriendUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiClient(): ApiClient {
        return ApiClient()
    }


    @Provides
    @Singleton
    fun provideFriendApi(apiClient: ApiClient): FriendApi {
        return apiClient.friendApi
    }

    @Provides
    @Singleton
    fun provideAuthApi(apiClient: ApiClient): AuthApi {
        return apiClient.authApi
    }

    @Provides
    @Singleton
    fun provideAuthManager(@ApplicationContext context: Context): AuthManager {
        return AuthManager(context)
    }

    @Provides
    @Singleton
    fun provideAddFriendUseCase(
        friendApi: FriendApi,
        authManager: AuthManager
    ): AddFriendUseCase {
        return AddFriendUseCase(friendApi, authManager)
    }
}