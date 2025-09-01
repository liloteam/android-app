package org.mozilla.fenix

import android.net.Uri

object LLAppConstants {
    const val homeHost = "search.lilo.org"
    const val privacyPolicyURL = "https://www.lilo.org/votre-vie-privee-avec-lilo/"
    const val supportURL = "https://support.lilo.org"

    enum class Parameter(val queryItem: Pair<String, String>) {
        HOME("t" to "homemobile"),
        INTRO("act" to "introduction"),
        LOGIN("ma" to "login")
    }

    enum class Path(val path: String) {
        MY_ACCOUNT("/mon-compte")
    }

    enum class AppURL {
        HOME,
        LOGIN,
        CONNECTION;

        fun url(isFirst: Boolean = false): Uri? {
            val params = mutableListOf<Parameter>()
            var path: String? = null

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
            }

            return buildURL(LLAppConstants.homeHost, path, params)
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
}
