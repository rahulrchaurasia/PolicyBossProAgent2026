package com.policyboss.policybosspro.feature.dashboard.ui.components

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.policyboss.policybosspro.feature.dashboard.model.DashboardDummyData
import com.policyboss.policybosspro.feature.dashboard.model.EarningsChartPoint
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ChartPointDto(
    val month: String,
    val earnings: Float
)

class ChartDataInterface(var json: String) {
    @JavascriptInterface
    fun getChartData(): String = json
}

/**
 * Builds a validated local asset URL for the earnings chart supporting optional query parameters.
 */
private fun buildSafeChartAssetUrl(
    currency: String = "INR",
    range: String = "monthly",
    year: Int = 2026
): String {
    val safeCurrency = when (currency.uppercase()) {
        "INR", "USD", "EUR", "GBP" -> currency.uppercase()
        else -> "INR"
    }
    val safeRange = when (range.lowercase()) {
        "monthly", "weekly", "yearly" -> range.lowercase()
        else -> "monthly"
    }
    val safeYear = if (year in 2020..2030) year else 2026
    return "file:///android_asset/earnings_chart.html?currency=$safeCurrency&range=$safeRange&year=$safeYear"
}

@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun EarningsWebViewChart(
    points: List<EarningsChartPoint>,
    modifier: Modifier = Modifier,
    currency: String = "INR",
    range: String = "monthly",
    year: Int = 2026
) {
    // Guaranteed fallback data if points is empty
    val effectivePoints = if (points.isNotEmpty()) points else DashboardDummyData.earningsChart
    val dtoList = effectivePoints.map { ChartPointDto(it.month, it.earnings) }
    val currentJson = remember(effectivePoints) { Json.encodeToString(dtoList) }

    val chartInterface = remember { ChartDataInterface(currentJson) }
    var isPageLoaded by remember { mutableStateOf(false) }

    val safeUrl = remember(currency, range, year) {
        buildSafeChartAssetUrl(currency, range, year)
    }

    // Helper to safely push updated data to the WebView JavaScript engine
    fun updateWebViewData(webView: WebView, jsonData: String) {
        val safeJson = jsonData.replace("\\", "\\\\").replace("'", "\\'")
        val optionsJson = """{"currency":"₹","range":"$range"}""".replace("\\", "\\\\").replace("'", "\\'")
        val script = "if (window.updateChartData) { window.updateChartData('$safeJson', '$optionsJson'); }"
        webView.evaluateJavascript(script, null)
    }

    AndroidView(
        factory = { ctx ->
            object : WebView(ctx) {
                override fun onTouchEvent(event: android.view.MotionEvent?): Boolean {
                    return false
                }
            }.apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = true
                    allowContentAccess = false
                    loadsImagesAutomatically = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                }

                setBackgroundColor(Color.WHITE)
                setLayerType(View.LAYER_TYPE_HARDWARE, null)
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                overScrollMode = View.OVER_SCROLL_NEVER

                addJavascriptInterface(chartInterface, "Android")

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        isPageLoaded = true
                        view?.let { updateWebViewData(it, currentJson) }
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                        Log.w("EarningsChart", "WebView error: ${error?.description}")
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                        if (consoleMessage?.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
                            Log.e("EarningsChartJS", "${consoleMessage.message()} (${consoleMessage.sourceId()}:${consoleMessage.lineNumber()})")
                        }
                        return true
                    }
                }

                loadUrl(safeUrl)
            }
        },
        update = { webView ->
            chartInterface.json = currentJson
            if (isPageLoaded) {
                updateWebViewData(webView, currentJson)
            }
        },
        onReset = { webView ->
            webView.stopLoading()
        },
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
    )
}
