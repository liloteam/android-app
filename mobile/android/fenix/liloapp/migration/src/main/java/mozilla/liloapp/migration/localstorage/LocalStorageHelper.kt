package mozilla.liloapp.migration.localstorage

import android.content.Context
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import mozilla.components.concept.engine.EngineSession
import mozilla.components.support.base.log.logger.Logger
import org.json.JSONObject

class LocalStorageHelper(
    private val logger: Logger
) {

    fun getAll(targetUrl: String, webView: WebView, callback: (Map<String, String>) -> Unit) {
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                logger.debug("Page finished loading: $url")

                val jsCode = JS_CODE.trimIndent()

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
        }

        webView.loadUrl(targetUrl)

    }

    // Writes a batch of key/value pairs to window.localStorage
    fun setGeckoLocalStorageItems(
        session: EngineSession,
        items: Map<String, String>
    ) {
        val jsString = buildLocalStorageStringForItems(items)
        injectJavaScript(session, jsString)
    }

    /**
     * Injects a JS snippet into the current page using javascript: URL
     */
    private fun injectJavaScript(session: EngineSession, script: String) {
        // Encode to keep special chars safe in javascript: URL
        val encoded = android.net.Uri.encode("(function(){try{$script}catch(e){console.error(e)}})();")
        logger.debug("JS: Encoded script: $encoded")
        //session.loadUrl("javascript:$encoded")
    }

    /**
     * Build a JS string to set a batch of key/value pairs to window.localStorage
     */
    private fun buildLocalStorageStringForItems(items: Map<String, String>): String {
        // Build JS to set multiple items
        val builder = StringBuilder()
        builder.append(
            """
        (function(){
          if (typeof window === 'undefined' || !window.localStorage) { return; }
        """.trimIndent()
        )
        for ((k, v) in items) {
            // Escape quotes and backslashes for safe inline JS
            val keyEsc = k.replace("\\", "\\\\").replace("\"", "\\\"")
            val valEsc = v.replace("\\", "\\\\").replace("\"", "\\\"")
            builder.append("""localStorage.setItem("$keyEsc","$valEsc");""")
        }
        builder.append("})();")

        return builder.toString()
    }


    companion object {
        const val JS_CODE =
            """
                (function() {
                    var items = {};
                    for (var i = 0; i < localStorage.length; i++) {
                        var key = localStorage.key(i);
                        items[key] = localStorage.getItem(key);
                    }
                    return JSON.stringify(items);
                })();
            """
    }
}
