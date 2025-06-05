package com.example.unifor_gym.services

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class VLibrasService {

    companion object {
        private const val VLIBRAS_URL = "https://vlibras.gov.br/app/"
        private const val VLIBRAS_WIDGET_URL = "https://vlibras.gov.br/app/vlibras-plugin.js"
    }

    fun setupVLibrasWebView(webView: WebView, context: Context) {
        webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.setSupportZoom(false)
            settings.builtInZoomControls = false
            settings.displayZoomControls = false
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Inject VLibras widget
                injectVLibrasWidget(webView)
            }
        }
    }

    private fun injectVLibrasWidget(webView: WebView) {
        val vLibrasScript = """
            javascript:(function() {
                // Remove existing VLibras if present
                var existing = document.getElementById('vlibras-plugin');
                if (existing) {
                    existing.remove();
                }
                
                // Create VLibras container
                var vp = document.createElement('div');
                vp.id = 'vlibras-plugin';
                vp.style.position = 'fixed';
                vp.style.bottom = '20px';
                vp.style.right = '20px';
                vp.style.zIndex = '9999999';
                vp.style.width = '100px';
                vp.style.height = '100px';
                document.body.appendChild(vp);
                
                // Load VLibras script
                var script = document.createElement('script');
                script.src = 'https://vlibras.gov.br/app/vlibras-plugin.js';
                script.onload = function() {
                    new window.VLibras.Widget('https://vlibras.gov.br/app');
                };
                document.head.appendChild(script);
            })();
        """.trimIndent()

        webView.evaluateJavascript(vLibrasScript, null)
    }

    fun translateText(webView: WebView, text: String) {
        val script = """
            javascript:(function() {
                if (window.VLibras && window.VLibras.Api) {
                    window.VLibras.Api.translate('$text');
                } else {
                    console.log('VLibras not loaded yet');
                }
            })();
        """.trimIndent()

        webView.evaluateJavascript(script, null)
    }
}