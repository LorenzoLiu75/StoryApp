package com.mobile.storyapp.view.detail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import coil.load
import com.mobile.storyapp.R
import com.mobile.storyapp.data.response.Story
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

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.detail_story)

        val storyId = intent.getStringExtra("storyId")
        val storyPhotoUrl = intent.getStringExtra("storyPhotoUrl")

        storyPhotoUrl?.let {
            binding.ivDetailPhoto.load(it)
        }

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
                binding.tvDetailName.text = getString(R.string.error_loading_story)
                binding.tvDetailDescription.text = storyResponse.message ?: getString(R.string.unknown_error)
            }
        })

        viewModel.error.observe(this, Observer { error ->
            binding.tvDetailName.text = getString(R.string.error)
            binding.tvDetailDescription.text = error
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displayStoryDetails(story: Story) {
        binding.tvDetailName.text = story.name
        binding.tvDetailDescription.text = story.description
        binding.ivDetailPhoto.load(story.photoUrl)

        binding.ivDetailPhoto.transitionName = "photo"
        binding.tvDetailName.transitionName = "name"
        binding.tvDetailDescription.transitionName = "description"
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}