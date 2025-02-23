package com.example.petpal.data

data class CartItem(
    val id: Int,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String,
    val manufacturer: String = "",       // Add this
    var isChecked: Boolean = false       // For checkbox state
)
