package com.undef.superahorroCalvoAlasino.data.network.dto

import com.google.gson.annotations.SerializedName

data class RegistroCompraRequestDTO(
    @SerializedName("usuario_email") val usuarioEmail: String,
    @SerializedName("supermercado") val supermercado: String,
    @SerializedName("total") val total: Double,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("cantidad_productos") val cantidadProductos: Int
)
