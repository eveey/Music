package com.evastos.music.ui.spotify.artists.adapter

import android.support.v7.util.DiffUtil
import com.evastos.music.data.model.spotify.item.artist.Artist

object ArtistDiffItemCallback : DiffUtil.ItemCallback<Artist>() {
    override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
        return oldItem.id === newItem.id
    }

    override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
        return oldItem == newItem
    }
}
