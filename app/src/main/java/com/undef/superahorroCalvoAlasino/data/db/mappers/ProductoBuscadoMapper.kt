package com.undef.superahorroCalvoAlasino.data.db.mappers

import com.undef.superahorroCalvoAlasino.data.db.entities.ProductoBuscadoEntity
import com.undef.superahorroCalvoAlasino.data.network.dto.NutrimentsDTO
import com.undef.superahorroCalvoAlasino.data.network.dto.ProductoDTO

/**
 * Convierte ProductoDTO (de la API) a ProductoBuscadoEntity (para Room).
 */
fun ProductoDTO.toEntity(): ProductoBuscadoEntity? {
    // Si no tiene código, no se puede guardar (es la primary key)
    if (codigo.isNullOrBlank()) return null

    return ProductoBuscadoEntity(
        codigo = codigo,
        nombre = nombre ?: "Sin nombre",
        marca = marca,
        imagenUrl = imagenUrl,
        nutriScore = nutriScore,
        ingredientes = ingredientes,
        alergenos = alergenos,
        energia = nutriments?.caloriasKcal?.toString(),
        grasas = nutriments?.grasas100g?.toString(),
        grasasSaturadas = null, // No disponible en NutrimentsDTO actual
        carbohidratos = nutriments?.carbohidratos100g?.toString(),
        azucares = nutriments?.azucares100g?.toString(),
        proteinas = nutriments?.proteinas100g?.toString(),
        sal = nutriments?.sal100g?.toString(),
        fibra = nutriments?.fibra100g?.toString(),
        timestamp = System.currentTimeMillis()
    )
}

/**
 * Convierte ProductoBuscadoEntity (de Room) a ProductoDTO (para la UI).
 */
fun ProductoBuscadoEntity.toDTO(): ProductoDTO {
    return ProductoDTO(
        codigo = codigo,
        nombre = nombre,
        marca = marca,
        cantidad = null,
        imagenUrl = imagenUrl,
        nutriments = NutrimentsDTO(
            caloriasKcal = energia?.toDoubleOrNull(),
            proteinas100g = proteinas?.toDoubleOrNull(),
            grasas100g = grasas?.toDoubleOrNull(),
            carbohidratos100g = carbohidratos?.toDoubleOrNull(),
            azucares100g = azucares?.toDoubleOrNull(),
            fibra100g = fibra?.toDoubleOrNull(),
            sal100g = sal?.toDoubleOrNull(),
            sodio100g = null
        ),
        nutriScore = nutriScore,
        ingredientes = ingredientes,
        alergenos = alergenos,
        categorias = null
    )
}

/**
 * Convierte una lista de ProductoDTO a lista de ProductoBuscadoEntity.
 * Filtra productos sin código (no se pueden guardar).
 */
fun List<ProductoDTO>.toEntityList(): List<ProductoBuscadoEntity> {
    return this.mapNotNull { it.toEntity() }
}

/**
 * Convierte una lista de ProductoBuscadoEntity a lista de ProductoDTO.
 */
fun List<ProductoBuscadoEntity>.toDTOList(): List<ProductoDTO> {
    return this.map { it.toDTO() }
}

