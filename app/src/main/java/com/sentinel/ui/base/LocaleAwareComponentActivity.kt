package com.sentinel.ui.base

import android.content.Context
import androidx.activity.ComponentActivity
import com.sentinel.util.LocaleManager

/**
 * Base activity that applies the user-selected locale before resources load.
 * Uses ComponentActivity (compatible with Material Compose theme).
 */
open class LocaleAwareComponentActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.wrap(newBase))
    }
}
