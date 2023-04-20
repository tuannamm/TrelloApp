package eu.tutorials.trelloapp.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
data class User (
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var image: String = "",
    var mobile: Long = 0,
    var fcmToken: String = "",
    var selected: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this (
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
        ) {}

    override fun describeContents() = 0

    companion object : Parceler<User> {

        override fun User.write(dest: Parcel, flags: Int) = with(dest) {
            writeString(id)
            writeString(name)
            writeString(email)
            writeString(image)
            writeLong(mobile)
            writeString(fcmToken)
        }

        override fun create(parcel: Parcel): User {
            return User(parcel)
        }
    }

}