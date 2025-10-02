package mozilla.liloapp.migration.localstorage

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.FrameLayout
import mozilla.components.support.base.log.logger.Logger
import mozilla.liloapp.migration.MigrationDataStore
import org.mozilla.fenix.LLAppConstants

class LocalStorageActivity : Activity() {

    private val logger = Logger("LILO:LOG:LS")

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Create an invisible WebView
        webView = WebView(this).apply {
            visibility = View.GONE
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
        }

        // Adds the WebView to an invisible root view
        val layout = FrameLayout(this).apply {
            setBackgroundColor(Color.TRANSPARENT)
            addView(webView)
        }

        setContentView(layout)

        // Read the local storage of the Android Webview for the Lilo search domain
        val storage = LocalStorageHelper(logger)
        storage.getAll(LLAppConstants.homeHost, webView) { result ->
            logger.debug("LocalStorage: $result")
            MigrationDataStore.localStorageRecords = result

            // Close the activity
            finish()
        }

    }
}
