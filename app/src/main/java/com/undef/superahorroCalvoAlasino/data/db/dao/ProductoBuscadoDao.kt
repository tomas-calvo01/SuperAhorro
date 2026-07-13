package com.undef.superahorroCalvoAlasino.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.undef.superahorroCalvoAlasino.data.db.entities.ProductoBuscadoEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para gestionar productos buscados en caché.
 */
@Dao
interface ProductoBuscadoDao {

    /**
     * Insertar o actualizar productos desde la API.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(productos: List<ProductoBuscadoEntity>)

    /**
     * Buscar productos por término en el nombre.
     * Retorna Flow para observar cambios reactivamente.
     */
    @Query("SELECT * FROM productos_buscados WHERE nombre LIKE '%' || :termino || '%' ORDER BY timestamp DESC")
    fun buscarProductos(termino: String): Flow<List<ProductoBuscadoEntity>>

    /**
     * Obtener un producto específico por código.
     */
    @Query("SELECT * FROM productos_buscados WHERE codigo = :codigo LIMIT 1")
    suspend fun getProductoPorCodigo(codigo: String): ProductoBuscadoEntity?

    /**
     * Eliminar productos con caché expirado (más de 7 días).
     */
    @Query("DELETE FROM productos_buscados WHERE timestamp < :tiempoLimite")
    suspend fun eliminarProductosViejos(tiempoLimite: Long)

    /**
     * Contar productos en caché.
     */
    @Query("SELECT COUNT(*) FROM productos_buscados")
    suspend fun contarProductos(): Int

    /**
     * Limpiar todo el caché.
     */
    @Query("DELETE FROM productos_buscados")
    suspend fun limpiarCache()
}

