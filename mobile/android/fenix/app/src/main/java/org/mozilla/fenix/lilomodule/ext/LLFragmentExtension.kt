package org.mozilla.fenix.lilomodule.ext

import android.view.WindowManager
import androidx.fragment.app.Fragment

fun Fragment.doNotResizeScreen() {
    requireActivity().window.setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING//.SOFT_INPUT_ADJUST_RESIZE,
    )
}
