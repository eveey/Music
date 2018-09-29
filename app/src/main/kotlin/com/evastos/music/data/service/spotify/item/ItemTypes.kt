package com.evastos.music.data.service.spotify.item

class ItemTypes {
    private val itemTypes = ArrayList<ItemType>()

    fun add(itemType: ItemType) {
        itemTypes.add(itemType)
    }

    override fun toString(): String {
        return itemTypes.joinToString()
    }
}
