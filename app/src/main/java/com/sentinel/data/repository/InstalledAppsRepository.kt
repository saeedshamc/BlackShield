package com.sentinel.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.sentinel.domain.model.InstalledAppInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstalledAppsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getLaunchableApps(): List<InstalledAppInfo> {
        val pm = context.packageManager
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val activities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(launcherIntent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(launcherIntent, 0)
        }
        return activities
            .map { resolve ->
                InstalledAppInfo(
                    packageName = resolve.activityInfo.packageName,
                    appName = resolve.loadLabel(pm).toString()
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.appName.lowercase() }
    }
}
