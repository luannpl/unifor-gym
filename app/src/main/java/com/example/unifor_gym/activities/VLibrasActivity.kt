// app/src/main/java/com/example/unifor_gym/activities/VLibrasActivity.kt
package com.example.unifor_gym.activities

import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageButton
import android.widget.Toast
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.unifor_gym.R
import com.example.unifor_gym.services.VLibrasService

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

        if (textToTranslate.isNotEmpty()) {
            // Schedule translation attempts
            scheduleTranslation()
        }
    }

    private fun setupVLibras() {
        vLibrasService.setupVLibrasWebView(webView, this)

        // Load a comprehensive HTML page for VLibras
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
                        <h1><span class="emoji">🤟</span>VLibras - Unifor Gym</h1>
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
                
                <script>
                    let translationAttempts = 0;
                    const maxAttempts = 5;
                    
                    function updateStatus(message, isError = false) {
                        const statusText = document.getElementById('status-text');
                        statusText.textContent = message;
                        statusText.style.color = isError ? '#d32f2f' : '#1976d2';
                        console.log('Status: ' + message);
                    }
                    
                    function showTranslationSection(text) {
                        const section = document.getElementById('translate-section');
                        const textDiv = document.getElementById('translation-text');
                        textDiv.textContent = text;
                        section.style.display = 'block';
                    }
                    
                    function checkVLibrasStatus() {
                        const status = {
                            vLibrasExists: typeof window.VLibras !== 'undefined',
                            vLibrasReady: window.vLibrasReady || false,
                            apiExists: false,
                            widgetExists: false,
                            timestamp: new Date().toLocaleTimeString()
                        };
                        
                        if (window.VLibras) {
                            status.apiExists = typeof window.VLibras.Api !== 'undefined';
                            status.widgetExists = typeof window.VLibras.Widget !== 'undefined';
                        }
                        
                        updateStatus('VLibras: ' + (status.vLibrasExists ? 'Carregado' : 'Não carregado') + 
                                   ', Ready: ' + (status.vLibrasReady ? 'Sim' : 'Não') + 
                                   ', API: ' + (status.apiExists ? 'Sim' : 'Não'));
                        
                        console.log('VLibras Status Check:', JSON.stringify(status, null, 2));
                        return status;
                    }
                    
                    function retryTranslation() {
                        if (window.currentTextToTranslate) {
                            updateStatus('Tentando traduzir novamente...');
                            setTimeout(() => {
                                window.AndroidInterface?.log('Manual retry translation attempt');
                                attemptTranslation(window.currentTextToTranslate);
                            }, 1000);
                        }
                    }
                    
                    function attemptTranslation(text) {
                        translationAttempts++;
                        updateStatus('Tentativa de tradução ' + translationAttempts + '/' + maxAttempts + '...');
                        
                        try {
                            // Multiple translation strategies
                            let success = false;
                            
                            // Strategy 1: Direct API
                            if (window.VLibras && window.VLibras.Api && window.VLibras.Api.translate) {
                                window.VLibras.Api.translate(text);
                                updateStatus('Tradução enviada via VLibras.Api.translate');
                                success = true;
                            }
                            // Strategy 2: Widget instance
                            else if (window.vLibrasInstance && window.vLibrasInstance.translate) {
                                window.vLibrasInstance.translate(text);
                                updateStatus('Tradução enviada via widget instance');
                                success = true;
                            }
                            // Strategy 3: Add text to page for auto-detection
                            else if (window.vLibrasReady) {
                                showTranslationSection(text);
                                updateStatus('Texto adicionado à página para detecção automática');
                                success = true;
                            }
                            
                            if (success) {
                                document.getElementById('retry-btn').style.display = 'inline-block';
                            } else if (translationAttempts < maxAttempts) {
                                setTimeout(() => attemptTranslation(text), 2000);
                            } else {
                                updateStatus('Falha ao traduzir após múltiplas tentativas', true);
                                document.getElementById('retry-btn').style.display = 'inline-block';
                            }
                            
                        } catch (error) {
                            console.error('Translation error:', error);
                            updateStatus('Erro na tradução: ' + error.message, true);
                            if (translationAttempts < maxAttempts) {
                                setTimeout(() => attemptTranslation(text), 2000);
                            }
                        }
                    }
                    
                    // Initialize when DOM is ready
                    document.addEventListener('DOMContentLoaded', function() {
                        updateStatus('Página carregada, aguardando VLibras...');
                        
                        // Check VLibras status periodically
                        const statusCheckInterval = setInterval(() => {
                            if (window.vLibrasReady) {
                                updateStatus('VLibras pronto!');
                                clearInterval(statusCheckInterval);
                                
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
                                updateStatus('Timeout aguardando VLibras', true);
                            }
                        }, 30000);
                    });
                </script>
            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL("https://vlibras.gov.br", htmlContent, "text/html", "UTF-8", null)
    }

    private fun scheduleTranslation() {
        // Set text in JavaScript for later use
        val setTextScript = "javascript:window.currentTextToTranslate = '$textToTranslate';"
        webView.evaluateJavascript(setTextScript, null)

        // Schedule multiple translation attempts
        webView.postDelayed({
            Log.d(TAG, "First translation attempt")
            vLibrasService.translateText(webView, textToTranslate)
        }, 3000)

        webView.postDelayed({
            Log.d(TAG, "Second translation attempt")
            vLibrasService.translateText(webView, textToTranslate)
        }, 6000)

        webView.postDelayed({
            Log.d(TAG, "Third translation attempt")
            vLibrasService.translateText(webView, textToTranslate)
        }, 10000)

        // Status check
        webView.postDelayed({
            Log.d(TAG, "Status check")
            vLibrasService.checkVLibrasStatus(webView)
        }, 8000)
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