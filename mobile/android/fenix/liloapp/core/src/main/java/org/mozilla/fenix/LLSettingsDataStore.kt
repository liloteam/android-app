package org.mozilla.fenix

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import mozilla.components.support.ktx.android.content.PreferencesHolder
import mozilla.components.support.ktx.android.content.longPreference

interface LLSettingsDataStore {
    var userKey: String?
}

class LLSettingsSharedPreferences (
    private val context: Context
) : LLSettingsDataStore, PreferencesHolder {

    override val preferences: SharedPreferences
        get() = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)

    var appFirstDate: Long
        get() = preferences.getLong(KEY_APP_FIRST_DATE, -1)
        set(value) = preferences.edit { putLong(KEY_APP_FIRST_DATE, value) }

    //var appFirstDate by longPreference(KEY_APP_FIRST_DATE, -1)

    override var userKey: String?
        get() = preferences.getString(KEY_USER_KEY_STRING, null)
        set(value) = preferences.edit { putString(KEY_USER_KEY_STRING, value) }

    companion object {
        const val FILENAME = "org.lilo.mobile.android2020.settings"

        const val KEY_APP_FIRST_DATE = "APP_FIRST_DATE"
        const val KEY_USER_KEY_STRING = "USER_KEY_STRING"
    }

}


class LLLegacySettingsSharedPreferences (
    private val context: Context
) : LLSettingsDataStore {

    private val preferences: SharedPreferences
        get() = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)

    val hasKeys: Boolean
        get() = preferences.all.isNotEmpty()

    override var userKey: String?
        get() = preferences.getString(KEY_USER_KEY_STRING, null)
        set(value) = preferences.edit { putString(KEY_USER_KEY_STRING, value) }

    companion object {
        const val FILENAME = "com.duckduckgo.app.settings_activity.settings"

        const val KEY_USER_KEY_STRING = "USER_KEY_STRING"
    }

}

