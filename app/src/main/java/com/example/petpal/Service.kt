package com.example.petpal

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Service(
    val title: String,
    val description: String,
    val imageUrl: String? = null
) : Parcelable
