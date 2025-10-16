package mozilla.liloapp.migration.bookmark

import org.json.JSONObject

/**
 * Data model representing a bookmark from the Lilo API
 */
data class BookmarkModel(
    val id: String,
    val color: String,
    val title: String,
    val url: String,
    val icon: String
) {
    fun toJSONObject(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("color", color)
            put("title", title)
            put("url", url)
            put("icon", icon)
        }
    }

    companion object {
        /**
         * Create a BookmarkModel from a JSONObject
         */
        fun fromJSONObject(json: JSONObject): BookmarkModel {
            return BookmarkModel(
                id = json.getString("id"),
                color = json.getString("color"),
                title = json.getString("title"),
                url = json.getString("url"),
                icon = json.getString("icon")
            )
        }
    }
}

