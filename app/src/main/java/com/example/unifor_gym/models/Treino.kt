package com.example.unifor_gym.models

import android.os.Parcel
import android.os.Parcelable

data class Treino(
    val titulo: String,
    val exercicios: List<ExercicioTreino>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.createTypedArrayList(ExercicioTreino.CREATOR) ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(titulo)
        parcel.writeTypedList(exercicios)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Treino> {
        override fun createFromParcel(parcel: Parcel): Treino {
            return Treino(parcel)
        }

        override fun newArray(size: Int): Array<Treino?> {
            return arrayOfNulls(size)
        }
    }
}