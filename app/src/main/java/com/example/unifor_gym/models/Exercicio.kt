package com.example.unifor_gym.models

import android.os.Parcel
import android.os.Parcelable

data class Exercicio(
    val id: Int,
    val nome: String,
    val grupoMuscular: String, // "Peito", "Costas", "Pernas", etc.
    val dificuldade: String, // "Iniciante", "Intermediário", "Avançado"
    val categorias: List<String>, // Lista de categorias (Peito, Braços, etc.)
    val aparelhos: List<String>, // Lista de aparelhos necessários
    val instrucoes: String, // Instruções de execução
    val videoUrl: String? = null, // Link para vídeo demonstrativo (opcional)
    val equipamentosNecessarios: List<String> = emptyList() // Lista de equipamentos necessários
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.createStringArrayList() ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nome)
        parcel.writeString(grupoMuscular)
        parcel.writeString(dificuldade)
        parcel.writeStringList(categorias)
        parcel.writeStringList(aparelhos)
        parcel.writeString(instrucoes)
        parcel.writeString(videoUrl)
        parcel.writeStringList(equipamentosNecessarios)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Exercicio> {
        override fun createFromParcel(parcel: Parcel): Exercicio {
            return Exercicio(parcel)
        }

        override fun newArray(size: Int): Array<Exercicio?> {
            return arrayOfNulls(size)
        }
    }
}