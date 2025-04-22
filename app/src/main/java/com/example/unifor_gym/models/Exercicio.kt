package com.example.unifor_gym.models

import android.os.Parcel
import android.os.Parcelable

data class Exercicio(
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

    companion object CREATOR : Parcelable.Creator<Exercicio> {
        override fun createFromParcel(parcel: Parcel): Exercicio {
            return Exercicio(parcel)
        }

        override fun newArray(size: Int): Array<Exercicio?> {
            return arrayOfNulls(size)
        }
    }
}