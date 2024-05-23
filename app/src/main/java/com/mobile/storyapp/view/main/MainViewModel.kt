package com.mobile.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mobile.storyapp.data.UserRepository
import com.mobile.storyapp.data.response.ListStoryItem
import com.mobile.storyapp.data.pref.UserModel
import com.mobile.storyapp.utils.EspressoIdlingResource
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val story: LiveData<PagingData<ListStoryItem>> =
        repository.getStoriesAll().cachedIn(viewModelScope)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        EspressoIdlingResource.increment()
        viewModelScope.launch {
            repository.logout()
            EspressoIdlingResource.decrement()
        }
    }
}