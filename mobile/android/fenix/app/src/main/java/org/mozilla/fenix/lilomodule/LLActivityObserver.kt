package org.mozilla.fenix.lilomodule

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.os.Bundle
import com.google.android.gms.common.api.internal.ActivityLifecycleObserver
import mozilla.components.support.base.log.logger.Logger
import mozilla.liloapp.migration.LLFragmentObserver
import org.mozilla.fenix.HomeActivity

/**
 * Called when a activity is created.
 * This class is responsible for observing the lifecycle of HomeActivity and
 * registering the LLFragmentObserver.
 */
class LLActivityObserver : Application.ActivityLifecycleCallbacks {
    private val logger = Logger("LILO:Observer")

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity is HomeActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                LLFragmentObserver(),
                true
            )
        }
    }

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

}
