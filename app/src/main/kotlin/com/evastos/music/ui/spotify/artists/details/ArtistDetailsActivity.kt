package com.evastos.music.ui.spotify.artists.details

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import com.evastos.music.R
import com.evastos.music.data.model.spotify.item.artist.Artist
import com.evastos.music.inject.module.GlideApp
import com.evastos.music.ui.base.BaseActivity
import com.evastos.music.ui.util.extensions.loadImage
import com.evastos.music.ui.util.extensions.setGone
import com.evastos.music.ui.util.extensions.setVisible
import com.evastos.music.ui.util.extensions.showText
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.activity_artist_details.artistExternalUrlButton
import kotlinx.android.synthetic.main.activity_artist_details.artistFollowersTextView
import kotlinx.android.synthetic.main.activity_artist_details.artistGenresTextView
import kotlinx.android.synthetic.main.activity_artist_details.artistImageView
import kotlinx.android.synthetic.main.activity_artist_details.artistNameTextView
import kotlinx.android.synthetic.main.activity_artist_details.networkConnectivityBanner

class ArtistDetailsActivity : BaseActivity() {

    companion object {
        private const val EXTRA_ARTIST = "extraArtist"

        fun newIntent(context: Context, artist: Artist) =
                Intent(context, ArtistDetailsActivity::class.java)
                        .apply {
                            putExtra(EXTRA_ARTIST, artist)
                        }
    }

    override val layoutRes: Int = R.layout.activity_artist_details

    private lateinit var viewModel: ArtistDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.apply {
            title = getString(R.string.activity_title_artist_details)
            setDisplayHomeAsUpEnabled(true)
        }

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ArtistDetailsViewModel::class.java)
        val artist = intent.getParcelableExtra<Artist>(EXTRA_ARTIST)

        viewModel.imageLiveData.observe(this, Observer { imagePath ->
            if (!imagePath.isNullOrEmpty()) {
                artistImageView.setVisible()
                GlideApp.with(this).loadImage(imagePath, artistImageView)
            } else {
                artistImageView.setGone()
            }
        })

        viewModel.nameLiveData.observe(this, Observer { name ->
            if (!name.isNullOrEmpty()) {
                artistNameTextView.setVisible()
                artistNameTextView.showText(name)
            } else {
                artistNameTextView.setGone()
            }
        })

        viewModel.genresLiveData.observe(this, Observer { genres ->
            if (!genres.isNullOrEmpty()) {
                artistGenresTextView.setVisible()
                artistGenresTextView.showText(
                    getString(R.string.artist_details_genres, genres)
                )
            } else {
                artistGenresTextView.setGone()
            }
        })

        viewModel.followersLiveData.observe(this, Observer { followers ->
            if (followers != null) {
                artistFollowersTextView.setVisible()
                artistFollowersTextView.showText(
                    getString(R.string.artist_details_followers, followers)
                )
            } else {
                artistFollowersTextView.setGone()
            }
        })

        viewModel.externalUrlLiveData.observe(this, Observer { externalUrl ->
            if (!externalUrl.isNullOrEmpty()) {
                artistExternalUrlButton.setVisible()
                artistExternalUrlButton.clicks().subscribe {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(externalUrl)))
                }
            } else {
                artistExternalUrlButton.setGone()
            }
        })

        viewModel.networkConnectivityBannerLiveData.observe(this, Observer { isVisible ->
            if (isVisible == true) {
                networkConnectivityBanner.setVisible()
            } else networkConnectivityBanner.setGone()
        })

        viewModel.onCreate(artist, networkConnectivityReceiver.observable)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
