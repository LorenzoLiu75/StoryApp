package com.mobile.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mobile.storyapp.data.AuthRepository
import com.mobile.storyapp.di.AuthInjection
import com.mobile.storyapp.view.login.LoginViewModel
import com.mobile.storyapp.view.signup.SignupViewModel

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        fun getInstance(context: Context) = AuthViewModelFactory(AuthInjection.provideAuthRepository(context))
    }
}