package org.mozilla.fenix.lilomodule.search.toolbar

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.RelativeLayout.GONE
import androidx.core.content.ContextCompat
import org.mozilla.fenix.databinding.SearchSelectorBinding
import org.mozilla.fenix.search.toolbar.SearchSelector

class LLSearchSelector @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : RelativeLayout(context, attrs, defStyle) {

    private val binding = SearchSelectorBinding.inflate(LayoutInflater.from(context), this)

    fun setIcon(icon: Drawable?, contentDescription: String?) {
        binding.icon.setImageDrawable(icon)
        binding.icon.contentDescription = contentDescription
    }

    fun customizeForLilo() {
        isClickable = false
        binding.arrow.visibility = GONE
        binding.searchSelector.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
    }

}
