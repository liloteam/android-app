package mozilla.liloapp.migration.cookie

import android.content.Context
import android.net.Uri
import android.webkit.CookieManager
import mozilla.components.support.base.log.logger.Logger

class CookieRetriever(
    private val context: Context
) {
    private val logger = Logger("LILO:LOG:COOKIE")

    private val cookieManager = CookieManager.getInstance()

    fun getAllCookies(): List<CookieModel> {
        if (cookieManager.hasCookies()) {
            logger.info("Cookies found...")
        }
        else {
            logger.info("No cookie found.")
        }

        var allCookies: MutableList<CookieModel> = mutableListOf()
        domains.forEach { domain ->
            val cookies = cookiesForDomain(domain)
            if (!cookies.isNullOrEmpty()) allCookies.addAll(cookies)
        }

        return allCookies
    }

    private fun cookiesForDomain(domain: String): List<CookieModel>? {
        var cookies: MutableList<CookieModel>? = null

        val cookiesString = cookieManager.getCookie(domain)

        if (cookiesString.isNullOrEmpty()) {
            logger.info("No cookie found for the domain: $domain")
        } else {
            logger.info("Cookies for $domain:")

            cookies = mutableListOf()
            cookiesString.split(";").forEach { cookie ->
                logger.info("  - $cookie")
                val cookieParts = cookie.trim().split("=")
                if (cookieParts.size == 2) {
                    val uri: Uri = Uri.Builder()
                        .scheme("https")
                        .authority(domain)
                        .build()
                    val cookieModel = CookieModel(
                        url = uri.toString(),
                        name = cookieParts[0],
                        value = cookieParts[1]
                    )

                    cookies.add(cookieModel)
                }
            }
        }

        return cookies
    }

    companion object {
        private val domains = listOf(
            "lilo.org",
            "www.lilo.org",
            "search.lilo.org",
            "clarity.ms",
            "www.clarity.ms",
            "*.clarity.ms",
            "touchclarity.com",
            "clarity.com",
            "www.clarity.com",
            )
    }
}
