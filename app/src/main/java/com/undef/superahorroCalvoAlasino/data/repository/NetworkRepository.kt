package com.undef.superahorroCalvoAlasino.data.repository

import com.undef.superahorroCalvoAlasino.data.db.dao.ProductoBuscadoDao
import com.undef.superahorroCalvoAlasino.data.db.mappers.toDTOList
import com.undef.superahorroCalvoAlasino.data.db.mappers.toEntityList
import com.undef.superahorroCalvoAlasino.data.network.api.CompraApiService
import com.undef.superahorroCalvoAlasino.data.network.api.OpenFoodFactsApi
import com.undef.superahorroCalvoAlasino.data.network.dto.ProductoDTO
import com.undef.superahorroCalvoAlasino.data.network.dto.RegistroCompraRequestDTO
import com.undef.superahorroCalvoAlasino.data.network.dto.RegistroCompraResponseDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Repository que integra Room + Retrofit siguiendo el patrón "Single Source of Truth".
 * Room es la única fuente de verdad, la API solo se consulta si no hay caché.
 */
class NetworkRepository(
    private val openFoodFactsApi: OpenFoodFactsApi,
    private val compraApiService: CompraApiService,
    private val productoBuscadoDao: ProductoBuscadoDao
) {

    companion object {
        // Caché válido por 7 días
        private const val CACHE_EXPIRATION_MS = 7L * 24 * 60 * 60 * 1000
    }

    /**
     * Buscar productos implementando caché con Room.
     *
     * Flujo:
     * 1. Buscar en Room (caché local)
     * 2. Si hay resultados válidos → retornar desde Room
     * 3. Si no hay resultados → llamar API → guardar en Room → retornar desde Room
     *
     * Room es la ÚNICA fuente de verdad (Single Source of Truth).
     */
    fun buscarProductos(termino: String): Flow<List<ProductoDTO>> = flow {
        // 1. Intentar obtener desde caché (Room)
        val productosEnCache = productoBuscadoDao.buscarProductos(termino).firstOrNull()

        if (!productosEnCache.isNullOrEmpty()) {
            // Verificar si el caché no está expirado (más de 7 días)
            val cacheMasReciente = productosEnCache.maxOfOrNull { it.timestamp } ?: 0L
            val cacheValido = System.currentTimeMillis() - cacheMasReciente < CACHE_EXPIRATION_MS

            if (cacheValido) {
                // 2. Caché válido → emitir desde Room
                emit(productosEnCache.toDTOList())
                return@flow
            }
        }

        // 3. No hay caché o está expirado → llamar a la API
        try {
            val response = openFoodFactsApi.buscarProductos(termino)
            val productosAPI = response.products

            if (productosAPI.isNotEmpty()) {
                // 4. Guardar en Room
                withContext(Dispatchers.IO) {
                    val entidades = productosAPI.toEntityList()
                    if (entidades.isNotEmpty()) {
                        productoBuscadoDao.insertAll(entidades)
                    }
                }

                // 5. Emitir desde Room (single source of truth)
                val productosActualizados = productoBuscadoDao.buscarProductos(termino).firstOrNull()
                emit(productosActualizados?.toDTOList() ?: emptyList())
            } else {
                // API no devolvió resultados
                emit(emptyList())
            }
        } catch (e: Exception) {
            // 6. Error de red → intentar usar caché aunque esté expirado
            if (!productosEnCache.isNullOrEmpty()) {
                emit(productosEnCache.toDTOList())
            } else {
                throw e // Sin caché y error de red → propagar error
            }
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Limpiar productos con caché expirado (llamar periódicamente o al inicio de la app).
     */
    suspend fun limpiarCacheExpirado() {
        val tiempoLimite = System.currentTimeMillis() - CACHE_EXPIRATION_MS
        productoBuscadoDao.eliminarProductosViejos(tiempoLimite)
    }

    suspend fun registrarCompraRemota(
        usuarioEmail: String,
        supermercado: String,
        total: Double,
        fecha: String,
        cantidadProductos: Int
    ): Result<RegistroCompraResponseDTO> {
        return try {
            val request = RegistroCompraRequestDTO(
                usuarioEmail = usuarioEmail,
                supermercado = supermercado,
                total = total,
                fecha = fecha,
                cantidadProductos = cantidadProductos
            )
            val response = compraApiService.registrarCompra(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
