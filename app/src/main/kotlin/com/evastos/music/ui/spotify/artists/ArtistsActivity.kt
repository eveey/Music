package com.evastos.music.ui.spotify.artists

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.evastos.music.R
import com.evastos.music.ui.base.BaseActivity

class ArtistsActivity : BaseActivity() {

    companion object {
        private const val ARTISTS_COLUMN_COUNT = 2

        fun newIntent(context: Context) = Intent(context, ArtistsActivity::class.java)
    }

    override val layoutRes: Int = R.layout.activity_artists

    private lateinit var viewModel: ArtistsViewModel

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.apply {
            title = getString(R.string.activity_title_artists)
        }

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ArtistsViewModel::class.java)
        viewModel.onCreate(networkConnectivityReceiver.observable)
    }
}
