package com.mobile.storyapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.storyapp.data.UserRepository
import com.mobile.storyapp.data.api.DetailStoryResponse
import com.mobile.storyapp.data.api.StoryResponse
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: UserRepository) : ViewModel() {

    private val _story = MutableLiveData<DetailStoryResponse>()
    val story: LiveData<DetailStoryResponse> = _story

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getStoryDetail(storyId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getStoryDetail(storyId)
                _story.value = response
            } catch (e: Exception) {
                _error.value = DetailStoryResponse(error = true, message = e.message).toString()
            } finally {
                _isLoading.value = false
            }
        }
    }
}