package org.mozilla.fenix.lilo

import android.content.Context
import androidx.core.content.pm.PackageInfoCompat
import mozilla.components.support.utils.ext.getPackageInfoCompat

class LiSettings {

    fun getAppInfo(context: Context): Pair<String, String> {
        val appName = context.packageManager.getApplicationLabel(context.applicationInfo).toString()
        val pkgInfo = context.packageManager.getPackageInfoCompat(context.packageName, 0)
        val versionName = pkgInfo.versionName ?: "0"
        val versionCode = PackageInfoCompat.getLongVersionCode(pkgInfo)
        return appName to "$versionName"
//        return appName to "$versionName ($versionCode)"
    }

    companion object {
        fun customizeUserAgent(context: Context, userAgent: String?): String {
            val appInfo = LiSettings().getAppInfo(context)
            val appName = appInfo.first
            val appVersion = appInfo.second
            return "$userAgent $appName/$appVersion"
        }
    }
}
