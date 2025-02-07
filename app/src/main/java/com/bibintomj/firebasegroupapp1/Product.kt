package com.bibintomj.firebasegroupapp1

data class Product (
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val availableColors: List<String> = emptyList(),
    val specifications: String = "",
    val photos: List<String> = emptyList(),
    val price: Double = 0.0
)