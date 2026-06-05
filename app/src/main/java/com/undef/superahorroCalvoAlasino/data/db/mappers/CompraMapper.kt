package com.undef.superahorroCalvoAlasino.data.db.mappers

import com.undef.superahorroCalvoAlasino.data.db.entities.CompraConProductos
import com.undef.superahorroCalvoAlasino.data.db.entities.CompraEntity
import com.undef.superahorroCalvoAlasino.data.db.entities.ProductoEntity
import com.undef.superahorroCalvoAlasino.model.Compra
import com.undef.superahorroCalvoAlasino.model.Producto

fun CompraConProductos.toModel(): Compra = Compra(
    id = compra.id,
    supermercado = compra.supermercado,
    fecha = compra.fecha,
    hora = compra.hora,
    total = compra.total,
    productos = productos.map { it.toModel() },
    usuarioEmail = compra.usuarioEmail
)

fun Compra.toEntity(): CompraEntity = CompraEntity(
    id = id,
    supermercado = supermercado,
    fecha = fecha,
    hora = hora,
    total = total,
    usuarioEmail = usuarioEmail
)

fun ProductoEntity.toModel(): Producto = Producto(
    id = id,
    codigo = codigo,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    cantidad = cantidad
)

fun Producto.toEntity(compraId: Int): ProductoEntity = ProductoEntity(
    id = id,
    compraId = compraId,
    codigo = codigo,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    cantidad = cantidad
)
