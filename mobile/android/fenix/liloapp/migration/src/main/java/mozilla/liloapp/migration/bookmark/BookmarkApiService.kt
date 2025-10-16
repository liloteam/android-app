package mozilla.liloapp.migration.bookmark

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mozilla.components.concept.fetch.Client
import mozilla.components.concept.fetch.Request
import mozilla.components.concept.fetch.isSuccess
import mozilla.components.support.base.log.logger.Logger
import org.json.JSONArray

/**
 * Service to retrieve bookmarks from the Lilo API using Mozilla's concept-fetch Client
 */
class BookmarkApiService(
    private val client: Client,
) {
    private val logger = Logger("LILO:BOOKMARK")
    private val baseUrl = "https://ws.lilo.org/users"

    /**
     * Fetch bookmarks from the Lilo API for a given user token
     * 
     * @param token The user token to fetch bookmarks for
     * @return List of BookmarkModel objects, or empty list if an error occurred
     */
    suspend fun fetchBookmarks(token: String): List<BookmarkModel> = withContext(Dispatchers.IO) {
        Result.runCatching {
            val url = "$baseUrl/$token/bookmarks"
            logger.debug("Fetching bookmarks from: $url")

            val request = Request(
                url = url,
                method = Request.Method.GET,
                conservative = true,
            )

            val response = client.fetch(request)
            
            if (!response.isSuccess) {
                logger.debug("Failed to fetch bookmarks. Response code: ${response.status}")
                response.close()
                return@runCatching emptyList<BookmarkModel>()
            }

            val bookmarks = response.body.useBufferedReader { reader ->
                val jsonResponse = reader.readText()
                logger.debug("Response received: $jsonResponse")

                // Parse JSON array
                val jsonArray = JSONArray(jsonResponse)
                val bookmarkList = mutableListOf<BookmarkModel>()

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val bookmark = BookmarkModel.fromJSONObject(jsonObject)
                    bookmarkList.add(bookmark)
                    
                    // Log each bookmark for debugging
                    logger.debug("Bookmark #${i + 1}:\n" +
                            "  ID: ${bookmark.id}\n" +
                            "  Title: ${bookmark.title}\n" +
                            "  URL: ${bookmark.url}\n" +
                            "  Color: ${bookmark.color}\n" +
                            "  Icon: ${bookmark.icon}\n")
                }

                bookmarkList
            }

            logger.debug("Successfully fetched ${bookmarks.size} bookmarks")
            bookmarks
        }.getOrElse { exception ->
            logger.debug("Exception while fetching bookmarks: ${exception.message}", exception)
            emptyList()
        }
    }
}
