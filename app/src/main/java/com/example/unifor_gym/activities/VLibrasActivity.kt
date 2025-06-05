package com.example.unifor_gym.activities

import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.unifor_gym.R
import com.example.unifor_gym.services.VLibrasService

class VLibrasActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var vLibrasService: VLibrasService
    private lateinit var btnBack: ImageButton

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
        val textToTranslate = intent.getStringExtra("text_to_translate") ?: ""
        if (textToTranslate.isNotEmpty()) {
            // Wait longer for VLibras to load and show the text in the webpage
            webView.postDelayed({
                // First, update the content div with the text to translate
                val updateContentScript = """
                    javascript:(function() {
                        var contentDiv = document.getElementById('content');
                        if (contentDiv) {
                            contentDiv.innerHTML = '<h2>Traduzindo:</h2><p>$textToTranslate</p>';
                        }
                    })();
                """.trimIndent()
                webView.evaluateJavascript(updateContentScript, null)

                // Then translate it
                vLibrasService.translateText(webView, textToTranslate)
            }, 5000) // Increased delay to 5 seconds
        }
    }

    private fun setupVLibras() {
        vLibrasService.setupVLibrasWebView(webView, this)

        // Load a simple HTML page that will host VLibras
        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>VLibras - Unifor Gym</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        padding: 20px;
                        background-color: #f5f5f5;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        background: white;
                        padding: 20px;
                        border-radius: 10px;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                    }
                    .header {
                        text-align: center;
                        color: #1976D2;
                        margin-bottom: 20px;
                    }
                    .content {
                        line-height: 1.6;
                        color: #333;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🤟 VLibras - Unifor Gym</h1>
                        <p>Tradução para Língua Brasileira de Sinais</p>
                    </div>
                    <div class="content" id="content">
                        <h2>Bem-vindo ao Unifor Gym!</h2>
                        <p>Esta é uma ferramenta de acessibilidade que traduz texto para LIBRAS (Língua Brasileira de Sinais).</p>
                        <p>O ícone do VLibras aparecerá no canto inferior direito da tela. Clique nele para ativar a tradução.</p>
                        <p>Use esta ferramenta para:</p>
                        <ul>
                            <li>Traduzir instruções de exercícios</li>
                            <li>Entender informações sobre aulas</li>
                            <li>Navegar pelo aplicativo com mais facilidade</li>
                            <li>Acessar conteúdo em LIBRAS</li>
                        </ul>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}