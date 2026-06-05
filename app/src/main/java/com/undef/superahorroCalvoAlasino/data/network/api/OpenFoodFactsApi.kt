package com.undef.superahorroCalvoAlasino.data.network.api

import com.undef.superahorroCalvoAlasino.data.network.dto.ProductoDTO
import com.undef.superahorroCalvoAlasino.data.network.dto.ProductoSearchResponseDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenFoodFactsApi {

    @GET("cgi/search.pl")
    suspend fun buscarProductos(
        @Query("search_terms") termino: String,
        @Query("search_simple") simple: Int = 1,
        @Query("action") action: String = "process",
        @Query("json") json: Int = 1,
        @Query("page_size") pageSize: Int = 10
    ): ProductoSearchResponseDTO

    @GET("api/v2/product/{barcode}.json")
    suspend fun obtenerProductoPorCodigo(
        @Path("barcode") barcode: String
    ): ProductoDTO
}
