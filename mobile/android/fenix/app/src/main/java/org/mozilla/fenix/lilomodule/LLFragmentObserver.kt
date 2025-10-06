package mozilla.liloapp.migration

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.preference.Preference
import mozilla.components.support.base.log.logger.Logger
import org.mozilla.fenix.R
import org.mozilla.fenix.settings.SettingsFragment
import org.mozilla.fenix.settings.requirePreference
import org.mozilla.fenix.translations.preferences.downloadlanguages.DownloadLanguagesPreferenceFragment

class LLFragmentObserver : FragmentManager.FragmentLifecycleCallbacks() {
    private val logger = Logger("LILO:Observer")

    /**
     * Called when a fragment is created.
     * Used to cusotmize the fragment according to the Lilo preferences.
     *  - Settings fragment
     *      -> hide search settings, home, passwords, credit cards, and language settings
     *      -> the More link of the Download Languages preference is hidden directly in the composable function.
     */
    override fun onFragmentCreated(
        fm: FragmentManager,
        f: Fragment,
        savedInstanceState: Bundle?
    ) {
        when (f) {
            is SettingsFragment -> {
                logger.debug("Settings fragment created")
                f.requirePreference<Preference>(R.string.pref_key_search_settings).isVisible = false
                f.requirePreference<Preference>(R.string.pref_key_home).isVisible = false
                f.requirePreference<Preference>(R.string.pref_key_passwords).isVisible = false
                f.requirePreference<Preference>(R.string.pref_key_credit_cards).isVisible = false
                f.requirePreference<Preference>(R.string.pref_key_language).isVisible = false

            }
            is DownloadLanguagesPreferenceFragment -> {
                logger.debug("Download Languages Preferences fragment created")
            }
        }
    }
}
