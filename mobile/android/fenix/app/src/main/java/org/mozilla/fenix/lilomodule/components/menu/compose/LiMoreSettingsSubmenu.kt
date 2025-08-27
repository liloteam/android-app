package org.mozilla.fenix.lilomodule.components.menu.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.mozilla.fenix.R
import org.mozilla.fenix.components.menu.compose.MenuGroup
import org.mozilla.fenix.components.menu.compose.MenuItemState
import org.mozilla.fenix.components.menu.store.TranslationInfo
import org.mozilla.fenix.components.menu.compose.MenuItem
import org.mozilla.fenix.theme.FirefoxTheme
import org.mozilla.fenix.theme.Theme

@Suppress("LongParameterList")
@Composable
internal fun LiMoreSettingsSubmenu(
    isPinned: Boolean,
    isInstallable: Boolean,
    hasExternalApp: Boolean,
    externalAppName: String,
    isReaderViewActive: Boolean,
    isWebCompatReporterSupported: Boolean,
    isWebCompatEnabled: Boolean,
    translationInfo: TranslationInfo,
    onWebCompatReporterClick: () -> Unit,
    onShortcutsMenuClick: () -> Unit,
    onAddToHomeScreenMenuClick: () -> Unit,
    onSaveToCollectionMenuClick: () -> Unit,
    onSaveAsPDFMenuClick: () -> Unit,
    onPrintMenuClick: () -> Unit,
    onOpenInAppMenuClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {

        MenuItem(
            label = if (isInstallable) {
                stringResource(id = R.string.browser_menu_add_app_to_homescreen)
            } else {
                stringResource(id = R.string.browser_menu_add_to_homescreen)
            },
            beforeIconPainter = painterResource(id = R.drawable.mozac_ic_add_to_homescreen_24),
            onClick = onAddToHomeScreenMenuClick,
        )

        if (hasExternalApp) {
            MenuItem(
                label = if (externalAppName != "") {
                    stringResource(id = R.string.browser_menu_open_in_fenix, externalAppName)
                } else {
                    stringResource(id = R.string.browser_menu_open_app_link)
                },
                beforeIconPainter = painterResource(id = R.drawable.mozac_ic_more_grid_24),
                state = MenuItemState.ENABLED,
                onClick = onOpenInAppMenuClick,
            )
        } else {
            MenuItem(
                label = stringResource(id = R.string.browser_menu_open_app_link),
                beforeIconPainter = painterResource(id = R.drawable.mozac_ic_more_grid_24),
                state = MenuItemState.DISABLED,
            )
        }

        MenuItem(
            label = stringResource(id = R.string.browser_menu_save_as_pdf_2),
            beforeIconPainter = painterResource(id = R.drawable.mozac_ic_save_file_24),
            onClick = onSaveAsPDFMenuClick,
        )

        MenuItem(
            label = stringResource(id = R.string.browser_menu_print_2),
            beforeIconPainter = painterResource(id = R.drawable.mozac_ic_print_24),
            onClick = onPrintMenuClick,
        )
    }
}

@PreviewLightDark
@Composable
private fun MoreSettingsSubmenuPreview() {
    FirefoxTheme {
        Column(
            modifier = Modifier.background(color = FirefoxTheme.colors.layer3),
        ) {
            MenuGroup {
                LiMoreSettingsSubmenu(
                    isPinned = true,
                    isInstallable = true,
                    hasExternalApp = true,
                    externalAppName = "Pocket",
                    isReaderViewActive = false,
                    isWebCompatReporterSupported = true,
                    isWebCompatEnabled = true,
                    translationInfo = TranslationInfo(
                        isTranslationSupported = true,
                        isPdf = false,
                        isTranslated = true,
                        translatedLanguage = "English",
                        onTranslatePageMenuClick = {},
                    ),
                    onWebCompatReporterClick = {},
                    onShortcutsMenuClick = {},
                    onAddToHomeScreenMenuClick = {},
                    onSaveToCollectionMenuClick = {},
                    onSaveAsPDFMenuClick = {},
                    onPrintMenuClick = {},
                    onOpenInAppMenuClick = {},
                )
            }
        }
    }
}

@Preview
@Composable
private fun MoreSettingsSubmenuPrivatePreview() {
    FirefoxTheme(theme = Theme.Private) {
        Column(
            modifier = Modifier.background(color = FirefoxTheme.colors.layer3),
        ) {
            MenuGroup {
                LiMoreSettingsSubmenu(
                    isPinned = false,
                    isInstallable = true,
                    hasExternalApp = false,
                    externalAppName = "Pocket",
                    isReaderViewActive = false,
                    isWebCompatReporterSupported = true,
                    isWebCompatEnabled = true,
                    translationInfo = TranslationInfo(
                        isTranslationSupported = true,
                        isPdf = false,
                        isTranslated = false,
                        translatedLanguage = "English",
                        onTranslatePageMenuClick = {},
                    ),
                    onWebCompatReporterClick = {},
                    onShortcutsMenuClick = {},
                    onAddToHomeScreenMenuClick = {},
                    onSaveToCollectionMenuClick = {},
                    onSaveAsPDFMenuClick = {},
                    onPrintMenuClick = {},
                    onOpenInAppMenuClick = {},
                )
            }
        }
    }
}
