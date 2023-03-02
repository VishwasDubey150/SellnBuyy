package com.example.sellnbuy.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
    val id:String="",
    val Username:String="",
    val email:String="",
    val mobile: String ="",
    val image:String="",
    val gender:String="",
    val profileCompleted: Int=0):Parcelable