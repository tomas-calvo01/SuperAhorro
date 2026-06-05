package com.undef.superahorroCalvoAlasino.data.network.api

import com.undef.superahorroCalvoAlasino.data.network.dto.RegistroCompraRequestDTO
import com.undef.superahorroCalvoAlasino.data.network.dto.RegistroCompraResponseDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface CompraApiService {

    @POST("api/purchases")
    suspend fun registrarCompra(
        @Body compra: RegistroCompraRequestDTO
    ): RegistroCompraResponseDTO
}
