package com.example.unifor_gym.services

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.JavascriptInterface
import android.util.Log

class VLibrasService {

    companion object {
        private const val TAG = "VLibrasService"
    }

    // This method is now primarily for setting up the WebView, not for VLibras specific injection.
    fun setupVLibrasWebView(webView: WebView, context: Context) {
        webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.setSupportZoom(false)
            settings.builtInZoomControls = false
            settings.displayZoomControls = false
            settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        // Add JavaScript interface for debugging
        webView.addJavascriptInterface(VLibrasJavaScriptInterface(), "AndroidInterface")

        // WebViewClient is now handled in VLibrasActivity for better control over page loading events.
        // Keeping a basic one here for completeness if this service is used elsewhere.
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "Service WebViewClient: Page finished loading: $url")
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.e(TAG, "Service WebView Error: $description")
            }
        }
    }

    // The translation logic is now entirely handled within the JavaScript in VLibrasActivity's HTML.
    // This method is no longer needed as direct calls from Android to translate are replaced by JS.
    // Keeping it as a placeholder or if there's a future need for direct Android-triggered translation.
    fun translateText(webView: WebView, text: String) {
        Log.w(TAG, "translateText called in VLibrasService. This method is deprecated. Translation is handled in WebView's JavaScript.")
        val cleanText = text.replace("\\\\'", "\\\\\\\\").replace("\n", " ").take(500)
        val script = "javascript:attemptTranslation(\"$cleanText\");"
        webView.evaluateJavascript(script, null)
    }

    // The status check logic is now entirely handled within the JavaScript in VLibrasActivity's HTML.
    // This method is no longer needed as direct calls from Android to check status are replaced by JS.
    // Keeping it as a placeholder or if there's a future need for direct Android-triggered status check.
    fun checkVLibrasStatus(webView: WebView) {
        Log.w(TAG, "checkVLibrasStatus called in VLibrasService. This method is deprecated. Status check is handled in WebView's JavaScript.")
        val script = "javascript:checkVLibrasStatus();"
        webView.evaluateJavascript(script) { result ->
            Log.d(TAG, "VLibras status result from JS: $result")
        }
    }

    // JavaScript Interface for debugging and communication from WebView to Android
    class VLibrasJavaScriptInterface {
        @JavascriptInterface
        fun log(message: String) {
            Log.d(TAG, "JS Log: $message")
        }

        @JavascriptInterface
        fun onVLibrasReady() {
            Log.d(TAG, "VLibras is ready! (Callback from JS)")
        }
    }
}