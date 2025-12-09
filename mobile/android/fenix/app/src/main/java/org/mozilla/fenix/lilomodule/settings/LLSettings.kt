package org.mozilla.fenix.lilomodule.settings

import android.content.Context
import android.os.StrictMode
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
    private val strictMode = context.components.strictMode

    private val legacySettings by lazy {
        LLLegacySettingsSharedPreferences(context)
    }

    private val liloSettings by lazy {
        LLSettingsSharedPreferences(context)
    }

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
    fun checkForLegacyUserKey(): String? {
        return legacySettings.userKey?.let { userKey ->
            liloSettings.userKey = userKey
            logger.debug("LILO:DBG:Userkey: User key: $userKey")
            userKey
        }
    }

    val isFirstRun: Boolean
        get() {
            val currentFirstDate = strictMode.resetAfter(StrictMode.allowThreadDiskReads()) {
                liloSettings.appFirstDate
            }
            if (currentFirstDate < 0) {
                liloSettings.appFirstDate = System.currentTimeMillis()
                return true
            }
            return false
        }

    val isDdgUpdate: Boolean
        get() = strictMode.resetAfter(StrictMode.allowThreadDiskReads()) {
            legacySettings.hasKeys
        }

    val shouldShowUpdateOnboarding: Boolean
        get() = isDdgUpdate && !isFirstRun

}
