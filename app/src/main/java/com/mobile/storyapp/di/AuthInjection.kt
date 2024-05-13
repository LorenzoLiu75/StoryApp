package com.mobile.storyapp.di

import android.content.Context
import com.mobile.storyapp.data.AuthRepository
import com.mobile.storyapp.data.api.AuthApiConfig
import com.mobile.storyapp.data.pref.UserPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object AuthInjection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val userPreference = UserPreference.getInstance(context)
        val user = runBlocking { userPreference.getSession().first() }
        val authApiService = AuthApiConfig.getApiService(user.token)
        return AuthRepository(authApiService, userPreference)
    }
}