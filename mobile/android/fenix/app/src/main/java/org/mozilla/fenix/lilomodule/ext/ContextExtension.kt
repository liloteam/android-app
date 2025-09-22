package org.mozilla.fenix.lilomodule.ext

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

fun Context.resolveLifecycleOwner(): LifecycleOwner? {
    return when (this) {
        is LifecycleOwner -> this
        is Application -> ProcessLifecycleOwner.get()
        is ContextWrapper -> this.baseContext.resolveLifecycleOwner()
        else -> null
    }
}
