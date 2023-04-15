package eu.tutorials.trelloapp.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
data class Board(
    val name: String = "",
    val image: String = "",
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList()
): Parcelable {
    constructor(parcel: Parcel) : this (
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!
        ) {}

    companion object : Parceler<Board> {

        override fun Board.write(parcel: Parcel, flags: Int) = with(parcel) {
            parcel.writeString(name)
            parcel.writeString(image)
            parcel.writeString(createdBy)
            writeStringList(assignedTo)
        }

        override fun create(parcel: Parcel): Board {
            return Board(parcel)
        }
    }
}