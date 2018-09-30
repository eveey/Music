package com.evastos.music.data.persistence.db.artist

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.evastos.music.data.model.spotify.item.artist.Artist
import io.reactivex.Single

/**
 * Data Access Object for the artist table.
 */
@Dao
interface ArtistDao {

    /**
     * Get all artists
     * @return the artist from the table.
     */
    @Query("SELECT * FROM Artist")
    fun getArtists(): Single<List<Artist>>

    /**
     * Insert artists to the database. If an artist already exists, replace it.
     * @param artist the artist to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArtists(artists: List<Artist>)

    /**
     * Delete all artists.
     */
    @Query("DELETE FROM Artist")
    fun deleteAllArtists()
}
