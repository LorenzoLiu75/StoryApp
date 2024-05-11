package com.mobile.storyapp.view.detail

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import coil.load
import com.mobile.storyapp.R
import com.mobile.storyapp.data.api.Story
import com.mobile.storyapp.databinding.ActivityDetailBinding
import com.mobile.storyapp.view.ViewModelFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra("storyId")

        storyId?.let {
            viewModel.getStoryDetail(it)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.story.observe(this, Observer { storyResponse ->
            if (!storyResponse.error!!) {
                storyResponse.story?.let { displayStoryDetails(it) }
            } else {
                binding.storyTitleDetail.text = getString(R.string.error_loading_story)
                binding.storyDescriptionDetail.text = storyResponse.message ?: "Unknown error"
            }
        })

        viewModel.error.observe(this, Observer { error ->
            binding.storyTitleDetail.text = "Error"
            binding.storyDescriptionDetail.text = error
        })
    }

    private fun displayStoryDetails(story: Story) {
        binding.storyTitleDetail.text = story.name
        binding.storyDescriptionDetail.text = story.description
        binding.storyImageDetail.load(story.photoUrl)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}