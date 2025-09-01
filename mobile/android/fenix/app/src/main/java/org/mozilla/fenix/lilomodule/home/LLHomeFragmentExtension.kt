package org.mozilla.fenix.lilomodule.home

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.mozilla.fenix.BrowserDirection
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.LLAppConstants
import org.mozilla.fenix.ext.requireComponents
import org.mozilla.fenix.home.HomeFragment

fun HomeFragment.switchFromHomeToLilo() {
    val homeURL = LLAppConstants.AppURL.HOME.url()
    lifecycleScope.launch {
        val tabsUseCases = requireComponents.useCases.tabsUseCases
        val tabId = tabsUseCases.addTab(homeURL.toString(), private = false)
        tabsUseCases.selectTab(tabId)

        // Navigate to browser to show the Lilo tab instead of staying on Home
        (requireActivity() as HomeActivity).openToBrowser(BrowserDirection.FromHome)
    }
}
