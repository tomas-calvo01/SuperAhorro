package com.undef.superahorroCalvoAlasino.data.network.dto

import com.google.gson.annotations.SerializedName

data class ProductoDTO(
    @SerializedName("code") val codigo: String? = null,
    @SerializedName("product_name") val nombre: String? = null,
    @SerializedName("brands") val marca: String? = null,
    @SerializedName("quantity") val cantidad: String? = null,
    @SerializedName("image_url") val imagenUrl: String? = null,
    @SerializedName("nutriments") val nutriments: NutrimentsDTO? = null
)
