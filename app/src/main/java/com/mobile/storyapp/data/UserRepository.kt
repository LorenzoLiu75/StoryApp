package com.mobile.storyapp.data

import com.mobile.storyapp.data.api.ApiService
import com.mobile.storyapp.data.api.DetailStoryResponse
import com.mobile.storyapp.data.api.LoginResponse
import com.mobile.storyapp.data.api.RegisterResponse
import com.mobile.storyapp.data.api.StoryResponse
import com.mobile.storyapp.data.pref.UserModel
import com.mobile.storyapp.data.pref.UserPreference
import kotlinx.coroutines.flow.first

class UserRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        val response = apiService.login(email, password)
        if (!response.error!!) {
            response.loginResult?.token?.let { token ->
                userPreference.saveSession(UserModel(email, token, true))
            }
        }
        return response
    }

    suspend fun saveSession(userModel: UserModel) {
        userPreference.saveSession(userModel)
    }

    suspend fun getStories(): StoryResponse {
        return apiService.getStories()
    }
    suspend fun getStoryDetail(storyId: String): DetailStoryResponse {
        return apiService.getStoryDetail(storyId)
    }

    fun getSession() = userPreference.getSession()

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
    }
}
