package com.undef.superahorroCalvoAlasino.data.network.dto

import com.google.gson.annotations.SerializedName

data class NutrimentsDTO(
    @SerializedName("energy-kcal_100g") val caloriasKcal: Double? = null,
    @SerializedName("proteins_100g") val proteinas100g: Double? = null,
    @SerializedName("fat_100g") val grasas100g: Double? = null,
    @SerializedName("carbohydrates_100g") val carbohidratos100g: Double? = null,
    @SerializedName("sugars_100g") val azucares100g: Double? = null,
    @SerializedName("fiber_100g") val fibra100g: Double? = null,
    @SerializedName("salt_100g") val sal100g: Double? = null,
    @SerializedName("sodium_100g") val sodio100g: Double? = null
)
