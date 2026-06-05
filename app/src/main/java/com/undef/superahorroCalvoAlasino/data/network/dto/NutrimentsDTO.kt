package com.undef.superahorroCalvoAlasino.data.network.dto

import com.google.gson.annotations.SerializedName

data class NutrimentsDTO(
    @SerializedName("energy-kcal_100g") val caloriasKcal: Double? = null
)
