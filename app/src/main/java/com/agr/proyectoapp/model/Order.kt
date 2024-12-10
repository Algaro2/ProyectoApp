package com.agr.proyectoapp.model

data class Order(
    val email: String = "",
    val productNumber: Int = 0,
    val productName: String = "",
    val observations: String = "",
    val address: String = ""
)
