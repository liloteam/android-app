package org.mozilla.fenix

import android.net.Uri

object LLAppConstants {
    const val homeHost = "search.lilo.org"
    //const val privacyPolicyURL = "https://www.lilo.org/votre-vie-privee-avec-lilo/"
    const val supportURL = "https://support.lilo.org"
    const val searchURL = "https://search.lilo.org?q=%s"
    const val suggestURL = "https://www.bing.com/osjson.aspx?query=%s"
    const val aboutBaseURL = "www.lilo.org"
    const val homeUrl = "https://www.lilo.org/"

    //Firefox URLs
    const val GOOGLE_URL = "https://www.google.com/webhp?client=lilo&channel=ts"
    const val FAQ_URL = "https://www.lilo.org/faq/?t=user"


    enum class Parameter(val queryItem: Pair<String, String>) {
        HOME("t" to "homemobile"),
        INTRO("act" to "introduction"),
        LOGIN("ma" to "login")
    }

    enum class Path(val path: String) {
        MY_ACCOUNT("/mon-compte"),
        ABOUT("/qui-est-lilo"),
        PRIVACY_POLICY("/votre-vie-privee-avec-lilo"),
        TERMS_OF_SERVICE("/cgu-lilo")
    }

    enum class AppURL {
        HOME,
        LOGIN,
        CONNECTION,
        ABOUT,
        PRIVACY_POLICY,
        TERMS_OF_SERVICE;

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
                    baseURL = aboutBaseURL
                    path = Path.ABOUT.path
                }
                PRIVACY_POLICY -> {
                    baseURL = aboutBaseURL
                    path = Path.PRIVACY_POLICY.path
                }
                TERMS_OF_SERVICE -> {
                    baseURL = aboutBaseURL
                    path = Path.TERMS_OF_SERVICE.path
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

    object OnboardingImage {
        const val WELCOME = "onboarding-lilo-welcome.svg"
        const val ACCOUNT = "onboarding-lilo-account.svg"
        const val CONFIDENTIALITY = "onboarding-lilo-update-confidentiality.svg"
        const val NAVIGATION = "onboarding-lilo-update-navigation.svg"
    }
}
