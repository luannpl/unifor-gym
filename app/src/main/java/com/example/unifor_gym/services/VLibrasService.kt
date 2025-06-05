// app/src/main/java/com/example/unifor_gym/services/VLibrasService.kt
package com.example.unifor_gym.services

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.JavascriptInterface
import android.util.Log

class VLibrasService {

    companion object {
        private const val TAG = "VLibrasService"
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
            settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        // Add JavaScript interface for debugging
        webView.addJavascriptInterface(VLibrasJavaScriptInterface(), "AndroidInterface")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "Page finished loading: $url")
                // Inject VLibras widget after page loads
                injectVLibrasWidget(webView)
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.e(TAG, "WebView error: $description")
            }
        }
    }

    private fun injectVLibrasWidget(webView: WebView) {
        val vLibrasScript = """
            javascript:(function() {
                console.log('Starting VLibras injection...');
                AndroidInterface.log('Starting VLibras injection...');
                
                // Remove existing VLibras if present
                var existing = document.getElementById('vlibras-plugin');
                if (existing) {
                    existing.remove();
                    console.log('Removed existing VLibras widget');
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
                    try {
                        console.log('VLibras script loaded successfully');
                        AndroidInterface.log('VLibras script loaded successfully');
                        
                        // Initialize VLibras with updated API
                        if (typeof window.VLibras !== 'undefined') {
                            new window.VLibras.Widget('https://vlibras.gov.br/app');
                            console.log('VLibras widget created');
                            AndroidInterface.log('VLibras widget created');
                            
                            // Set ready flag after a delay
                            setTimeout(function() {
                                window.vLibrasReady = true;
                                window.vLibrasInstance = window.VLibras;
                                console.log('VLibras ready flag set');
                                AndroidInterface.log('VLibras ready flag set');
                                AndroidInterface.onVLibrasReady();
                            }, 3000);
                        } else {
                            console.error('VLibras not available after script load');
                            AndroidInterface.log('VLibras not available after script load');
                        }
                    } catch (error) {
                        console.error('Error creating VLibras widget:', error);
                        AndroidInterface.log('Error creating VLibras widget: ' + error.message);
                    }
                };
                
                script.onerror = function() {
                    console.error('Failed to load VLibras script');
                    AndroidInterface.log('Failed to load VLibras script');
                };
                
                document.head.appendChild(script);
                console.log('VLibras script added to head');
            })();
        """.trimIndent()

        webView.evaluateJavascript(vLibrasScript, null)
    }

    fun translateText(webView: WebView, text: String) {
        // Clean and prepare text for translation
        val cleanText = text.replace("'", "\\'").replace("\n", " ").take(500) // Limit text length

        val script = """
            javascript:(function() {
                console.log('Attempting to translate: $cleanText');
                AndroidInterface.log('Attempting to translate: $cleanText');
                
                function tryTranslate() {
                    try {
                        // Try the new VLibras API methods
                        if (window.vLibrasReady && window.vLibrasInstance) {
                            // Method 1: Try the Widget API
                            if (window.vLibrasInstance.Widget && window.vLibrasInstance.Widget.prototype.translate) {
                                window.vLibrasInstance.Widget.prototype.translate('$cleanText');
                                console.log('Translation sent via Widget.prototype.translate');
                                AndroidInterface.log('Translation sent via Widget.prototype.translate');
                                return true;
                            }
                            
                            // Method 2: Try direct API access
                            if (window.vLibrasInstance.Api && window.vLibrasInstance.Api.translate) {
                                window.vLibrasInstance.Api.translate('$cleanText');
                                console.log('Translation sent via Api.translate');
                                AndroidInterface.log('Translation sent via Api.translate');
                                return true;
                            }
                            
                            // Method 3: Try to find the translate function in the global scope
                            if (typeof window.VLibras !== 'undefined' && window.VLibras.Api) {
                                window.VLibras.Api.translate('$cleanText');
                                console.log('Translation sent via global VLibras.Api.translate');
                                AndroidInterface.log('Translation sent via global VLibras.Api.translate');
                                return true;
                            }
                        }
                        
                        // Method 4: Try to trigger translation by updating page content
                        if (window.vLibrasReady) {
                            var textElement = document.createElement('div');
                            textElement.textContent = '$cleanText';
                            textElement.style.position = 'absolute';
                            textElement.style.top = '10px';
                            textElement.style.left = '10px';
                            textElement.style.zIndex = '999999';
                            textElement.style.backgroundColor = '#fff';
                            textElement.style.border = '2px solid #1976D2';
                            textElement.style.padding = '10px';
                            textElement.style.borderRadius = '5px';
                            textElement.id = 'vlibras-text-target';
                            
                            // Remove existing text element
                            var existing = document.getElementById('vlibras-text-target');
                            if (existing) existing.remove();
                            
                            document.body.appendChild(textElement);
                            console.log('Text added to page for VLibras to detect');
                            AndroidInterface.log('Text added to page for VLibras to detect');
                            return true;
                        }
                        
                        return false;
                    } catch (error) {
                        console.error('Error in tryTranslate:', error);
                        AndroidInterface.log('Error in tryTranslate: ' + error.message);
                        return false;
                    }
                }
                
                // Try immediate translation
                if (!tryTranslate()) {
                    console.log('VLibras not ready, waiting...');
                    AndroidInterface.log('VLibras not ready, retrying in 2 seconds...');
                    
                    // Retry after delay
                    setTimeout(function() {
                        if (!tryTranslate()) {
                            console.log('Translation failed after retry');
                            AndroidInterface.log('Translation failed after retry - VLibras may not be fully initialized');
                        }
                    }, 2000);
                    
                    // Final retry after longer delay
                    setTimeout(function() {
                        if (!tryTranslate()) {
                            console.log('Final translation attempt failed');
                            AndroidInterface.log('Final translation attempt failed');
                        }
                    }, 5000);
                }
            })();
        """.trimIndent()

        webView.evaluateJavascript(script, null)
    }

    fun checkVLibrasStatus(webView: WebView) {
        val script = """
            javascript:(function() {
                var status = {
                    vLibrasExists: typeof window.VLibras !== 'undefined',
                    vLibrasReady: window.vLibrasReady || false,
                    apiExists: false,
                    widgetExists: false
                };
                
                if (window.VLibras) {
                    status.apiExists = typeof window.VLibras.Api !== 'undefined';
                    status.widgetExists = typeof window.VLibras.Widget !== 'undefined';
                }
                
                console.log('VLibras Status:', JSON.stringify(status, null, 2));
                AndroidInterface.log('VLibras Status: ' + JSON.stringify(status));
                
                // Try to list available methods
                if (window.VLibras) {
                    var methods = Object.keys(window.VLibras);
                    console.log('VLibras methods:', methods);
                    AndroidInterface.log('VLibras methods: ' + methods.join(', '));
                    
                    if (window.VLibras.Api) {
                        var apiMethods = Object.keys(window.VLibras.Api);
                        console.log('VLibras.Api methods:', apiMethods);
                        AndroidInterface.log('VLibras.Api methods: ' + apiMethods.join(', '));
                    }
                }
                
                return JSON.stringify(status);
            })();
        """.trimIndent()

        webView.evaluateJavascript(script) { result ->
            Log.d(TAG, "VLibras status result: $result")
        }
    }

    // JavaScript Interface for debugging
    class VLibrasJavaScriptInterface {
        @JavascriptInterface
        fun log(message: String) {
            Log.d(TAG, "JS Log: $message")
        }

        @JavascriptInterface
        fun onVLibrasReady() {
            Log.d(TAG, "VLibras is ready!")
        }
    }
}