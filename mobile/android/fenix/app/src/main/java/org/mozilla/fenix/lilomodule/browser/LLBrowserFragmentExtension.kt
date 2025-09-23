package org.mozilla.fenix.lilomodule.browser

import org.mozilla.fenix.browser.BrowserFragment
import org.mozilla.fenix.components.toolbar.BrowserToolbarView

fun BrowserFragment.initializeLiloUI() {
    // Disable the site info button in the browser toolbar.
    (browserToolbarView as? BrowserToolbarView)?.toolbar?.display?.setOnSiteInfoClickedListener(null)


}
