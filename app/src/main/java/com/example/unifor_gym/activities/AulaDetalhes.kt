package com.example.unifor_gym.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.unifor_gym.R
import com.example.unifor_gym.fragments.Aula
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.TemporalAdjusters
import java.util.Locale

class AulaDetalhes : AppCompatActivity() {
    lateinit var btnVoltar: CardView
    lateinit var txtNome: TextView
    lateinit var txtProfessor: TextView
    lateinit var txtProfessorPhoto: TextView
    lateinit var txtDiaHora: TextView
    lateinit var txtAlunoVaga: TextView
    lateinit var txtEquipamentos: TextView
    lateinit var txtDuracao: TextView

    // Novas TextViews para as próximas datas e horas
    private lateinit var txtDataAula1: TextView
    private lateinit var txtHoraAula1: TextView
    private lateinit var txtDataAula2: TextView
    private lateinit var txtHoraAula2: TextView
    private lateinit var txtDataAula3: TextView
    private lateinit var txtHoraAula3: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aula_detalhes)
        btnVoltar = findViewById(R.id.btnVoltarAulaDetalhes)
        txtNome = findViewById(R.id.aulaDetalhesTitle)
        txtProfessor = findViewById(R.id.aulaDetalhesInstrutor)
        txtProfessorPhoto = findViewById(R.id.aulaDetalhesInstrutorPhoto)
        txtDiaHora = findViewById(R.id.aulaDetalhesDiaHora)
        txtAlunoVaga = findViewById(R.id.aulaDetalhesAlunoVaga)
        txtEquipamentos = findViewById(R.id.aulaDetalhesEquipamentos)
        txtDuracao = findViewById(R.id.aulaDetalhesDuracao)

        // Inicialize as novas TextViews
        txtDataAula1 = findViewById(R.id.txtDataAula1)
        txtHoraAula1 = findViewById(R.id.txtHoraAula1)
        txtDataAula2 = findViewById(R.id.txtDataAula2)
        txtHoraAula2 = findViewById(R.id.txtHoraAula2)
        txtDataAula3 = findViewById(R.id.txtDataAula3)
        txtHoraAula3 = findViewById(R.id.txtHoraAula3)
        btnVoltar.setOnClickListener {
            finish()
        }
        val fb = Firebase.firestore

        val aulaId = intent.getStringExtra("aulaId")
        Log.d("AulaDetalhes", "Aula:" + aulaId)

        if(aulaId.isNullOrEmpty()) {
            Toast.makeText(this, "Aula não encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val aula = fb.collection("Aulas").document(aulaId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val aula = document.toObject(Aula::class.java)
                    if (aula != null) {

                        txtNome.text = aula.nome
                        txtProfessor.text = aula.instrutor

                        val iniciaisProfessor = if (aula.instrutor.length >= 2) {
                            aula.instrutor.substring(0, 2).uppercase()
                        } else {
                            aula.instrutor.uppercase()
                        }

                        val equipamentoIds = aula.equipamentos

                        if (equipamentoIds.isNotEmpty()) {
                            fb.collection("equipamentos")
                                .whereIn(FieldPath.documentId(), equipamentoIds)
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    val nomesEquipamentos = querySnapshot.documents.mapNotNull { it.getString("nome") }
                                    txtEquipamentos.text = "Equipamentos: " + nomesEquipamentos.joinToString(", ")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("AulaDetalhes", "Erro ao buscar equipamentos", e)
                                    txtEquipamentos.text = "Equipamentos: Não disponível"
                                }
                        } else {
                            txtEquipamentos.text = "Equipamentos: Nenhum"
                        }
                        txtProfessorPhoto.text = iniciaisProfessor
                        txtDiaHora.text = "${aula.diaDaSemana} ${aula.horarioInicio} - ${aula.horarioFim}"
                        txtDuracao.text = "Duração: ${calcularDuracaoAula(aula.horarioInicio, aula.horarioFim)}"
                        txtAlunoVaga.text = "Alunos: ${aula.qtdMatriculados}/${aula.qtdVagas}"

                        val proximasAulasInfo = getProximosDiasDeAula(aula.diaDaSemana, aula.horarioInicio, 3)

                        if (proximasAulasInfo.isNotEmpty()) {
                            txtDataAula1.text = proximasAulasInfo[0].first
                            txtHoraAula1.text = aula.horarioInicio // A hora é fixa para todas as instâncias da aula
                        }
                        if (proximasAulasInfo.size > 1) {
                            txtDataAula2.text = proximasAulasInfo[1].first
                            txtHoraAula2.text = aula.horarioInicio
                        }
                        if (proximasAulasInfo.size > 2) {
                            txtDataAula3.text = proximasAulasInfo[2].first
                            txtHoraAula3.text = aula.horarioInicio
                        }
                    }
                } else {
                    Toast.makeText(this, "Aula não encontrada", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Log.e("AulaDetalhes", "Erro ao buscar aula", e)
                Toast.makeText(this, "Erro ao buscar aula", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun calcularDuracaoAula(horarioInicio: String, horarioFim: String): String {
        try {
            val inicio = LocalTime.parse(horarioInicio)
            val fim = LocalTime.parse(horarioFim)

            val duracao = Duration.between(inicio, fim)

            val horas = duracao.toHours()
            val minutos = duracao.toMinutes() % 60 // Pega os minutos restantes após as horas

            return when {
                horas > 0 && minutos > 0 -> "${horas}h ${minutos}m"
                horas > 0 -> "${horas}h"
                minutos > 0 -> "${minutos}m"
                else -> "0m" // Duração zero ou inválida
            }
        } catch (e: DateTimeParseException) {
            // Lidar com erro de formato de hora inválido
            Log.e("GestaoAulas", "Erro ao fazer parse das horas: ${e.message}")
            return "Duração inválida"
        } catch (e: Exception) {
            // Lidar com outros erros
            Log.e("GestaoAulas", "Erro ao calcular duração: ${e.message}")
            return "Duração inválida"
        }
    }

    private fun mapDiaDaSemanaToDayOfWeek(diaDaSemana: String): DayOfWeek? {
        return when (diaDaSemana.lowercase(Locale.ROOT)) {
            "segunda" -> DayOfWeek.MONDAY
            "terça" -> DayOfWeek.TUESDAY
            "quarta" -> DayOfWeek.WEDNESDAY
            "quinta" -> DayOfWeek.THURSDAY
            "sexta" -> DayOfWeek.FRIDAY
            "sábado", "sabado" -> DayOfWeek.SATURDAY
            "domingo" -> DayOfWeek.SUNDAY
            else -> null
        }
    }

    private fun getProximosDiasDeAula(diaDaSemanaAula: String, horarioInicioAula: String, numeroDeAulas: Int = 3): List<Pair<String, String>> {
        val proximoDiaEnum = mapDiaDaSemanaToDayOfWeek(diaDaSemanaAula)

        if (proximoDiaEnum == null) {
            return List(numeroDeAulas) { Pair("Dia inválido", "") }
        }

        val hoje = LocalDate.now()
        val horaAtual = LocalTime.now()
        val horarioAula = try {
            LocalTime.parse(horarioInicioAula)
        } catch (e: DateTimeParseException) {
            Log.e("AulaDetalhes", "Erro ao fazer parse do horarioInicioAula: ${e.message}")
            return List(numeroDeAulas) { Pair("Hora inválida", "") }
        }

        val formatter = DateTimeFormatter.ofPattern("dd 'de' MMM", Locale("pt", "BR"))
        val proximasAulasInfo = mutableListOf<Pair<String, String>>()

        var dataAulaAtual = hoje.with(TemporalAdjusters.nextOrSame(proximoDiaEnum))

        // Ajuste para garantir que a primeira aula seja de fato futura
        // Se a aula é hoje e o horário da aula já passou, a primeira aula é na próxima semana.
        if (hoje.isEqual(dataAulaAtual) && horaAtual.isAfter(horarioAula)) {
            dataAulaAtual = dataAulaAtual.plusWeeks(1)
        }

        for (i in 0 until numeroDeAulas) {
            proximasAulasInfo.add(Pair(dataAulaAtual.format(formatter), horarioInicioAula))
            dataAulaAtual = dataAulaAtual.plusWeeks(1) // Adiciona 1 semana para a próxima aula
        }

        return proximasAulasInfo
    }


}