package com.undef.superahorroCalvoAlasino.data.network

import com.undef.superahorroCalvoAlasino.BuildConfig
import com.undef.superahorroCalvoAlasino.data.network.api.CompraApiService
import com.undef.superahorroCalvoAlasino.data.network.api.OpenFoodFactsApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val OPEN_FOOD_FACTS_BASE_URL = "https://world.openfoodfacts.org/"
    private const val REQRES_BASE_URL = "https://reqres.in/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("User-Agent", "SuperAhorro/1.0 (Android)")
                    .build()
            )
        }
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val openFoodFactsApi: OpenFoodFactsApi by lazy {
        Retrofit.Builder()
            .baseUrl(OPEN_FOOD_FACTS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFoodFactsApi::class.java)
    }

    val compraApiService: CompraApiService by lazy {
        Retrofit.Builder()
            .baseUrl(REQRES_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CompraApiService::class.java)
    }
}
