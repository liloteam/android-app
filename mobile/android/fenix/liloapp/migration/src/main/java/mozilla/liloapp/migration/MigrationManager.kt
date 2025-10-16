package mozilla.liloapp.migration

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mozilla.components.concept.fetch.Client
import mozilla.components.support.base.log.logger.Logger
import mozilla.liloapp.migration.bookmark.BookmarkApiService
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

    /**
     * Retrieve the local storage items from the Android shared preferences and set them to Gecko.
     */
    fun migrateLocalStorageItems(webStorageFeature: LLWebStorageFeature?, items: Map<String, String>, complete: () -> Unit) {
        webStorageFeature?.postLocalStorageItems(items, complete)
    }

    /**
     * Fetch bookmarks from the Lilo API and log them to Logcat
     * 
     * @param token The user token to fetch bookmarks for
     */
    fun fetchRemoteBookmarks(client: Client, token: String) = scope.launch {
        logger.debug("Fetching remote bookmarks for token: $token")
        
        val bookmarkApiService = BookmarkApiService(client)
        val bookmarks = bookmarkApiService.fetchBookmarks(token)
        
        if (bookmarks.isNotEmpty()) {
            logger.info("Retrieved ${bookmarks.size} bookmarks from API:")
            bookmarks.forEachIndexed { index, bookmark ->
                logger.info("Bookmark #${index + 1}:")
                logger.info("  ID: ${bookmark.id}")
                logger.info("  Title: ${bookmark.title}")
                logger.info("  URL: ${bookmark.url}")
                logger.info("  Color: ${bookmark.color}")
                logger.info("  Icon: ${bookmark.icon}")
            }
        } else {
            logger.info("No bookmarks retrieved from API")
        }
    }

}
