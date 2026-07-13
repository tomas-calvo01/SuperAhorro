package com.undef.superahorroCalvoAlasino

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.undef.superahorroCalvoAlasino.data.db.AppDatabase
import com.undef.superahorroCalvoAlasino.data.network.RetrofitClient
import com.undef.superahorroCalvoAlasino.data.repository.CompraRepository
import com.undef.superahorroCalvoAlasino.data.repository.NetworkRepository
import com.undef.superahorroCalvoAlasino.navigation.AppNavGraph
import com.undef.superahorroCalvoAlasino.ui.theme.SuperAhorroTheme

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getInstance(this) }
    private val compraRepository by lazy {
        CompraRepository(database.compraDao(), database.productoDao())
    }
    private val networkRepository by lazy {
        NetworkRepository(
            RetrofitClient.openFoodFactsApi,
            RetrofitClient.compraApiService,
            database.productoBuscadoDao()  // Inyectar DAO para caché de productos
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperAhorroTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph(
                        compraRepository = compraRepository,
                        networkRepository = networkRepository
                    )
                }
            }
        }
    }
}
