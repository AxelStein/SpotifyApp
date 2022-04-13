package com.example.spotifyapp.ui.track_list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spotifyapp.R
import com.example.spotifyapp.data.entities.Track
import com.example.spotifyapp.databinding.TrackItemBinding
import com.example.spotifyapp.utils.CompareBuilder
import com.example.spotifyapp.utils.OnItemClickListener
import com.example.spotifyapp.utils.inflate

class TrackListAdapter : ListAdapter<Track, TrackListAdapter.ViewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.run {
                CompareBuilder().append(id, newItem.id)
                    .append(title, newItem.title)
                    .append(artists, newItem.artists)
                    .append(externalUrl, newItem.externalUrl)
                    .append(threadNumber, newItem.threadNumber)
                    .areEqual()
            }
        }
    }

    private var onItemCLickListener: OnItemClickListener<Track>? = null

    fun setOnItemClickListener(callback: (pos: Int, item: Track) -> Unit) {
        onItemCLickListener = object : OnItemClickListener<Track> {
            override fun onItemClick(pos: Int, item: Track) {
                callback(pos, item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.track_item)).also { vh ->
            vh.setOnClickListener { pos ->
                onItemCLickListener?.onItemClick(pos, getItem(pos))
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = TrackItemBinding.bind(itemView)

        fun setOnClickListener(callback: (pos: Int) -> Unit) {
            binding.container.setOnClickListener {
                if (adapterPosition != -1) {
                    callback(adapterPosition)
                }
            }
        }

        fun setItem(track: Track) {
            binding.title.text = track.title
            binding.artists.text = track.artists
            binding.threadNumber.text = track.threadNumber.toString()
        }
    }
}