package com.mobile.storyapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.storyapp.data.UserRepository
import com.mobile.storyapp.data.api.StoryResponse
import kotlinx.coroutines.launch

class MapsViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _stories = MutableLiveData<StoryResponse>()
    val stories: LiveData<StoryResponse> = _stories

    fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            val response = userRepository.getStoriesWithLocation()
            _stories.postValue(response)
        }
    }
}