package org.mozilla.fenix.lilomodule.search

import android.content.Context
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mozilla.components.browser.icons.IconRequest
import mozilla.components.browser.state.search.SearchEngine
import mozilla.components.browser.state.state.selectedOrDefaultSearchEngine
import mozilla.components.feature.search.ext.createSearchEngine
import mozilla.components.support.base.log.logger.Logger
import org.mozilla.fenix.LLAppConstants
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.lilomodule.ext.resolveLifecycleOwner

data class LLSearchEngine(
    val logger: Logger
) {
    val tag = "LLSearchEngine"

    fun setupLiloSearchEngine(context: Context) {
        context.resolveLifecycleOwner()?.lifecycleScope?.launch(Main) {
            // Wait for the search engines to be loaded before checking for the Lilo search engine.
            // The search engines are loaded the first time from the home fragment.
            delay(2000)

            val searchState = context.components.core.store.state.search

            // If the Lilo search engine is already selected, do nothing.
            searchState.selectedOrDefaultSearchEngine?.let {
                if (it.name == LLAppConstants.SearchEngine.NAME) {
                    logger.info("$tag: Lilo search engine already selected")
                    return@launch
                }
            }

            // If the Lilo search engine already exists, select it.
            searchState.customSearchEngines.find { it.name == LLAppConstants.SearchEngine.NAME }
                ?.let { engine ->
                    logger.info("$tag: Lilo search engine already exists, select it")
                    context.components.useCases.searchUseCases.selectSearchEngine(engine)
                    return@launch
                }

            logger.info("$tag: Lilo search engine has to be created and selected")
            // If the Lilo search engine doesn't exist, create it and select it.
            createLiloSearchEngine(context) { engine ->
                logger.info("$tag: Lilo search engine created with name: ${engine?.name}")
                engine?.also {
                    context.components.useCases.searchUseCases.addSearchEngine(it)
                    context.components.useCases.searchUseCases.selectSearchEngine(it)
                }
            }
        }
    }

    private fun createLiloSearchEngine(context: Context, block: (SearchEngine?) -> Unit) {
        val searchString = LLAppConstants.SearchEngine.RESULT
        context.resolveLifecycleOwner()?.lifecycleScope?.launch(Main) {
            val engine = createSearchEngine(
                name = LLAppConstants.SearchEngine.NAME,
                url = LLAppConstants.SearchEngine.RESULT.toSearchUrl(),
                icon = context.components.core.icons.loadIcon(IconRequest(searchString))
                    .await().bitmap,
                suggestUrl = LLAppConstants.SearchEngine.SUGGEST.toSearchUrl(),
                isGeneral = true
            )
            block(engine)
        }?: kotlin.run { logger.error("$tag: context is not lifecycle owner: $context") }
    }

    private fun String.toSearchUrl(): String {
        return replace("%s", "{searchTerms}")
    }

}
