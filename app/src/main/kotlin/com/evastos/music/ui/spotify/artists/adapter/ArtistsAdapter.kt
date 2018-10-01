package com.evastos.music.ui.spotify.artists.adapter

import android.arch.paging.PagedListAdapter
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.evastos.music.R
import com.evastos.music.data.model.spotify.item.artist.Artist
import com.evastos.music.inject.module.GlideRequests
import com.evastos.music.ui.util.extensions.debounceClicks
import com.evastos.music.ui.util.extensions.inflate
import com.evastos.music.ui.util.extensions.loadImage
import com.evastos.music.ui.util.extensions.setGone
import com.evastos.music.ui.util.extensions.setVisible
import com.evastos.music.ui.util.extensions.showText
import kotlinx.android.synthetic.main.layout_item_artist.view.artistImageView
import kotlinx.android.synthetic.main.layout_item_artist.view.artistNameTextView
import kotlinx.android.synthetic.main.layout_item_artist.view.artistOverlay

class ArtistsAdapter(
    private val glideRequests: GlideRequests,
    private val artistClickListener: (Artist?) -> Unit
) : PagedListAdapter<Artist, RecyclerView.ViewHolder>(ArtistDiffItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ArtistItemViewHolder(parent.inflate(R.layout.layout_item_artist))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ArtistItemViewHolder) {
            holder.bind(getItem(position))
        } else {
            throw RuntimeException("Unknown view holder type")
        }
    }

    private inner class ArtistItemViewHolder(movieItemView: View) :
        RecyclerView.ViewHolder(movieItemView) {

        fun bind(artist: Artist?) {
            with(itemView) {
                artistNameTextView.setVisible()
                artistNameTextView.showText(artist?.name)
                val artistImagePath = artist?.images?.let { images ->
                    if (images.isNotEmpty()) images[0].url else null
                }
                glideRequests.loadImage(artistImagePath, artistImageView) {
                    artistNameTextView.setGone()
                }
                artistOverlay.debounceClicks()
                        .subscribe { _ ->
                            artist?.let {
                                artistClickListener.invoke(it)
                            }
                        }
            }
        }
    }
}
