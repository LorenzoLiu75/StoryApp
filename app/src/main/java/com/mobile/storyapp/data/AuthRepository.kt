package com.mobile.storyapp.data

import com.mobile.storyapp.data.api.AuthApiService
import com.mobile.storyapp.data.response.LoginResponse
import com.mobile.storyapp.data.response.RegisterResponse
import com.mobile.storyapp.data.pref.UserModel
import com.mobile.storyapp.data.pref.UserPreference

class AuthRepository(private val authApiService: AuthApiService, private val userPreference: UserPreference) {

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

    companion object {
        fun getInstance(authApiService: AuthApiService, userPreference: UserPreference) = AuthRepository(authApiService, userPreference)
    }
}