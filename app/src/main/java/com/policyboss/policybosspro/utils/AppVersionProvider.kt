package com.policyboss.policybosspro.utils

import android.content.Context
import com.policyboss.policybosspro.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppVersionProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Returns the installed application version string (e.g. "1.0").
     */
    fun getAppVersion(): String {
        return try {
            BuildConfig.VERSION_NAME
        } catch (_: Throwable) {
            try {
                @Suppress("DEPRECATION")
                val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                pInfo.versionName ?: "1.0"
            } catch (_: Throwable) {
                "1.0"
            }
        }
    }
}

