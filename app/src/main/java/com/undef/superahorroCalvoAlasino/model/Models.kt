

package com.undef.superahorroCalvoAlasino.model

data class Compra(
    val id: Int,
    val supermercado: String,
    val fecha: String,
    val hora: String,
    val total: Double,
    val productos: List<Producto> = emptyList(),
    val usuarioEmail: String = "",
    val ticketImageUri: String? = null
)

data class Producto(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val cantidad: Int = 1
)

data class Usuario(
    val nombre: String,
    val email: String
)