package org.mozilla.fenix.lilomodule.onboarding

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import org.mozilla.fenix.UpdateOnboardingPageType
import org.mozilla.fenix.ext.hideToolbar
import org.mozilla.fenix.ext.isLargeScreenSize
import org.mozilla.fenix.theme.FirefoxTheme
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import org.mozilla.fenix.utils.isLargeScreenSize

/**
 * DialogFragment displaying the update onboarding flow (2 screens).
 * This onboarding is shown only when the app is updated from a previous version.
 */
class UpdateOnboardingDialogFragment : DialogFragment() {

    private var orientationEventListener: OrientationEventListener? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_NoTitleBar_Fullscreen)
        
        // Force portrait orientation immediately to prevent rotation animation
        if (!isLargeScreenSize()) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Force portrait orientation before dialog is shown to prevent rotation animation
        if (!isLargeScreenSize()) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        
        return super.onCreateDialog(savedInstanceState).apply {
            window?.apply {
                setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                )
                // Make dialog fullscreen - extend behind status bar
                addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
                // Make dialog fullscreen and hide status bar
                WindowCompat.setDecorFitsSystemWindows(this, false)
                WindowInsetsControllerCompat(this, decorView).apply {
                    hide(WindowInsetsCompat.Type.statusBars())
                    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setContent {
            FirefoxTheme {
                ScreenContent()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Force portrait orientation to prevent any rotation
        if (!isLargeScreenSize()) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        // Hide status bar on both activity and dialog windows
        setStatusBarOnActivity(hidden = true)
        
        // Start orientation listener to detect rotation changes in real-time
        startOrientationListener()
    }

    override fun onResume() {
        super.onResume()
        hideToolbar()
        // Ensure status bar stays hidden
        setStatusBarOnActivity(hidden = true)
    }

    override fun onStop() {
        super.onStop()
        stopOrientationListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopOrientationListener()
        if (!isLargeScreenSize()) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        setStatusBarOnActivity(hidden = false)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    private fun ScreenContent() {
        UpdateOnboardingScreen(
            pages = listOf(
                UpdateOnboardingPageType.CONFIDENTIALITY,
                UpdateOnboardingPageType.NAVIGATION
            ),
            onFinish = {
                onFinish()
            },
        )
    }

    private fun onFinish() {
        //requireComponents.updateOnboarding.finish()
        dismiss()
    }

    companion object {
        const val TAG = "UpdateOnboardingDialogFragment"

        fun show(fragmentManager: FragmentManager) {
            // Force portrait orientation before showing dialog to prevent rotation animation
            val activity = fragmentManager.fragments.firstOrNull()?.activity
                ?: (fragmentManager as? androidx.fragment.app.Fragment)?.activity
            if (activity != null && !activity.isLargeScreenSize()) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            
            val fragment = UpdateOnboardingDialogFragment()
            fragment.show(fragmentManager, TAG)
        }
    }

    private fun startOrientationListener() {
        if (isLargeScreenSize()) {
            return
        }
        
        val context = requireContext()
        orientationEventListener = object : OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {
            override fun onOrientationChanged(orientation: Int) {
                // Force portrait orientation immediately when device is rotated
                // This happens before onConfigurationChanged, preventing the rotation animation
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
        
        if (orientationEventListener?.canDetectOrientation() == true) {
            orientationEventListener?.enable()
        }
    }

    private fun stopOrientationListener() {
        orientationEventListener?.disable()
        orientationEventListener = null
    }

    private fun setStatusBarOnActivity(hidden: Boolean) {
        val window = activity?.window ?: return
        WindowCompat.setDecorFitsSystemWindows(window, !hidden)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            if (hidden) {
                hide(WindowInsetsCompat.Type.statusBars())
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            else {
                show(WindowInsetsCompat.Type.statusBars())
            }
        }

    }

}
