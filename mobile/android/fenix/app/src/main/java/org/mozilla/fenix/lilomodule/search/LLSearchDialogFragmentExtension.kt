package org.mozilla.fenix.lilomodule.search

import mozilla.components.browser.state.selector.selectedTab
import org.mozilla.fenix.BrowserDirection
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.ext.requireComponents
import org.mozilla.fenix.search.SearchDialogFragment

/**
 * LILO: Ensures a tab exists before dismissing the search dialog.
 * This is called when user exits search without performing a search.
 */
fun SearchDialogFragment.ensureTabIsSelectedBeforeDismiss(sessionId: String?) {
    if (sessionId != null) { return }

    val selectedTab = requireComponents.core.store.state.selectedTab
    if (selectedTab == null) {
        val homeURL = org.mozilla.fenix.lilomodule.LLModule.homeUrl
        val isPrivate = requireComponents.appStore.state.mode.isPrivate
        val tabsUseCases = requireComponents.useCases.tabsUseCases
        val tabId = tabsUseCases.addTab(homeURL?.toString() ?: "about:blank", private = isPrivate)
        tabsUseCases.selectTab(tabId)
    }
    (requireActivity() as HomeActivity).openToBrowser(BrowserDirection.FromSearchDialog)
}

