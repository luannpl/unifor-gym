package com.example.unifor_gym.activities

import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageButton
import android.widget.Toast
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.unifor_gym.R
import com.example.unifor_gym.services.VLibrasService
import android.webkit.WebChromeClient // Import WebChromeClient
import android.webkit.WebViewClient // Import WebViewClient

class VLibrasActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var vLibrasService: VLibrasService
    private lateinit var btnBack: ImageButton
    private var textToTranslate: String = ""

    companion object {
        private const val TAG = "VLibrasActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vlibras)

        webView = findViewById(R.id.webViewVLibras)
        btnBack = findViewById(R.id.btnBackVLibras)
        vLibrasService = VLibrasService()

        btnBack.setOnClickListener {
            finish()
        }

        setupVLibras()

        // Get text to translate from intent
        textToTranslate = intent.getStringExtra("text_to_translate") ?: ""
        Log.d(TAG, "Text to translate: $textToTranslate")

        // The JavaScript injection will happen after the page is loaded and VLibras is ready.
        // This ensures the WebView is fully initialized and the VLibras API is available.
    }

    private fun setupVLibras() {
        Log.d(TAG, "Setting up WebView for VLibras.")
        // Configure WebView settings
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
        webView.addJavascriptInterface(VLibrasService.VLibrasJavaScriptInterface(), "AndroidInterface")
        Log.d(TAG, "AndroidInterface added to WebView.")

        // Set a WebChromeClient to capture console messages from JavaScript
        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage?): Boolean {
                consoleMessage?.let {
                    Log.d(TAG, "WebView Console: ${it.message()} -- From line ${it.lineNumber()} of ${it.sourceId()}")
                }
                return super.onConsoleMessage(consoleMessage)
            }
        }
        Log.d(TAG, "WebChromeClient set for WebView.")

        // Set a WebViewClient to detect when the page has finished loading
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "WebView onPageFinished: $url")
                // Once the page is finished loading, trigger the JavaScript process
                // to check VLibras readiness and then attempt translation.
                if (textToTranslate.isNotEmpty()) {
                    val setTextAndTriggerScript = "javascript:window.currentTextToTranslate =$textToTranslate\"; startTranslationProcess();"
                    webView.evaluateJavascript(setTextAndTriggerScript, null)
                    Log.d(TAG, "Attempted to inject textToTranslate and trigger startTranslationProcess via evaluateJavascript.")
                }
            }

            override fun onReceivedError(view: WebView?, request: android.webkit.WebResourceRequest?, error: android.webkit.WebResourceError?) {
                super.onReceivedError(view, request, error)
                Log.e(TAG, "WebView Error: ${error?.description} on ${request?.url}")
            }
        }

        // Load a comprehensive HTML page for VLibras with embedded script
        val htmlContent = """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>VLibras - Unifor Gym</title>
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Arial, sans-serif;
                        padding: 20px;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        min-height: 100vh;
                        margin: 0;
                        color: #333;
                    }
                    
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        background: rgba(255, 255, 255, 0.95);
                        padding: 30px;
                        border-radius: 20px;
                        box-shadow: 0 10px 30px rgba(0,0,0,0.2);
                        backdrop-filter: blur(10px);
                    }
                    
                    .header {
                        text-align: center;
                        margin-bottom: 30px;
                    }
                    
                    .header h1 {
                        color: #1976D2;
                        margin-bottom: 10px;
                        font-size: 2.2em;
                    }
                    
                    .header .subtitle {
                        color: #666;
                        font-size: 1.1em;
                        font-weight: 300;
                    }
                    
                    .content {
                        line-height: 1.8;
                        color: #444;
                    }
                    
                    .text-to-translate {
                        background: #f8f9fa;
                        border-left: 4px solid #1976D2;
                        padding: 20px;
                        margin: 20px 0;
                        border-radius: 8px;
                        font-size: 1.1em;
                        line-height: 1.6;
                    }
                    
                    .instructions {
                        background: #e3f2fd;
                        padding: 20px;
                        border-radius: 10px;
                        margin: 20px 0;
                    }
                    
                    .status {
                        padding: 15px;
                        margin: 20px 0;
                        border-radius: 8px;
                        border: 1px solid #ddd;
                        background: #fff;
                    }
                    
                    .btn {
                        background: #1976D2;
                        color: white;
                        border: none;
                        padding: 12px 24px;
                        border-radius: 6px;
                        cursor: pointer;
                        font-size: 16px;
                        margin: 10px;
                        transition: background 0.3s;
                    }
                    
                    .btn:hover {
                        background: #1565C0;
                    }
                    
                    .emoji {
                        font-size: 1.5em;
                        margin-right: 10px;
                    }
                    
                    ul {
                        padding-left: 20px;
                    }
                    
                    li {
                        margin-bottom: 8px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>VLibras - Unifor Gym</h1>
                        <div class="subtitle">Tradução para Língua Brasileira de Sinais</div>
                    </div>
                    
                    <div class="content" id="content">
                        <div class="instructions">
                            <h3>📱 Como usar:</h3>
                            <ul>
                                <li>O ícone do VLibras aparecerá no canto inferior direito</li>
                                <li>Clique no ícone para ativar/desativar o tradutor</li>
                                <li>O texto será automaticamente traduzido para LIBRAS</li>
                                <li>Aguarde o carregamento completo da ferramenta</li>
                            </ul>
                        </div>
                        
                        <div id="translate-section" style="display: none;">
                            <h3>🔄 Conteúdo sendo traduzido:</h3>
                            <div class="text-to-translate" id="translation-text"></div>
                        </div>
                        
                        <div class="status" id="status">
                            <strong>Status:</strong> <span id="status-text">Carregando VLibras...</span>
                        </div>
                        
                        <div style="text-align: center; margin-top: 30px;">
                            <button class="btn" onclick="checkVLibrasStatus()">🔍 Verificar Status</button>
                            <button class="btn" onclick="retryTranslation()" id="retry-btn" style="display: none;">🔄 Tentar Novamente</button>
                        </div>
                    </div>
                </div>
                
                <div vw class="enabled">
                    <div vw-access-button class="active"></div>
                    <div vw-plugin-wrapper>
                        <div class="vw-plugin-top-wrapper"></div>
                    </div>
                </div>
                <script src="https://vlibras.gov.br/app/vlibras-plugin.js"></script>
                <script>
                    new window.VLibras.Widget("https://vlibras.gov.br/app");

                    let translationAttempts = 0;
                    const maxAttempts = 5;
                    
                    function updateStatus(message, isError = false) {
                        const statusText = document.getElementById("status-text");
                        statusText.textContent = message;
                        statusText.style.color = isError ? "#d32f2f" : "#1976d2";
                        console.log("Status: " + message);
                        AndroidInterface.log("JS Status: " + message);
                    }
                    
                    function showTranslationSection(text) {
                        const section = document.getElementById("translate-section");
                        const textDiv = document.getElementById("translation-text");
                        textDiv.textContent = text;
                        section.style.display = "block";
                    }
                    
                    function checkVLibrasStatus() {
                        const status = {
                            vLibrasExists: typeof window.VLibras !== "undefined",
                            vLibrasReady: window.vLibrasReady || false,
                            apiExists: false,
                            widgetExists: false,
                            timestamp: new Date().toLocaleTimeString()
                        };
                        
                        if (window.VLibras) {
                            status.apiExists = typeof window.VLibras.Api !== "undefined";
                            status.widgetExists = typeof window.VLibras.Widget !== "undefined";
                        }
                        
                        updateStatus("VLibras: " + (status.vLibrasExists ? "Carregado" : "Não carregado") + 
                                   ", Ready: " + (status.vLibrasReady ? "Sim" : "Não") + 
                                   ", API: " + (status.apiExists ? "Sim" : "Não"));
                        
                        console.log("VLibras Status Check:", JSON.stringify(status, null, 2));
                        AndroidInterface.log("JS VLibras Status Check: " + JSON.stringify(status));
                        return status;
                    }
                    
                    function retryTranslation() {
                        if (window.currentTextToTranslate) {
                            updateStatus("Tentando traduzir novamente...");
                            setTimeout(() => {
                                AndroidInterface.log("Manual retry translation attempt");
                                attemptTranslation(window.currentTextToTranslate);
                            }, 1000);
                        }
                    }
                    
                    function attemptTranslation(text) {
                        translationAttempts++;
                        updateStatus("Tentativa de tradução " + translationAttempts + "/" + maxAttempts + "...");
                        AndroidInterface.log("JS Attempting translation: " + text + " (Attempt " + translationAttempts + ")");
                        
                        try {
                            let success = false;
                            if (typeof window.VLibras !== "undefined" && typeof window.VLibras.Api !== "undefined" && typeof window.VLibras.Api.translate === "function") {
                                window.VLibras.Api.translate(text);
                                updateStatus("Tradução enviada via VLibras.Api.translate");
                                success = true;
                            } else {
                                console.log("VLibras API not ready yet. Retrying...");
                                AndroidInterface.log("JS VLibras API not ready yet. Retrying... typeof window.VLibras: " + (typeof window.VLibras) + ", typeof window.VLibras.Api: " + (typeof window.VLibras.Api) + ", typeof window.VLibras.Api.translate: " + (typeof window.VLibras.Api.translate));
                            }
                            
                            if (success) {
                                document.getElementById("retry-btn").style.display = "inline-block";
                            } else if (translationAttempts < maxAttempts) {
                                setTimeout(() => attemptTranslation(text), 2000);
                            } else {
                                updateStatus("Falha ao traduzir após múltiplas tentativas", true);
                                document.getElementById("retry-btn").style.display = "inline-block";
                            }
                            
                        } catch (error) {
                            console.error("Translation error:", error);
                            AndroidInterface.log("JS Translation error: " + error.message);
                            updateStatus("Erro na tradução: " + error.message, true);
                            if (translationAttempts < maxAttempts) {
                                setTimeout(() => attemptTranslation(text), 2000);
                            }
                        }
                    }
                    
                    function startTranslationProcess() {
                        updateStatus("Página carregada, aguardando VLibras...");
                        AndroidInterface.log("JS startTranslationProcess called");
                        
                        const statusCheckInterval = setInterval(() => {
                            AndroidInterface.log("JS Checking VLibras readiness... typeof window.VLibras: " + (typeof window.VLibras) + ", typeof window.VLibras.Api: " + (typeof window.VLibras.Api) + ", typeof window.VLibras.Api.translate: " + (typeof window.VLibras.Api.translate));
                            if (typeof window.VLibras !== "undefined" && typeof window.VLibras.Api !== "undefined" && typeof window.VLibras.Api.translate === "function") {
                                window.vLibrasReady = true; // Manually set vLibrasReady after VLibras.Api is available
                                updateStatus("VLibras pronto!");
                                clearInterval(statusCheckInterval);
                                AndroidInterface.log("JS VLibras is ready. Attempting translation.");
                                
                                // Start translation if text is available
                                if (window.currentTextToTranslate) {
                                    setTimeout(() => attemptTranslation(window.currentTextToTranslate), 1000);
                                }
                            }
                        }, 1000);
                        
                        // Stop checking after 30 seconds
                        setTimeout(() => {
                            clearInterval(statusCheckInterval);
                            if (!window.vLibrasReady) {
                                updateStatus("Timeout aguardando VLibras", true);
                                AndroidInterface.log("JS Timeout waiting for VLibras.");
                            }
                        }, 30000);
                    }

                    // Removed DOMContentLoaded listener as onPageFinished in Android handles it.
                </script>
            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL("https://vlibras.gov.br", htmlContent, "text/html", "UTF-8", null)
        Log.d(TAG, "HTML content loaded into WebView.")
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }
}