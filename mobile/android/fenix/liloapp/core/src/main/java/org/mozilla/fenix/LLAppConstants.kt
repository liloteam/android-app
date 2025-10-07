package org.mozilla.fenix

import android.net.Uri

object LLAppConstants {
    const val homeHost = "search.lilo.org"
    const val privacyPolicyURL = "https://www.lilo.org/votre-vie-privee-avec-lilo/"
    const val supportURL = "https://support.lilo.org"
    const val searchURL = "https://search.lilo.org?q=%s"
    const val suggestURL = "https://www.bing.com/osjson.aspx?query=%s"
    const val aboutURL = "www.lilo.org"

    enum class Parameter(val queryItem: Pair<String, String>) {
        HOME("t" to "homemobile"),
        INTRO("act" to "introduction"),
        LOGIN("ma" to "login")
    }

    enum class Path(val path: String) {
        MY_ACCOUNT("/mon-compte"),
        ABOUT("/qui-est-lilo")
    }

    enum class AppURL {
        HOME,
        LOGIN,
        CONNECTION,
        ABOUT;

        fun url(isFirst: Boolean = false): Uri? {
            val params = mutableListOf<Parameter>()
            var path: String? = null
            var baseURL: String = homeHost

            when (this) {
                HOME -> {
                    if (isFirst) params.add(Parameter.INTRO)
                }
                LOGIN -> {
                    params.add(Parameter.LOGIN)
                }
                CONNECTION -> {
                    path = Path.MY_ACCOUNT.path
                }
                ABOUT -> {
                    baseURL = aboutURL
                    path = Path.ABOUT.path
                }
            }

            return buildURL(baseURL, path, params)
        }

        private fun buildURL(host: String, path: String?, parameters: List<Parameter>): Uri? {
            val builder = Uri.Builder()
                .scheme("https")
                .authority(host)

            path?.let { builder.appendEncodedPath(it) }

            parameters.forEach {
                builder.appendQueryParameter(it.queryItem.first, it.queryItem.second)
            }

            return builder.build()
        }
    }

    object SearchEngine {
        const val NAME = "Lilo"
        const val ICON = ""
        const val RESULT = searchURL
        const val SUGGEST = suggestURL

    }
}
