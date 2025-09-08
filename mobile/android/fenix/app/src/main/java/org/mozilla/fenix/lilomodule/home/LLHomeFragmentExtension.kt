package org.mozilla.fenix.lilomodule.home

import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import mozilla.components.browser.state.selector.findCustomTabOrSelectedTab
import mozilla.components.browser.state.state.SessionState
import org.mozilla.fenix.BrowserDirection
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.LLAppConstants
import org.mozilla.fenix.ext.requireComponents
import org.mozilla.fenix.home.HomeFragment

/**
 * Switch from Home to Lilo or the last current tab if it exists.
 * A new tab is added only if needed.
 */
fun HomeFragment.switchFromHomeToLilo() {
    val homeURL = LLAppConstants.AppURL.HOME.url()
    lifecycleScope.launch {
        // If no Web page is already displayed in a tab then a new tab is created
        // with the Lilo's home page elsewhere the last tab will be shown.
        getCurrentTab()?.let {  }?: run {
            val tabsUseCases = requireComponents.useCases.tabsUseCases
            val tabId = tabsUseCases.addTab(homeURL.toString(), private = false)
            tabsUseCases.selectTab(tabId)
        }

        // Navigate to browser to show the Lilo tab instead of staying on Home
        (requireActivity() as HomeActivity).openToBrowser(BrowserDirection.FromHome)
    }
}

/**
 * Init the native home page by hiding the app bar content.
 */
fun HomeFragment.initMinimalistHomePage() {
    binding.homeAppBarContent.isVisible = false
}

/**
 * Get the current tab excluding system tabs.
 */
private fun HomeFragment.getCurrentTab(): SessionState? {
    val currentTab = requireComponents.core.store.state.findCustomTabOrSelectedTab(null)
    val currentUrl = currentTab?.content?.url?.takeIf { url ->
        url != "about:home" && !url.startsWith("about:")
    }
    return currentUrl?.let { currentTab }
}
