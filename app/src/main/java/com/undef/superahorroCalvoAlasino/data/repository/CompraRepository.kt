package com.undef.superahorroCalvoAlasino.data.repository

import com.undef.superahorroCalvoAlasino.data.db.dao.CompraDao
import com.undef.superahorroCalvoAlasino.data.db.dao.ProductoDao
import com.undef.superahorroCalvoAlasino.data.db.mappers.toEntity
import com.undef.superahorroCalvoAlasino.data.db.mappers.toModel
import com.undef.superahorroCalvoAlasino.model.Compra
import com.undef.superahorroCalvoAlasino.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CompraRepository(
    private val compraDao: CompraDao,
    private val productoDao: ProductoDao
) {

    fun observarComprasDeUsuario(email: String): Flow<List<Compra>> =
        compraDao.observarComprasDeUsuario(email).map { list -> list.map { it.toModel() } }

    fun observarTodasLasCompras(): Flow<List<Compra>> =
        compraDao.observarTodasLasCompras().map { list -> list.map { it.toModel() } }

    suspend fun guardarCompra(compra: Compra): Int {
        val generatedId = compraDao.insert(compra.toEntity()).toInt()
        compra.productos.forEach { producto ->
            productoDao.insert(producto.toEntity(generatedId))
        }
        return generatedId
    }

    suspend fun actualizarCompra(compra: Compra) {
        compraDao.update(compra.toEntity())
    }

    suspend fun eliminarCompra(compra: Compra) {
        compraDao.delete(compra.toEntity())
    }

    suspend fun agregarProducto(producto: Producto, compraId: Int) {
        productoDao.insert(producto.toEntity(compraId))
    }

    suspend fun actualizarProducto(producto: Producto, compraId: Int) {
        productoDao.update(producto.toEntity(compraId))
    }

    suspend fun eliminarProducto(producto: Producto, compraId: Int) {
        productoDao.delete(producto.toEntity(compraId))
    }

    suspend fun getCompraConProductos(id: Int): Compra? =
        compraDao.getCompraConProductos(id)?.toModel()

    suspend fun actualizarTicketUri(compraId: Int, uri: String?) {
        compraDao.updateTicketUri(compraId, uri)
    }
}
