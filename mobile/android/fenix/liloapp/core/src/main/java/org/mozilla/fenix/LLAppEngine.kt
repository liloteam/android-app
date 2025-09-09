package org.mozilla.fenix

import android.content.Context
import androidx.core.content.pm.PackageInfoCompat
import mozilla.components.support.utils.ext.getPackageInfoCompat

object LLAppEngine {
    private var newHomeTab = false

    fun customizedUserAgent(context: Context, userAgent: String?): String {
        val appInfo = getAppInfo(context)
        val appName = appInfo.first
        val appVersion = appInfo.second
        return "$userAgent $appName/$appVersion"
    }

    var shouldAddHomeTab: Boolean
        get() {
            val tab = newHomeTab
            newHomeTab = false
            return tab
        }
        set(value) {
            newHomeTab = value
        }

    private fun getAppInfo(context: Context): Pair<String, String> {
        val appName = context.packageManager.getApplicationLabel(context.applicationInfo).toString()
        val pkgInfo = context.packageManager.getPackageInfoCompat(context.packageName, 0)
        val versionName = pkgInfo.versionName ?: "0"
        val versionCode = PackageInfoCompat.getLongVersionCode(pkgInfo)
        return appName to "$versionName"
//        return appName to "$versionName ($versionCode)"
    }

}
