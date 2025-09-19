package org.mozilla.fenix.lilomodule.settings

import android.content.Context
import mozilla.components.browser.state.action.TranslationsAction
import mozilla.components.support.base.log.logger.Logger
import org.mozilla.fenix.LLLegacySettingsSharedPreferences
import org.mozilla.fenix.LLSettingsDataStore
import org.mozilla.fenix.LLSettingsSharedPreferences
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.lilomodule.LLModule

data class LLSettings(
    val context: Context
) {
    private val logger = Logger("LLSettings")

    fun updateOfferTranslationOption(enabled: Boolean) {
        val browserStore = context.components.core.store
        browserStore.dispatch(
            TranslationsAction.UpdateGlobalOfferTranslateSettingAction(
                offerTranslation = enabled,
            ),
        )
        context.settings().offerTranslation = enabled
    }

    /**
     * If it's the first run of the app then save the current date and check for a user key
     * from a previous app version.
     * If it's not the first run or there is no user key then return null.
     * if it's the first run and there is a user key then save it and return it such as
     * it can be used to a migration purpose.
     */
    fun checkForUserKeyIfFirstRun(): String? {
        var userKey: String? = null
        val liloSettings = LLSettingsSharedPreferences(context)
        if (liloSettings.appFirstDate < 0) {
            liloSettings.appFirstDate = System.currentTimeMillis()
            val legacySettings = LLLegacySettingsSharedPreferences(context)
            legacySettings.userKey?.let {
                liloSettings.userKey = it
                logger.debug("LILO:DBG:Userkey: User key: $it")
                userKey = it
            }?:run {
                logger.debug("LILO:DBG:Userkey: No user key")
            }
        }
        else {
            logger.debug("LILO:DBG:Userkey: first date: ${liloSettings.appFirstDate}")
        }
        return userKey
    }
}
