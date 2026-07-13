package com.undef.superahorroCalvoAlasino.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room para cachear productos buscados desde OpenFoodFacts.
 * Implementa el patrón "Single Source of Truth" donde Room es la única fuente de verdad.
 */
@Entity(tableName = "productos_buscados")
data class ProductoBuscadoEntity(
    @PrimaryKey
    val codigo: String,  // Código de barras (EAN)
    val nombre: String,
    val marca: String?,
    val imagenUrl: String?,
    val nutriScore: String?,
    val ingredientes: String?,
    val alergenos: String?,
    val energia: String?,
    val grasas: String?,
    val grasasSaturadas: String?,
    val carbohidratos: String?,
    val azucares: String?,
    val proteinas: String?,
    val sal: String?,
    val fibra: String?,
    val timestamp: Long  // Timestamp de cuando se guardó (para expiración de caché)
)

