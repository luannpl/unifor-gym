package com.example.unifor_gym.activities

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.unifor_gym.R
import com.example.unifor_gym.models.Exercicio
import com.example.unifor_gym.utils.VLibrasHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.regex.Pattern

class ExercicioDetalhes : AppCompatActivity() {

    private lateinit var btnVoltar: CardView
    private lateinit var txtTitulo: TextView
    private lateinit var txtDificuldade: TextView
    private lateinit var txtInstrucoes: TextView
    private lateinit var containerEquipamentos: LinearLayout
    private lateinit var containerCategorias: LinearLayout
    private lateinit var webViewPlayer: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercicio_detalhes)

        // Inicializar views
        btnVoltar = findViewById(R.id.btnVoltarExercicioDetalhes)
        txtTitulo = findViewById(R.id.txtTituloExercicioDetalhes)
        txtDificuldade = findViewById(R.id.txtDificuldadeExercicioDetalhes)
        txtInstrucoes = findViewById(R.id.txtInstrucoesExercicioDetalhes)
        containerEquipamentos = findViewById(R.id.containerEquipamentosDetalhes)
        containerCategorias = findViewById(R.id.containerCategoriasDetalhes)
        // Pegando o WebView pelo ID
        webViewPlayer = findViewById(R.id.webview_player)

        btnVoltar.setOnClickListener {
            finish()
        }

        val fabAccess = findViewById<FloatingActionButton>(R.id.fab_access)
        fabAccess.setOnClickListener {
            VLibrasHelper.openVLibrasWithScreenContent(this)
        }

        val exercicio = intent.getParcelableExtra<Exercicio>("exercicio")

        if (exercicio != null) {
            preencherDetalhesExercicio(exercicio)
        } else {
            txtTitulo.text = "Erro ao carregar exercício"
        }
    }

    private fun preencherDetalhesExercicio(exercicio: Exercicio) {
        // Preencher título e informações básicas
        txtTitulo.text = exercicio.nome
        txtDificuldade.text = exercicio.dificuldade
        txtInstrucoes.text = exercicio.instrucoes

        // Adicionar equipamentos
        for (equipamento in exercicio.aparelhos) {
            val txtEquipamento = TextView(this)
            txtEquipamento.text = "• $equipamento"
            txtEquipamento.textSize = 16f
            containerEquipamentos.addView(txtEquipamento)
        }

        // Adicionar categorias
        for (categoria in exercicio.categorias) {
            val txtCategoria = TextView(this).apply {
                text = categoria
                textSize = 14f
                setTextColor(ContextCompat.getColor(context, R.color.white))
                background = ContextCompat.getDrawable(context, R.drawable.rounded_blue_bg)

                // Padding (em dp convertido pra px)
                val horizontalPadding = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics
                ).toInt()
                val verticalPadding = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics
                ).toInt()
                setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)

                // Margin end (se estiver usando LinearLayout ou FlexboxLayout)
                val layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics
                    ).toInt()
                }
                this.layoutParams = layoutParams
            }
            containerCategorias.addView(txtCategoria)
        }


        // Se não houver equipamentos ou categorias, exibir mensagem
        if (exercicio.aparelhos.isEmpty()) {
            val txtSemEquipamentos = TextView(this)
            txtSemEquipamentos.text = "Não há equipamentos necessários"
            txtSemEquipamentos.textSize = 16f
            containerEquipamentos.addView(txtSemEquipamentos)
        }

        if (exercicio.categorias.isEmpty()) {
            val txtSemCategorias = TextView(this)
            txtSemCategorias.text = "Não há categorias definidas"
            txtSemCategorias.textSize = 16f
            containerCategorias.addView(txtSemCategorias)
        }

        val urlDoVideo = exercicio.videoUrl
        Log.d("ExercicioDetalhes", "Video URL: ${exercicio.videoUrl}")
        if (!urlDoVideo.isNullOrBlank()) {
            val videoId = getYouTubeId(urlDoVideo)
            Log.d("ExercicioDetalhes", "videoId: ${videoId}")
            if (videoId != null) {
                webViewPlayer.settings.javaScriptEnabled = true
                val html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <style>
                            body { margin: 0; padding: 0; background-color: black; }
                            .video-container { position: absolute; top: 0; left: 0; width: 100%; height: 100%; }
                        </style>
                    </head>
                    <body>
                        <div class="video-container">
                            <iframe 
                                width="100%" 
                                height="100%" 
                                src="https://www.youtube.com/embed/$videoId" 
                                frameborder="0" 
                                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                                allowfullscreen>
                            </iframe>
                        </div>
                    </body>
                    </html>
                """.trimIndent()
                // Carregar o HTML no WebView
                webViewPlayer.loadData(html, "text/html", "utf-8")
            }
        }
    }

    private fun getYouTubeId(youTubeUrl: String?): String? {
        if (youTubeUrl.isNullOrBlank()) {
            return null
        }
        Log.d("ExerciciosDetalhes::getYoutubeId", "youtubeUrl: ${youTubeUrl}")

        // Padrão de Regex ATUALIZADO para incluir "shorts/" e ser mais robusto.
        val pattern = "(?:youtu\\.be/|watch\\?v=|/videos/|embed\\/|/v/|/e/|shorts/|watch\\?.+&v=)([^&?/\"]{11})"

        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(youTubeUrl)

        return if (matcher.find()) {
            matcher.group(1) // Pega o primeiro grupo de captura, que é o nosso ID
        } else {
            null
        }
    }

    override fun onDestroy() {
        // É importante destruir o WebView para liberar memória
        webViewPlayer.destroy()
        super.onDestroy()
    }
}