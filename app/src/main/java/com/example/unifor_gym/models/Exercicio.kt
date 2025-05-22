package com.example.unifor_gym.models

import android.os.Parcel
import android.os.Parcelable

data class Exercicio(
    val id: Int = 0, // Manter para compatibilidade, mas usar firebaseId como principal
    var firebaseId: String? = null, // ID real do Firebase
    val nome: String = "",
    val grupoMuscular: String = "",
    val dificuldade: String = "",
    val categorias: List<String> = emptyList(),
    val aparelhos: List<String> = emptyList(),
    val instrucoes: String = "",
    val videoUrl: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: "",
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(firebaseId)
        parcel.writeString(nome)
        parcel.writeString(grupoMuscular)
        parcel.writeString(dificuldade)
        parcel.writeStringList(categorias)
        parcel.writeStringList(aparelhos)
        parcel.writeString(instrucoes)
        parcel.writeString(videoUrl)
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
