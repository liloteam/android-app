package org.mozilla.fenix.lilomodule.settings

import android.content.Context
import mozilla.components.browser.state.action.TranslationsAction
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.ext.settings

data class LLSettings(
    val context: Context
) {
    fun updateOfferTranslationOption(enabled: Boolean) {
        val browserStore = context.components.core.store
        browserStore.dispatch(
            TranslationsAction.UpdateGlobalOfferTranslateSettingAction(
                offerTranslation = enabled,
            ),
        )
        context.settings().offerTranslation = enabled
    }
}
