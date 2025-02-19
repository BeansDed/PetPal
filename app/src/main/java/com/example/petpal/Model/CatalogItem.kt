package com.example.petpal

data class CatalogItem(
    val id: Int,
    val name: String,
    val price: String,
    val description: String,
    var quantity: Int,
    val imageUrl: String
)
