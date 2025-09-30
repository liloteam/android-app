package mozilla.liloapp.migration

import mozilla.components.support.base.log.logger.Logger
import mozilla.liloapp.migration.cookie.CookieModel
import org.mozilla.gecko.util.ThreadUtils.runOnUiThread
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.WebExtension

object LLWebStorageFeature {
    val logger = Logger("LILO:LOG:WEB")

    private const val EXTENSION_LOCATION = "resource://android/assets/webstorage/"
    private const val EXTENSION_ID = "web-storage@lilo.org"
    private const val EXTENSION_VERSION = "1.0"

    private var communicationPort: WebExtension.Port? = null
    var onPortConnected: (() -> Unit)? = null

    private val portDelegate = object: WebExtension.PortDelegate {
        override fun onPortMessage(message: Any, port: WebExtension.Port) {
            try {
                logger.info("WSFeature: Message from extension: $message")

            } catch (e: Exception) {
                logger.error("WSFeature: Error while handling message from extension", e)
            }
        }

        override fun onDisconnect(port: WebExtension.Port) {
            logger.info("WSFeature: Extension disconnected")
            if (port == communicationPort) communicationPort = null
        }
    }

    private val messageDelegate = object: WebExtension.MessageDelegate {

        override fun onConnect(port: WebExtension.Port) {
            super.onConnect(port)
            logger.info("WSFeature: Extension connected")
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
            logger.info("WSFeature: Message from extension with native app: $nativeApp")
            logger.info("WSFeature: Message from extension: $message")
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
            logger.info("WSFeature: Posting cookie: $obj")
            port.postMessage(obj)
        }?:run { logger.info("WSFeature: No communication port") }
    }
}
