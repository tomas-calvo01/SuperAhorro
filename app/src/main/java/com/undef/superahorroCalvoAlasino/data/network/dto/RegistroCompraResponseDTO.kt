package com.undef.superahorroCalvoAlasino.data.network.dto

import com.google.gson.annotations.SerializedName

data class RegistroCompraResponseDTO(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("createdAt") val creadoEn: String? = null
)
