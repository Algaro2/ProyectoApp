package com.agr.proyectoapp.model

data class Product(
    val name: String = "",
    val category: String = "",
    val ingredients: List<String> = emptyList(),
)


