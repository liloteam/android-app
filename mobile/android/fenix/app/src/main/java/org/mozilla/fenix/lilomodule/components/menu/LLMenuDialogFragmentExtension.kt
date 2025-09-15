package org.mozilla.fenix.lilomodule.components.menu

import org.mozilla.fenix.BrowserDirection
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.LLAppConstants
import org.mozilla.fenix.components.menu.BrowserNavigationParams
import org.mozilla.fenix.components.menu.MenuDialogFragment
import org.mozilla.fenix.ext.runIfFragmentIsAttached
import org.mozilla.fenix.settings.SupportUtils

fun MenuDialogFragment.goToLoginPage() {
    val url = LLAppConstants.AppURL.LOGIN.url().toString()
    openToBrowser(url)
}

private fun MenuDialogFragment.openToBrowser(url: String?) = runIfFragmentIsAttached {
    url?.let {
        (activity as HomeActivity).openToBrowserAndLoad(
            searchTermOrURL = url,
            newTab = true,
            from = BrowserDirection.FromMenuDialogFragment,
        )
    }
}
