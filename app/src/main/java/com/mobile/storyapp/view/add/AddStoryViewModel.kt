package com.mobile.storyapp.view.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mobile.storyapp.data.UserRepository
import com.mobile.storyapp.data.api.ApiConfig
import com.mobile.storyapp.data.response.FileUploadResponse
import com.mobile.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(repository: UserRepository): ViewModel() {

    private val _response = MutableLiveData<FileUploadResponse>()
    val response: LiveData<FileUploadResponse> = _response

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val story: LiveData<PagingData<ListStoryItem>> =
        repository.getStoriesAll().cachedIn(viewModelScope)

    suspend fun uploadStoryImage(token: String, multipartBody: MultipartBody.Part, description: RequestBody, lat: Double?, lon: Double?): FileUploadResponse {
        return withContext(Dispatchers.IO) {
            try {
                val apiService = ApiConfig.getApiService(token)
                val response = apiService.uploadImage(multipartBody, description, lat, lon)
                _response.postValue(response)
                response
            } catch (e: Exception) {
                throw e
            }
        }
    }
}