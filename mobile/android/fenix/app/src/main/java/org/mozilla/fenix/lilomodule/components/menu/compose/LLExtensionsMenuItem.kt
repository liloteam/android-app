package org.mozilla.fenix.lilomodule.components.menu.compose

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable

@Composable
public fun LLExtensionsMenuItem(
    isExtensionsProcessDisabled: Boolean,
    isExtensionsExpanded: Boolean,
    isPrivate: Boolean,
    webExtensionMenuCount: Int,
    allWebExtensionsDisabled: Boolean,
    onExtensionsMenuClick: () -> Unit,
    extensionSubmenu: @Composable ColumnScope.() -> Unit,
    extensionsMenuItemDescription: String?,
) {
    //At this time Lilo doest not display any extension menu item.
}
