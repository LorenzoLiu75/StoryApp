package com.mobile.storyapp.di

import android.content.Context
import com.mobile.storyapp.data.UserRepository
import com.mobile.storyapp.data.api.ApiConfig
import com.mobile.storyapp.data.pref.UserPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.internal.userAgent

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context)
        val user = runBlocking { userPreference.getSession().first() }

        val authApiService = ApiConfig.getAuthApiService()
        val storyApiService = ApiConfig.getApiService(user.token)

        return UserRepository(authApiService, storyApiService, userPreference)
    }
}
