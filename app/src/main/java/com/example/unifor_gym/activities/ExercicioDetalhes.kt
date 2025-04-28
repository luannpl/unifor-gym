package com.example.unifor_gym.activities

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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

        // Limpar containers
        containerEquipamentos.removeAllViews()
        containerCategorias.removeAllViews()

        // Adicionar equipamentos
        for (equipamento in exercicio.aparelhos) {
            val txtEquipamento = TextView(this)
            txtEquipamento.text = "• $equipamento"
            txtEquipamento.textSize = 16f
            containerEquipamentos.addView(txtEquipamento)
        }

        // Adicionar categorias
        for (categoria in exercicio.categorias) {
            val txtCategoria = TextView(this)
            txtCategoria.text = "• $categoria"
            txtCategoria.textSize = 16f
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