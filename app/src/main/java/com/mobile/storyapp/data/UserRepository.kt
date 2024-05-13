package com.mobile.storyapp.data

import com.mobile.storyapp.data.api.ApiService
import com.mobile.storyapp.data.api.AuthApiService
import com.mobile.storyapp.data.api.DetailStoryResponse
import com.mobile.storyapp.data.api.LoginResponse
import com.mobile.storyapp.data.api.RegisterResponse
import com.mobile.storyapp.data.api.StoryResponse
import com.mobile.storyapp.data.pref.UserModel
import com.mobile.storyapp.data.pref.UserPreference

class UserRepository(
    private val authApiService: AuthApiService,
    private val storyApiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return authApiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        val response = authApiService.login(email, password)
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
        return storyApiService.getStories()
    }

    suspend fun getStoryDetail(storyId: String): DetailStoryResponse {
        return storyApiService.getStoryDetail(storyId)
    }

    fun getSession() = userPreference.getSession()

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(authApiService: AuthApiService, storyApiService: ApiService, userPreference: UserPreference): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(authApiService, storyApiService, userPreference)
            }.also { instance = it }
    }
}
