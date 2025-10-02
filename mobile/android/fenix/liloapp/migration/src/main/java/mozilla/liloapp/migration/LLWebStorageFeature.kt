package mozilla.liloapp.migration

import mozilla.components.support.base.log.logger.Logger
import mozilla.liloapp.migration.cookie.CookieModel
import org.json.JSONObject
import org.mozilla.gecko.util.ThreadUtils.runOnUiThread
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.WebExtension

object LLWebStorageFeature {
    val logger = Logger("LILO:WSF:")

    private const val EXTENSION_LOCATION = "resource://android/assets/webstorage/"
    private const val EXTENSION_ID = "web-storage@lilo.org"
    private const val EXTENSION_VERSION = "1.0"

    private var communicationPort: WebExtension.Port? = null
    var onPortConnected: (() -> Unit)? = null
    var onSuccessMessage: (() -> Unit)? = null

    private val portDelegate = object: WebExtension.PortDelegate {
        override fun onPortMessage(message: Any, port: WebExtension.Port) {
            try {
                logger.info("on port message: $message")
                val resp = message.toMap()
                logger.info("on port message, resp: $resp")
                when (resp["type"]) {
                    "INJECT_LOCALSTORAGE" -> {
                        logger.info("on port message, success: ${resp["success"]}")
                        onSuccessMessage?.invoke()
                        onSuccessMessage = null
                    }
                }

            } catch (e: Exception) {
                logger.error("Error while handling message from extension", e)
            }
        }

        override fun onDisconnect(port: WebExtension.Port) {
            logger.info("WSFeature disconnected")
            if (port == communicationPort) communicationPort = null
        }
    }

    private val messageDelegate = object: WebExtension.MessageDelegate {

        override fun onConnect(port: WebExtension.Port) {
            super.onConnect(port)
            logger.info("WSFeature connected")
            port.setDelegate(portDelegate)
            communicationPort = port
            onPortConnected?.invoke()
            super.onConnect(port)
        }

        override fun onMessage(
            nativeApp: String,
            message: Any,
            sender: WebExtension.MessageSender
        ): GeckoResult<Any>? {
            logger.info("on message: $message \n with native app: $nativeApp")
            return super.onMessage(nativeApp, message, sender)
        }
    }

    fun install(runtime: GeckoRuntime, onConnected: (() -> Unit)?): LLWebStorageFeature {

        // Let's make sure the extension is installed
        runtime
            .webExtensionController
            .ensureBuiltIn(EXTENSION_LOCATION, EXTENSION_ID)
            .accept {
                it?.let { extension ->
                    runOnUiThread {
                        runtime.webExtensionController.setAllowedInPrivateBrowsing(extension, true)
                        extension.setMessageDelegate(messageDelegate, "lilobrowser")
                        this.onPortConnected = onConnected
                    }
                }
            }
        return this
    }

    fun postCookie(cookie: CookieModel) {
        communicationPort?.let { port ->
            val obj = cookie.toJSONObject()
            logger.info("Posting cookie: $obj")
            port.postMessage(obj)
        }?:run { logger.error("Error: No communication port") }
    }

    fun postLocalStorageItems(items: Map<String, String>, complete: () -> Unit) {
        communicationPort?.let { port ->
            val message = mapOf("type" to "INJECT_LOCALSTORAGE", "items" to items)
            logger.info("Posting local storage items with message: $message")
            onSuccessMessage = complete
            port.postMessage(JSONObject(message))
        }?:run { logger.error("Error: No communication port") }
    }

}


internal fun Any?.toMap(): Map<String, Any> {
    if (this == null) return emptyMap()

    val cleaned = this.toString()
        .removePrefix("\"")
        .removeSuffix("\"")
        .replace("\\\\", "\\")
        .replace("\\\"", "\"")

    return try {
        val json = JSONObject(cleaned)
        val keys = json.keys()
        val map = mutableMapOf<String, Any>()
        while (keys.hasNext()) {
            val key = keys.next()
            map[key] = json.get(key)
        }
        map
    } catch (e: Exception) {
        emptyMap()
    }
}

