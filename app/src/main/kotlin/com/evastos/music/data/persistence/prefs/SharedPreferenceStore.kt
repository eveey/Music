package com.evastos.music.data.persistence.prefs

import android.content.Context
import android.content.SharedPreferences
import com.evastos.music.data.model.spotify.user.User
import com.evastos.music.data.model.auth.AuthData
import com.evastos.music.inject.qualifier.AppContext
import com.squareup.moshi.Moshi

class SharedPreferenceStore(
    @AppContext context: Context
) : PreferenceStore {

    companion object {
        private const val SHARED_PREFERENCES_NAME = "music_rebels_preferences"
    }

    private val sharedPreferences =
            context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val moshi = Moshi.Builder().build()

    override var authData: AuthData?
        get() = sharedPreferences[PreferenceStore.Constants.AUTH_DATA]
        set(value) {
            sharedPreferences[PreferenceStore.Constants.AUTH_DATA] = value
        }

    override var user: User?
        get() = sharedPreferences[PreferenceStore.Constants.USER]
        set(value) {
            sharedPreferences[PreferenceStore.Constants.USER] = value
        }

    override var artistQuery: String?
        get() = sharedPreferences[PreferenceStore.Constants.ARTIST_QUERY]
        set(value) {
            sharedPreferences[PreferenceStore.Constants.ARTIST_QUERY] = value
        }

    operator fun SharedPreferences.set(key: String, value: Any?) {
        when (value) {
            is String? -> edit { it.putString(key, value) }
            is Int -> edit { it.putInt(key, value) }
            is Boolean -> edit { it.putBoolean(key, value) }
            is Float -> edit { it.putFloat(key, value) }
            is Long -> edit { it.putLong(key, value) }
            is AuthData -> {
                val authDataJson = moshi.adapter(AuthData::class.java).toJson(value)
                edit {
                    it.putString(key, authDataJson)
                }
            }
            is User -> {
                val userJson = moshi.adapter(User::class.java).toJson(value)
                edit {
                    it.putString(key, userJson)
                }
            }
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

    @Suppress("UnsafeCast")
    inline operator fun <reified T : Any> SharedPreferences.get(
        key: String,
        defaultValue: T? = null
    ): T? {
        return when (T::class) {
            String::class -> getString(key, defaultValue as? String) as T?
            Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
            Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
            Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
            Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
            AuthData::class -> getFromJson(key, defaultValue)
            User::class -> getFromJson(key, defaultValue)
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    inline fun <reified T : Any> SharedPreferences.getFromJson(
        key: String,
        defaultValue: T? = null
    ): T? {
        getString(key, defaultValue as? String)?.let {
            return Moshi.Builder().build().adapter(T::class.java).fromJson(it)
        }
        return null
    }
}
