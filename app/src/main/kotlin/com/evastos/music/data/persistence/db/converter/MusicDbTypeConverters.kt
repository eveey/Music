package com.evastos.music.data.persistence.db.converter

import android.arch.persistence.room.TypeConverter
import com.evastos.music.data.model.spotify.item.ItemType
import com.evastos.music.data.model.spotify.item.artist.Followers
import com.evastos.music.data.model.spotify.item.artist.Image
import com.evastos.music.data.model.spotify.search.ExternalUrls

class MusicDbTypeConverters {

    companion object {
        private const val STRING_LIST_DELIMITER = ","
    }

    @TypeConverter
    fun fromFollowers(followers: Followers?): Int? =
            followers?.let {
                return@let it.total
            }

    @TypeConverter
    fun toFollowers(followersCount: Int?): Followers? =
            followersCount?.let {
                return@let Followers(it)
            }

    @TypeConverter
    fun fromExternalUrls(externalUrls: ExternalUrls?): String? =
            externalUrls?.let {
                return@let it.spotify
            }

    @TypeConverter
    fun toExternalUrls(spotifyUrl: String?): ExternalUrls? =
            spotifyUrl?.let {
                return@let ExternalUrls(it)
            }

    @TypeConverter
    fun fromImages(images: List<Image>?): String? =
            images?.let {
                if (it.isNotEmpty()) return@let it[0].url
                else null
            }

    @TypeConverter
    fun toImages(imageUrl: String?): List<Image>? =
            imageUrl?.let {
                return@let listOf(Image(url = it, width = null, height = null))
            }

    @TypeConverter
    fun fromStringList(stringList: List<String>?): String? =
            stringList?.joinToString(separator = STRING_LIST_DELIMITER)

    @TypeConverter
    fun toStringList(stringsJoined: String?): List<String>? =
            stringsJoined?.split(STRING_LIST_DELIMITER)

    @TypeConverter
    fun fromItemType(itemType: ItemType?): Int? =
            itemType?.ordinal

    @TypeConverter
    fun toItemType(itemTypeOrdinal: Int?): ItemType? =
            itemTypeOrdinal?.let { ordinal ->
                return@let ItemType.values().find { itemType ->
                    itemType.ordinal == ordinal
                }
            }
}
