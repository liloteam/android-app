package mozilla.liloapp.migration

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.pm.PackageInfoCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import mozilla.components.support.base.log.logger.Logger
import mozilla.components.support.utils.ext.getPackageInfoCompat
import org.mozilla.fenix.BrowserDirection
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.LLAppConstants
import org.mozilla.fenix.R
import org.mozilla.fenix.ext.getPreferenceKey
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.settings.SettingsFragment
import org.mozilla.fenix.settings.requirePreference
import org.mozilla.fenix.translations.preferences.downloadlanguages.DownloadLanguagesPreferenceFragment

class LLFragmentObserver : FragmentManager.FragmentLifecycleCallbacks() {
    private val logger = Logger("LILO:Observer")

    /**
     * Called when a fragment is created.
     * Used to cusotmize the fragment according to the Lilo preferences.
     *  - Settings fragment - Search section
     *      -> hide search settings, home, passwords, credit cards, and language settings
     *      -> the More link of the Download Languages preference is hidden directly in the composable function.
     *  - Settings fragment - Privacy and security section
     *      -> hide private browsing, https only, tracking protection, delete browsing data on quit, notifications
     *      -> hide the cookie banner private mode option which is shown when browsing protection is changed.
     *  - Settings fragment - Advanced
     *      -> hide Advanced section
     *      -> hide addons, link sharing, open links in apps, downloads, leakcanary, and remote debugging
     *  - Settings fragment - About
     *    -> hide the About menu
     *    -> create a new About Lilo menu with the app name and version number. Click on this menu
     *    to open the Lilo about Web page.
     */
    override fun onFragmentCreated(
        fm: FragmentManager,
        f: Fragment,
        savedInstanceState: Bundle?
    ) {
        when (f) {
            is SettingsFragment -> {
                logger.debug("Settings fragment created")
                // Search menu
                f.requirePreference<Preference>(R.string.pref_key_search_settings).isVisible = false
                f.requirePreference<Preference>(R.string.pref_key_home).isVisible = false
                f.requirePreference<Preference>(R.string.pref_key_passwords).isVisible = false
                f.requirePreference<Preference>(R.string.pref_key_credit_cards).isVisible = false
                f.requirePreference<Preference>(R.string.pref_key_language).isVisible = false

                // Privacy and security menu
                f.requirePreference<Preference>(R.string.pref_key_private_browsing).isVisible = false
                f.requirePreference<Preference>(R.string.pref_key_https_only_settings).isVisible = false
                f.requirePreference<Preference>(R.string.pref_key_tracking_protection_settings).isVisible = false
                f.requirePreference<Preference>(R.string.pref_key_delete_browsing_data_on_quit_preference).isVisible = false
                f.requirePreference<Preference>(R.string.pref_key_notifications).isVisible = false
                f.requirePreference<SwitchPreference>(R.string.pref_key_cookie_banner_private_mode).isVisible = false

                // Advanced menu
                f.requirePreference<Preference>(R.string.pref_key_advanced).isVisible = false
                f.requirePreference<Preference>(R.string.pref_key_addons).isVisible = false
                f.requirePreference<Preference>(R.string.pref_key_link_sharing).isVisible = false
                f.requirePreference<Preference>(R.string.pref_key_open_links_in_apps).isVisible = false
                f.requirePreference<Preference>(R.string.pref_key_downloads).isVisible = false
                f.requirePreference<Preference>(R.string.pref_key_leakcanary).isVisible = false
                f.requirePreference<SwitchPreference>(R.string.pref_key_remote_debugging).isVisible = false

                // About menu
                f.requirePreference<Preference>(R.string.pref_key_about).isVisible = false
                f.preferenceManager?.let { manager ->
                    val screen = f.preferenceScreen ?: return
                    val appName = f.getString(R.string.app_name)
                    val aboutPref = Preference(f.requireContext()).apply {
                        key = f.getString(R.string.pref_key_about) + "_lilo"
                        title = f.getString(R.string.preferences_about, appName)
                        summary = appVersion(f.requireContext())
                        isIconSpaceReserved = false
                        setOnPreferenceClickListener {
                            LLAppConstants.AppURL.ABOUT.url()?.let { url ->
                                openLinkInNormalTab(url.toString(), f)
                            }
                            true
                        }
                    }

                    screen.addPreference(aboutPref)
                }
            }
            is DownloadLanguagesPreferenceFragment -> {
                logger.debug("Download Languages Preferences fragment created")
            }
        }
    }

    /**
     * Called when a fragment is resumed.
     *  - Settings fragment - Privacy and security section
     *      -> hide the cookie banner private mode option which is shown when browsing protection is changed.
     *  - Settings fragment - Advanced
     *      -> link sharing and remote debugging
     */
    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        super.onFragmentResumed(fm, f)

        when (f) {
            is SettingsFragment -> {
                logger.debug("Settings fragment resumed")
                f.requirePreference<SwitchPreference>(R.string.pref_key_cookie_banner_private_mode).isVisible = false
                val context = f.requireContext()
                val prefs = context.settings().preferences
                prefs.edit().apply {
                    putBoolean(
                        context.getPreferenceKey(R.string.pref_key_cookie_banner_private_mode),
                        false
                    )
                }

                //Advanced menu
                f.requirePreference<Preference>(R.string.pref_key_link_sharing).isVisible = false
                f.requirePreference<SwitchPreference>(R.string.pref_key_remote_debugging).isVisible = false
            }
        }
    }

    private fun appVersion(context: Context) : String {
        val aboutText = try {
            val packageInfo =
                context.packageManager.getPackageInfoCompat(context.packageName, 0)
            val versionCode = PackageInfoCompat.getLongVersionCode(packageInfo).toString()

            String.format(
                "%s (Build #%s)",
                packageInfo.versionName,
                versionCode,
            )
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
        return aboutText
    }

    private fun openLinkInNormalTab(url: String, fragment: Fragment) {
        (fragment.activity as HomeActivity).openToBrowserAndLoad(
            searchTermOrURL = url,
            newTab = true,
            from = BrowserDirection.FromAbout,
        )
    }
}
