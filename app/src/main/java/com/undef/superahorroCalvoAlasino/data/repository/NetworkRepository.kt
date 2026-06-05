package com.undef.superahorroCalvoAlasino.data.repository

import com.undef.superahorroCalvoAlasino.data.network.api.CompraApiService
import com.undef.superahorroCalvoAlasino.data.network.api.OpenFoodFactsApi
import com.undef.superahorroCalvoAlasino.data.network.dto.ProductoDTO
import com.undef.superahorroCalvoAlasino.data.network.dto.RegistroCompraRequestDTO
import com.undef.superahorroCalvoAlasino.data.network.dto.RegistroCompraResponseDTO

class NetworkRepository(
    private val openFoodFactsApi: OpenFoodFactsApi,
    private val compraApiService: CompraApiService
) {

    suspend fun buscarProductos(termino: String): Result<List<ProductoDTO>> {
        return try {
            val response = openFoodFactsApi.buscarProductos(termino)
            Result.success(response.products)
        } catch (e: Exception) {
            Result.failure(e)
        }
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
