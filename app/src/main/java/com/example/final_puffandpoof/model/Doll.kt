package com.example.final_puffandpoof.model

import android.os.Parcel
import android.os.Parcelable

data class Doll(
    val id: Int,
    val desc: String,
    val name: String,
    val size: String,
    val price: Int,
    val rating: Double,
    val imageLink: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),          // Read id
        parcel.readString() ?: "", // Read desc
        parcel.readString() ?: "", // Read name
        parcel.readString() ?: "", // Read size
        parcel.readInt(),          // Read price
        parcel.readDouble(),       // Read rating
        parcel.readString() ?: ""  // Read imageLink
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)        // Write id
        parcel.writeString(desc)   // Write desc
        parcel.writeString(name)   // Write name
        parcel.writeString(size)   // Write size
        parcel.writeInt(price)     // Write price
        parcel.writeDouble(rating) // Write rating
        parcel.writeString(imageLink) // Write imageLink
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Doll> {
        override fun createFromParcel(parcel: Parcel): Doll {
            return Doll(parcel)
        }

        override fun newArray(size: Int): Array<Doll?> {
            return arrayOfNulls(size)
        }
    }
}
