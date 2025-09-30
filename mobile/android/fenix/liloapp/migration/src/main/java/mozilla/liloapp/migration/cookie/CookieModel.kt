package mozilla.liloapp.migration.cookie

import org.json.JSONObject

data class CookieModel(
    val url: String,
    val name: String,
    val value: String,
) {
    fun toJSONObject(): JSONObject {
        return JSONObject().apply {
            put("url", url)
            put("name", name)
            put("value", value)
        }
    }
}
