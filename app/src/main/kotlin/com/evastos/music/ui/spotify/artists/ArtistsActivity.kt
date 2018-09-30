package com.evastos.music.ui.spotify.artists

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import com.evastos.music.R
import com.evastos.music.data.model.spotify.item.artist.Artist
import com.evastos.music.domain.livedata.LoadingState
import com.evastos.music.inject.module.GlideApp
import com.evastos.music.ui.base.BaseActivity
import com.evastos.music.ui.spotify.artists.adapter.ArtistsAdapter
import com.evastos.music.ui.spotify.artists.adapter.suggestions.ArtistSuggestionsAdapter
import com.evastos.music.ui.util.extensions.setGone
import com.evastos.music.ui.util.extensions.setNotRefreshing
import com.evastos.music.ui.util.extensions.setRefreshing
import com.evastos.music.ui.util.extensions.setVisible
import kotlinx.android.synthetic.main.activity_artists.artistsLoadingView
import kotlinx.android.synthetic.main.activity_artists.artistsRecyclerView
import kotlinx.android.synthetic.main.activity_artists.artistsRootView
import kotlinx.android.synthetic.main.activity_artists.artistsSwipeRefresh
import kotlinx.android.synthetic.main.activity_artists.networkConnectivityBanner

class ArtistsActivity : BaseActivity() {

    companion object {
        private const val ARTISTS_COLUMN_COUNT = 2

        fun newIntent(context: Context) = Intent(context, ArtistsActivity::class.java)
    }

    override val layoutRes: Int = R.layout.activity_artists

    private lateinit var viewModel: ArtistsViewModel

    private lateinit var artistSuggestionsAdapter: ArtistSuggestionsAdapter

    private lateinit var artistsAdapter: ArtistsAdapter

    private lateinit var searchView: SearchView

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.apply {
            title = getString(R.string.activity_title_artists)
            setDisplayHomeAsUpEnabled(false)
        }

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ArtistsViewModel::class.java)

        artistsRecyclerView.apply {
            artistsAdapter = getArtistsAdapter()
            layoutManager = GridLayoutManager(context, ARTISTS_COLUMN_COUNT)
            adapter = artistsAdapter
        }

        artistSuggestionsAdapter = ArtistSuggestionsAdapter(this)

        artistsSwipeRefresh.setOnRefreshListener {
            artistsSwipeRefresh.setRefreshing()
            viewModel.onRefresh()
        }

        viewModel.artistsLiveData.observe(this, Observer<PagedList<Artist>> { pagedArtists ->
            artistsAdapter.submitList(pagedArtists)
        })

        viewModel.loadingStateLiveData.observe(this, Observer { loadingState ->
            loadingState?.let { it ->
                when (it) {
                    is LoadingState.Loading -> {
                        artistsLoadingView.setVisible()
                        hideSnackbar()
                    }
                    is LoadingState.Success -> {
                        artistsLoadingView.setGone()
                        artistsSwipeRefresh.setNotRefreshing()
                        hideSnackbar()
                    }
                    is LoadingState.Error -> {
                        artistsLoadingView.setGone()
                        artistsSwipeRefresh.setNotRefreshing()
                        it.errorMessage?.let { error ->
                            showSnackbar(artistsRootView, error, getString(R.string.action_retry)) {
                                viewModel.onRetry()
                            }
                        }
                    }
                }
            }
        })

        viewModel.artistSuggestionsLiveData.observe(this, Observer { suggestions ->
            artistSuggestionsAdapter.setSuggestions(suggestions)
        })

        viewModel.artistDetailsLiveData.observe(this, Observer { artist ->
            artist?.let {
                //                startActivity(ArtistDetailsActivity.newIntent(this, it))
            }
        })

        viewModel.artistSearchLiveData.observe(this, Observer { artistName ->
            supportActionBar?.apply {
                this.subtitle = artistName
            }
        })

        viewModel.networkConnectivityBannerLiveData.observe(this, Observer { isVisible ->
            if (isVisible == true) {
                networkConnectivityBanner.setVisible()
            } else networkConnectivityBanner.setGone()
        })

        viewModel.onCreate(networkConnectivityReceiver.observable)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_artists, menu)
        val searchItem = menu.findItem(R.id.searchArtistsAction)

        searchView = searchItem.actionView as SearchView
        searchView.apply {
            suggestionsAdapter = artistSuggestionsAdapter

            setOnSuggestionListener(object : SearchView.OnSuggestionListener {
                override fun onSuggestionSelect(position: Int): Boolean {
                    return false
                }

                override fun onSuggestionClick(position: Int): Boolean {
                    artistSuggestionsAdapter.getArtistName(position)?.let { movieTitle ->
                        setQuery(movieTitle, false)
                        clearFocus()
                        viewModel.onSearchQuerySubmit(movieTitle)
                        return true
                    }
                    return false
                }
            })

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        clearFocus()
                        if (!it.isEmpty()) {
                            viewModel.onSearchQuerySubmit(it)
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    query?.let {
                        if (!it.isEmpty()) {
                            viewModel.onSearchQueryChange(it)
                        }
                    }
                    return true
                }
            })
        }
        return true
    }

    private fun getArtistsAdapter() =
            ArtistsAdapter(GlideApp.with(this)) { artist: Artist? ->
                viewModel.onArtistClick(artist)
            }
}
