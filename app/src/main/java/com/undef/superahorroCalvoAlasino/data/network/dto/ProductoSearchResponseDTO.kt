package com.undef.superahorroCalvoAlasino.data.network.dto

import com.google.gson.annotations.SerializedName

data class ProductoSearchResponseDTO(
    @SerializedName("products") val products: List<ProductoDTO> = emptyList(),
    @SerializedName("count") val count: Int = 0
)
