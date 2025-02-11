package com.example.petpal

data class Service(
    val id: Int,
    val serviceName: String,
    val description: String,
    val price: Double,
    val status: String,
    val imageUrl: String? = null
)
