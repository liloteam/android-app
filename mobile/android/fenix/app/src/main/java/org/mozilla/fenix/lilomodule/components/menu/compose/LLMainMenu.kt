package org.mozilla.fenix.lilomodule.components.menu.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.LLAppEngine
import org.mozilla.fenix.R
import org.mozilla.fenix.components.menu.compose.LibraryMenuItem
import org.mozilla.fenix.components.menu.compose.MenuItem
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.lilomodule.home.addNewTab
import org.mozilla.fenix.utils.maybeShowAddSearchWidgetPrompt

/**
 * Horizontal menu group without the Passwords section. Only with History,
 * Bookmarks and Downloads.
 */
@Composable
fun LLLibraryMenuGroup(
    onBookmarksMenuClick: () -> Unit,
    onHistoryMenuClick: () -> Unit,
    onDownloadsMenuClick: () -> Unit,
    onPasswordsMenuClick: () -> Unit,
) {
    val spacerWidth = 2.dp
    val innerRounding = 4.dp
    val outerRounding = 28.dp

    val leftShape = RoundedCornerShape(
        topStart = outerRounding, topEnd = innerRounding,
        bottomStart = outerRounding, bottomEnd = innerRounding,
    )
    val middleShape = RoundedCornerShape(innerRounding)
    val rightShape = RoundedCornerShape(
        topStart = innerRounding,
        topEnd = outerRounding, bottomStart = innerRounding, bottomEnd = outerRounding,
    )

    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .semantics {
                this.collectionInfo = CollectionInfo(
                    rowCount = 1,
                    columnCount = 3,
                )
            },
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LibraryMenuItem(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            iconRes = R.drawable.mozac_ic_history_24,
            labelRes = R.string.library_history,
            shape = leftShape,
            index = 0,
            onClick = onHistoryMenuClick,
        )

        Spacer(Modifier.width(spacerWidth))

        LibraryMenuItem(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            iconRes = R.drawable.mozac_ic_bookmark_tray_fill_24,
            labelRes = R.string.library_bookmarks,
            shape = middleShape,
            index = 1,
            onClick = onBookmarksMenuClick,
        )

        Spacer(Modifier.width(spacerWidth))

        LibraryMenuItem(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            iconRes = R.drawable.mozac_ic_download_24,
            labelRes = R.string.library_downloads,
            shape = rightShape,
            index = 2,
            onClick = onDownloadsMenuClick,
        )
    }
}

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

@Composable
fun LLAddWidgetMenuItem() {
    val context = LocalContext.current
    if (!context.settings().searchWidgetInstalled) {
        val itemResources = LLAppEngine.addWidgetMenuResources
        MenuItem(
            label = stringResource(id = itemResources.second),
            beforeIconPainter = painterResource(id = itemResources.first),
            onClick = {
                (context as? HomeActivity)?.also { maybeShowAddSearchWidgetPrompt(it) }
            },
        )
    }
}
