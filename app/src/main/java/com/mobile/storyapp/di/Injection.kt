package com.mobile.storyapp.di

import android.content.Context
import com.mobile.storyapp.data.UserRepository
import com.mobile.storyapp.data.api.ApiConfig
import com.mobile.storyapp.data.database.StoryDatabase
import com.mobile.storyapp.data.pref.UserPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context)

        val user = runBlocking { userPreference.getSession().first() }

        val apiService = ApiConfig.getApiService(user.token)
        val storyDatabase = StoryDatabase.getDatabase(context)

        return UserRepository(apiService, storyDatabase, userPreference)
    }
}