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
     * Returns the app version sent to the backend, e.g. "policyboss-1.5.6.1".
     */
    fun getAppVersion(): String = format(versionName())

    private fun versionName(): String {
        return try {
            BuildConfig.VERSION_NAME
        } catch (_: Throwable) {
            try {
                @Suppress("DEPRECATION")
                val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                pInfo.versionName ?: "1.0.0"
            } catch (_: Throwable) {
                "1.0.0"
            }
        }
    }

    companion object {
        private const val PREFIX = "policyboss-"

        /** `1.5.6.1` -> `policyboss-1.5.6.1`. */
        fun format(versionName: String): String = "$PREFIX$versionName"
    }
}

