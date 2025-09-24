package org.mozilla.fenix.lilomodule.tabstray.ui.banner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import mozilla.components.compose.base.menu.DropdownMenu
import mozilla.components.compose.base.menu.MenuItem
import org.mozilla.fenix.R
import org.mozilla.fenix.compose.BottomSheetHandle
import org.mozilla.fenix.tabstray.Page
import org.mozilla.fenix.tabstray.TabsTrayTestTag

private val ICON_SIZE = 24.dp
private const val MAX_WIDTH_TAB_ROW_PERCENT = 0.85f
private const val BOTTOM_SHEET_HANDLE_WIDTH_PERCENT = 0.1f
private const val ROW_HEIGHT_DP = 48
private const val TAB_INDICATOR_ROUNDED_CORNER_DP = 100

@Suppress("LongMethod")
@Composable
fun LLTabPageBanner(
    menuItems: List<MenuItem>,
    selectedPage: Page,
    normalTabCount: Int,
    privateTabCount: Int,
    syncedTabCount: Int,
    onTabPageIndicatorClicked: (Page) -> Unit,
    onDismissClick: () -> Unit,
) {
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant
    var showMenu by remember { mutableStateOf(false) }

    Column {
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.bottom_sheet_handle_top_margin)))

        BottomSheetHandle(
            onRequestDismiss = onDismissClick,
            contentDescription = stringResource(R.string.a11y_action_label_collapse),
            modifier = Modifier
                .fillMaxWidth(BOTTOM_SHEET_HANDLE_WIDTH_PERCENT)
                .align(Alignment.CenterHorizontally)
                .testTag(TabsTrayTestTag.BANNER_HANDLE),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ROW_HEIGHT_DP.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TabRow(
                selectedTabIndex = selectedPage.ordinal,
                modifier = Modifier
                    .fillMaxWidth(MAX_WIDTH_TAB_ROW_PERCENT)
                    .fillMaxHeight(),
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedPage.ordinal]),
                        shape = RoundedCornerShape(
                            topStart = TAB_INDICATOR_ROUNDED_CORNER_DP.dp,
                            topEnd = TAB_INDICATOR_ROUNDED_CORNER_DP.dp,
                        ),
                    )
                },
            ) {
                val privateTabDescription = stringResource(
                    id = R.string.tabs_header_private_tabs_counter_title,
                    privateTabCount.toString(),
                )
                val normalTabDescription = stringResource(
                    id = R.string.tabs_header_normal_tabs_counter_title,
                    normalTabCount.toString(),
                )
                val syncedTabDescription = stringResource(
                    id = R.string.tabs_header_synced_tabs_counter_title,
                    syncedTabCount.toString(),
                )

                Tab(
                    selected = selectedPage == Page.PrivateTabs,
                    onClick = { onTabPageIndicatorClicked(Page.PrivateTabs) },
                    modifier = Modifier
                        .testTag(TabsTrayTestTag.PRIVATE_TABS_PAGE_BUTTON)
                        .semantics {
                            contentDescription = privateTabDescription
                        }
                        .height(ROW_HEIGHT_DP.dp),
                    unselectedContentColor = inactiveColor,
                ) {
                    Text(text = stringResource(id = R.string.tabs_header_private_tabs_title))
                }

                Tab(
                    selected = selectedPage == Page.NormalTabs,
                    onClick = { onTabPageIndicatorClicked(Page.NormalTabs) },
                    modifier = Modifier
                        .testTag(TabsTrayTestTag.NORMAL_TABS_PAGE_BUTTON)
                        .semantics {
                            contentDescription = normalTabDescription
                        }
                        .height(ROW_HEIGHT_DP.dp),
                    unselectedContentColor = inactiveColor,
                ) {
                    Text(text = stringResource(R.string.tabs_header_normal_tabs_title))
                }

            }

            Spacer(modifier = Modifier.weight(1.0f))

            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .testTag(TabsTrayTestTag.THREE_DOT_BUTTON),
            ) {
                DropdownMenu(
                    menuItems = menuItems,
                    expanded = showMenu,
                    offset = DpOffset(x = 0.dp, y = -ICON_SIZE),
                    onDismissRequest = {
                        showMenu = false
                    },
                )
                Icon(
                    painter = painterResource(R.drawable.ic_menu),
                    contentDescription = stringResource(id = R.string.open_tabs_menu),
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}
