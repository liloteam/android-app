package org.mozilla.fenix.lilomodule.components.menu.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.R
import org.mozilla.fenix.components.menu.compose.MenuItem
import org.mozilla.fenix.lilomodule.home.addNewTab

@Composable
fun LLNewTabMenuItems() {
    val context = LocalContext.current
    MenuItem(
        label = stringResource(id = R.string.mozac_browser_menu_new_tab),
        beforeIconPainter = painterResource(id = R.drawable.mozac_ic_plus_24),
        onClick = {
            (context as? HomeActivity)?.addNewTab(false)
        },
    )
    MenuItem(
        label = stringResource(id = R.string.mozac_browser_menu_new_private_tab),
        beforeIconPainter = painterResource(id = R.drawable.mozac_ic_private_mode_24),
        onClick = {
            (context as? HomeActivity)?.addNewTab(true)
        },
    )
}
