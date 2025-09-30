package mozilla.liloapp.migration.localstorage

import android.content.Context
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import mozilla.components.support.base.log.logger.Logger
import org.json.JSONObject

class LocalStorageHelper(
    private val logger: Logger,
    private val context: Context,
) {
    private val webView by lazy {
        val webView = WebView(context)

        // Configure les paramètres du WebView
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            useWideViewPort = true
            loadWithOverviewMode = true
        }
        webView.visibility = View.GONE

        // Permet la navigation dans le WebView
        webView.webViewClient = WebViewClient()

        // Charge une URL
        webView.loadUrl("https://search.lilo.org")

        return@lazy webView
    }

    fun getAll(callback: (Map<String, String>) -> Unit) {
        val jsCode = """
            (function() {
                var items = {};
                for (var i = 0; i < localStorage.length; i++) {
                    var key = localStorage.key(i);
                    items[key] = localStorage.getItem(key);
                }
                return JSON.stringify(items);
            })();
        """.trimIndent()

        webView.evaluateJavascript(jsCode) { result ->
            logger.debug("JS result: $result")
            var map: Map<String, String> = emptyMap()
            val cleaned = result?.removePrefix("\"")?.removeSuffix("\"")?.replace("\\\\", "\\")?.replace("\\\"", "\"")
            logger.debug("Cleaned JS result: $cleaned")
            cleaned?.let {
                map = try {
                    JSONObject(cleaned).let { json ->
                        val keys = json.keys()
                        val output = mutableMapOf<String, String>()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            output[key] = json.getString(key)
                        }
                        output
                    }
                } catch (e: Exception) {
                    logger.error("Error parsing JSON", e)
                    emptyMap()
                }
            }?:run {
                logger.error("Error with JS result: $result")
            }
            callback(map)
        }
    }

    fun syncWebView(): WebView {
        return webView
    }
}
