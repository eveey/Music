package com.evastos.music.data.persistence.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.evastos.music.data.model.spotify.item.artist.Artist
import com.evastos.music.data.persistence.db.artist.ArtistDao
import com.evastos.music.data.persistence.db.converter.MusicDbTypeConverters

/**
 * The Room database that contains the Artist table.
 */
@Database(entities = [Artist::class], version = 1, exportSchema = false)
@TypeConverters(MusicDbTypeConverters::class)
abstract class MusicDatabase : RoomDatabase() {

    abstract fun artistDao(): ArtistDao

    companion object {

        @Volatile
        private var databaseInstance: MusicDatabase? = null

        fun getInstance(context: Context): MusicDatabase =
                databaseInstance ?: synchronized(this) {
                    databaseInstance ?: buildDatabase(context).also { databaseInstance = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                    MusicDatabase::class.java, "MusicRebels.db")
                        .build()
    }
}
