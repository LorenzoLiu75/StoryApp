package com.mobile.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.mobile.storyapp.data.api.ApiService
import com.mobile.storyapp.data.database.StoryDatabase
import com.mobile.storyapp.data.mediator.StoryRemoteMediator
import com.mobile.storyapp.data.response.DetailStoryResponse
import com.mobile.storyapp.data.response.ListStoryItem
import com.mobile.storyapp.data.response.StoryResponse
import com.mobile.storyapp.data.pref.UserPreference
import com.mobile.storyapp.data.response.FileUploadResponse
import com.mobile.storyapp.utils.wrapEspressoIdlingResource
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase,
    private val userPreference: UserPreference
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getStoriesAll(): LiveData<PagingData<ListStoryItem>> {
        wrapEspressoIdlingResource {
            return Pager(
                config = PagingConfig(
                    pageSize = 5
                ),
                remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
                pagingSourceFactory = {
                    storyDatabase.storyDao().getAllStories()
                }
            ).liveData
        }
    }

    suspend fun getStoriesWithLocation(): StoryResponse {
        return apiService.getStoriesWithLocation(location = 1)
    }

    suspend fun getStoryDetail(storyId: String): DetailStoryResponse {
        return apiService.getStoryDetail(storyId)
    }

    fun getSession() = userPreference.getSession()

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        fun getInstance(apiService: ApiService, storyDatabase: StoryDatabase, userPreference: UserPreference) = UserRepository(apiService, storyDatabase, userPreference)
    }
}
