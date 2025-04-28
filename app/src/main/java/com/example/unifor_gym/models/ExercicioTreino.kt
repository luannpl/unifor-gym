package com.example.unifor_gym.models

import android.os.Parcel
import android.os.Parcelable

data class ExercicioTreino(
    val nome: String,
    val peso: String,
    val repeticoes: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nome)
        parcel.writeString(peso)
        parcel.writeString(repeticoes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ExercicioTreino> {
        override fun createFromParcel(parcel: Parcel): ExercicioTreino {
            return ExercicioTreino(parcel)
        }

        override fun newArray(size: Int): Array<ExercicioTreino?> {
            return arrayOfNulls(size)
        }
    }
}