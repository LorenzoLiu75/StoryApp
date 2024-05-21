package com.mobile.storyapp.data.adapter

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mobile.storyapp.data.api.ListStoryItem
import com.mobile.storyapp.databinding.ItemStoryBinding
import com.mobile.storyapp.view.detail.DetailActivity

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(StoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {

        val story = getItem(position)
        holder.bind(story!!)
        holder.itemView.setOnClickListener {
            val intentDetail = Intent(holder.itemView.context, DetailActivity::class.java)
            intentDetail.putExtra("storyId", story.id)

            val photoPair = android.util.Pair.create(holder.binding.ivItemPhoto as View, "photo")
            val namePair = android.util.Pair.create(holder.binding.tvItemName as View, "name")
            val descriptionPair = android.util.Pair.create(holder.binding.storyDescription as View, "description")

            val options = ActivityOptions.makeSceneTransitionAnimation(
                holder.itemView.context as Activity,
                photoPair,
                namePair,
                descriptionPair
            ).toBundle()

            holder.itemView.context.startActivity(intentDetail, options)
        }
    }

    class StoryViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.tvItemName.text = story.name
            binding.storyDescription.text = story.description
            binding.ivItemPhoto.load(story.photoUrl)
        }
    }

    class StoryDiffCallback : DiffUtil.ItemCallback<ListStoryItem>() {
        override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem == newItem
        }
    }
}