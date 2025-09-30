package mozilla.liloapp.migration

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mozilla.components.support.base.log.logger.Logger
import mozilla.liloapp.migration.cookie.CookieRetriever
import mozilla.liloapp.migration.localstorage.LocalStorageHelper
import kotlin.coroutines.CoroutineContext

class MigrationManager(
    private val applicationContext: Context,
    coroutineContext: CoroutineContext = Dispatchers.IO,
    private val migrationStorage: MigrationStorage = MigrationStorage(applicationContext)
) {

    private val logger = Logger("LILO:LOG:MIGRATION")

    private var scope = CoroutineScope(coroutineContext)

    /**
     * Retrieve the open tabs from the DDG's SQlite data base such they could be restored in to the Fenix context.
     * The creation part is not implemented yet.
     */
    fun restoreTabs() = scope.launch {
        logger.debug("Restoring tabs")
        migrationStorage.getTabsList().forEach { tab ->
            logger.debug("Tab to restore: ${tab.title} - ${tab.url}")
        }
    }

    /**
     * Retrieve the cookies from the Android cookie manager and set them to Gecko although a Web extension.
     */
    fun migrateAllCookies(webStorageFeature: LLWebStorageFeature?) {
        val cookies = CookieRetriever(applicationContext).getAllCookies()
        if (cookies.isNotEmpty()) {
            cookies.forEach { cookie ->
                // Post the cookie to the Web extension whether the communication is established.
                webStorageFeature?.postCookie(cookie)
            }
        }
        else {
            logger.info("No cookie found")
        }
    }

    fun debugLocalStorage() {
        LocalStorageHelper(logger, applicationContext).getAll { result ->
            logger.debug("Local storage: $result")
        }
    }
}
