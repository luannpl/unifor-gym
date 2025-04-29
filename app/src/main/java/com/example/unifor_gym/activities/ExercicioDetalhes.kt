package com.example.unifor_gym.activities

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.unifor_gym.R
import com.example.unifor_gym.models.Exercicio

class ExercicioDetalhes : AppCompatActivity() {

    private lateinit var btnVoltar: CardView
    private lateinit var txtTitulo: TextView
    private lateinit var txtDificuldade: TextView
    private lateinit var txtInstrucoes: TextView
    private lateinit var containerEquipamentos: LinearLayout
    private lateinit var containerCategorias: LinearLayout

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

        // Configurar botão de voltar
        btnVoltar.setOnClickListener {
            finish()
        }

        // Obter exercício dos extras
        val exercicio = intent.getParcelableExtra<Exercicio>("exercicio")

        if (exercicio != null) {
            preencherDetalhesExercicio(exercicio)
        } else {
            // Caso não tenha recebido os dados do exercício
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
    }
}