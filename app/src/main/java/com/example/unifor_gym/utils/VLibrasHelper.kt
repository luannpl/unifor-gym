package com.example.unifor_gym.utils

import android.content.Context
import android.content.Intent
import com.example.unifor_gym.activities.VLibrasActivity

object VLibrasHelper {

    fun openVLibras(context: Context, textToTranslate: String = "") {
        val intent = Intent(context, VLibrasActivity::class.java).apply {
            if (textToTranslate.isNotEmpty()) {
                putExtra("text_to_translate", textToTranslate)
            }
        }
        context.startActivity(intent)
    }

    fun getAccessibilityTexts(): Map<String, String> {
        return mapOf(
            "welcome" to "Bem-vindo ao Unifor Gym, seu aplicativo de academia",
            "exercises" to "Aqui você pode ver todos os exercícios disponíveis",
            "workouts" to "Esta é a seção de treinos personalizados",
            "classes" to "Veja as aulas disponíveis e faça sua inscrição",
            "notifications" to "Confira suas notificações e lembretes",
            "profile" to "Gerencie seu perfil e configurações",
            "chat" to "Converse com o assistente virtual para tirar dúvidas sobre exercícios"
        )
    }

    fun getExerciseInstructions(exerciseName: String): String {
        return when (exerciseName.lowercase()) {
            "supino reto" -> "Deite-se no banco, segure a barra na largura dos ombros, abaixe até o peito e empurre para cima"
            "agachamento" -> "Fique em pé, desça flexionando os joelhos mantendo as costas retas, depois suba"
            "flexão" -> "Apoie as mãos no chão, mantenha o corpo reto, desça e suba usando os braços"
            else -> "Instruções para o exercício: $exerciseName"
        }
    }
}