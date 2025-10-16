package org.mozilla.fenix.lilomodule.bookmark

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mozilla.appservices.places.BookmarkRoot
import mozilla.components.browser.storage.sync.PlacesBookmarksStorage
import mozilla.components.concept.storage.BookmarksStorage
import mozilla.components.concept.storage.HistoryStorage
import mozilla.components.support.base.log.logger.Logger
import mozilla.liloapp.migration.bookmark.BookmarkModel
import org.mozilla.fenix.components.bookmarks.BookmarksUseCase
import org.mozilla.fenix.ext.bookmarkStorage
import org.mozilla.fenix.ext.components
import kotlin.coroutines.CoroutineContext

/**
 * Service to save bookmarks from the Lilo API into the native Fenix bookmarks storage
 */
class BookmarkSaver(
    private val context: Context,
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
    private val bookmarksStorage: PlacesBookmarksStorage = context.bookmarkStorage,
    private val historyStorage: HistoryStorage = context.components.core.historyStorage,
) {
    private val logger = Logger("LILO:BOOKMARK:SAVER")
    private val bookmarksUseCase = BookmarksUseCase(bookmarksStorage, historyStorage)

    private val scope = CoroutineScope(coroutineContext)

    fun savingBookmarks(bookmarks: List<BookmarkModel>) {
        scope.launch {
            val savedCount = saveBookmarks(bookmarks)
            logger.debug("Bookmarks successfully saved: $savedCount")
        }
    }

    /**
     * Save a list of bookmarks from the API into the native bookmarks storage
     *
     * @param bookmarks List of BookmarkModel objects from the API
     * @return Number of bookmarks successfully saved
     */
    suspend fun saveBookmarks(bookmarks: List<BookmarkModel>): Int = withContext(Dispatchers.IO) {
        var savedCount = 0
        var skippedCount = 0
        var errorCount = 0

        logger.debug("Starting to save ${bookmarks.size} bookmarks to native storage")

        bookmarks.forEach { bookmark ->
            try {
                val result = bookmarksUseCase.addBookmark(
                    url = bookmark.url,
                    title = bookmark.title,
                    parentGuid = BookmarkRoot.Mobile.id, // Save to mobile bookmarks folder
                )

                if (result != null) {
                    savedCount++
                    logger.debug("Successfully saved bookmark: ${bookmark.title} (${bookmark.url})")
                } else {
                    skippedCount++
                    logger.debug("Skipped bookmark (already exists): ${bookmark.title} (${bookmark.url})")
                }
            } catch (e: Exception) {
                errorCount++
                logger.error("Failed to save bookmark: ${bookmark.title} (${bookmark.url})", e)
            }
        }

        logger.debug("Bookmark save operation completed:\n" +
            "  - Successfully saved: $savedCount\n" +
            "  - Skipped (already exists): $skippedCount\n" +
            "  - Errors: $errorCount\n")

        return@withContext savedCount
    }

    /**
     * Save a single bookmark from the API into the native bookmarks storage
     *
     * @param bookmark BookmarkModel object from the API
     * @return The GUID of the saved bookmark, or null if not saved
     */
    suspend fun saveBookmark(bookmark: BookmarkModel): String? = withContext(Dispatchers.IO) {
        try {
            val result = bookmarksUseCase.addBookmark(
                url = bookmark.url,
                title = bookmark.title,
                parentGuid = BookmarkRoot.Mobile.id,
            )

            if (result != null) {
                logger.debug("Successfully saved bookmark: ${bookmark.title} (${bookmark.url})")
            } else {
                logger.debug("Skipped bookmark (already exists): ${bookmark.title} (${bookmark.url})")
            }

            return@withContext result
        } catch (e: Exception) {
            logger.error("Failed to save bookmark: ${bookmark.title} (${bookmark.url})", e)
            return@withContext null
        }
    }

    /**
     * Check if a bookmark with the given URL already exists in the storage
     *
     * @param url The URL to check
     * @return True if a bookmark with this URL already exists
     */
    suspend fun bookmarkExists(url: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val existingBookmarks = bookmarksStorage.getBookmarksWithUrl(url)
            val exists = existingBookmarks.any { it.url == url }
            logger.debug("Bookmark exists check for $url: $exists")
            return@withContext exists
        } catch (e: Exception) {
            logger.error("Error checking if bookmark exists: $url", e)
            return@withContext false
        }
    }

    /**
     * Get the total number of bookmarks in the storage
     *
     * @return Number of bookmarks in the storage
     */
    suspend fun getBookmarkCount(): Int = withContext(Dispatchers.IO) {
        try {
            val bookmarks = bookmarksStorage.getBookmarksWithUrl("") // Empty string returns all bookmarks
            val count = bookmarks.size
            logger.debug("Total bookmarks in storage: $count")
            return@withContext count
        } catch (e: Exception) {
            logger.error("Error getting bookmark count", e)
            return@withContext 0
        }
    }
}
