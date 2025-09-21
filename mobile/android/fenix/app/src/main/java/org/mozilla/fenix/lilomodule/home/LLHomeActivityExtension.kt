package org.mozilla.fenix.lilomodule.home

import androidx.navigation.fragment.NavHostFragment
import mozilla.components.support.base.log.logger.Logger
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.NavGraphDirections
import org.mozilla.fenix.browser.browsingmode.BrowsingMode
import org.mozilla.fenix.lilomodule.LLModule

fun HomeActivity.addNewTab(private: Boolean = false) {
    val navHostFragment = this.supportFragmentManager?.fragments?.firstOrNull() as? NavHostFragment
    navHostFragment?.let { navHost ->
        LLModule.setShouldAddHomeTab(true)

        this.browsingModeManager.mode = if (private) BrowsingMode.Private else BrowsingMode.Normal
        navHost.navController.navigate(
            NavGraphDirections.actionGlobalHome(
                focusOnAddressBar = false,
            ),
        )
    }?:run {
        Logger("LILO:DBG").debug("adding new tab: nav host fragment is null")
    }

}
