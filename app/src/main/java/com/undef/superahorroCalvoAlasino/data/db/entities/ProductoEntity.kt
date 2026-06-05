package com.undef.superahorroCalvoAlasino.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "productos",
    foreignKeys = [ForeignKey(
        entity = CompraEntity::class,
        parentColumns = ["id"],
        childColumns = ["compraId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("compraId")]
)
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val compraId: Int,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val cantidad: Int = 1
)
