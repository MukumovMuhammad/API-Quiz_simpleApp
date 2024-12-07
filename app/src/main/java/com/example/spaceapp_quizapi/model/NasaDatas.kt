package com.example.spaceapp_quizapi.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.Serializable

@Serializable
data class Apod_data(
    val title: String,
    val date: String,
    val url: String,
    val explanation: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeString(title)
        p0.writeString(date)
        p0.writeString(url)
        p0.writeString(explanation)
    }

    companion object CREATOR : Parcelable.Creator<Apod_data> {
        override fun createFromParcel(parcel: Parcel): Apod_data {
            return Apod_data(parcel)
        }

        override fun newArray(size: Int): Array<Apod_data?> {
            return arrayOfNulls(size)
        }
    }
}
