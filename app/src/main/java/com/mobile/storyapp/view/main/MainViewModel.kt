package com.mobile.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mobile.storyapp.data.UserRepository
import com.mobile.storyapp.data.api.StoryResponse
import com.mobile.storyapp.data.pref.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _stories = MutableLiveData<StoryResponse>()
    val stories: LiveData<StoryResponse>
        get() = _stories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStories() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val storyResponse = repository.getStories()
                _stories.value = storyResponse
            } catch (e: Exception) {
                _stories.value = StoryResponse(error = true, message = e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }

}