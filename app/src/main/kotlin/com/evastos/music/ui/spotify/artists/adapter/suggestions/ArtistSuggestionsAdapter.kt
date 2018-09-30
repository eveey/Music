package com.evastos.music.ui.spotify.artists.adapter.suggestions

import android.app.SearchManager
import android.content.Context
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.support.v4.widget.SimpleCursorAdapter
import com.evastos.music.R
import com.evastos.music.data.model.spotify.item.artist.Artist

/**
 * Shows movie suggestions while typing the query in the search view.
 */
class ArtistSuggestionsAdapter(
    context: Context?
) : SimpleCursorAdapter(
    context,
    R.layout.layout_item_artist_suggestion,
    null,
    Array(1) { SearchManager.SUGGEST_COLUMN_TEXT_1 },
    IntArray(1) { R.id.artistSuggestionTextView },
    0
) {
    private var suggestions: List<Artist>? = null

    /**
     * Sets the artist suggestions.
     * Ugly solution but SearchView only takes a CursorAdapter.
     */
    fun setSuggestions(suggestions: List<Artist>?) {
        this.suggestions = suggestions
        val columns = arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1)
        val cursor = MatrixCursor(columns)
        suggestions?.let {
            for (i in 0 until it.size) {
                val row = arrayOf(i.toString(), it[i].name)
                cursor.addRow(row)
            }
        }
        swapCursor(cursor)
    }

    /**
     * Returns the artist name for the position, or null if there is no item at this position.
     */
    fun getArtistName(position: Int): String? {
        suggestions?.let {
            if (position < it.size) {
                return it[position].name
            }
        }
        return null
    }
}
