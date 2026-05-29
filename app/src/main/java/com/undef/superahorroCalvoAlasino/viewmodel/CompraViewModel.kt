package com.undef.superahorroCalvoAlasino.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorroCalvoAlasino.model.Compra
import com.undef.superahorroCalvoAlasino.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CompraUiState(
    val compras: List<Compra> = emptyList(),
    val compraSeleccionada: Compra? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class CompraViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CompraUiState())
    val uiState: StateFlow<CompraUiState> = _uiState.asStateFlow()

    private var proximoId = 1

    // Agregar una nueva compra con múltiples productos
    fun agregarCompra(
        supermercado: String,
        fecha: String,
        hora: String,
        total: Double,
        productosAgregados: List<Pair<String, Double>> = emptyList()
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val productos = productosAgregados.mapIndexed { index, (nombre, precio) ->
                    Producto(
                        id = index + 1,
                        codigo = "",
                        nombre = nombre,
                        descripcion = "",
                        precio = precio
                    )
                }

                val nuevaCompra = Compra(
                    id = proximoId++,
                    supermercado = supermercado,
                    fecha = fecha,
                    hora = hora,
                    total = total,
                    productos = productos
                )

                val comprasActualizadas = listOf(nuevaCompra) + _uiState.value.compras
                _uiState.value = _uiState.value.copy(
                    compras = comprasActualizadas,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    // Agregar una nueva compra con productos detallados
    fun agregarCompraConProductos(
        supermercado: String,
        fecha: String,
        hora: String,
        total: Double,
        productos: List<Producto> = emptyList()
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val nuevaCompra = Compra(
                    id = proximoId++,
                    supermercado = supermercado,
                    fecha = fecha,
                    hora = hora,
                    total = total,
                    productos = productos
                )

                val comprasActualizadas = listOf(nuevaCompra) + _uiState.value.compras
                _uiState.value = _uiState.value.copy(
                    compras = comprasActualizadas,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    // Agregar producto a una compra existente
    fun agregarProductoACompra(
        compraId: Int,
        codigo: String,
        nombre: String,
        descripcion: String,
        precio: Double
    ) {
        viewModelScope.launch {
            val compras = _uiState.value.compras.toMutableList()
            val compraIndex = compras.indexOfFirst { it.id == compraId }

            if (compraIndex != -1) {
                val compra = compras[compraIndex]
                val nuevoProducto = Producto(
                    id = compra.productos.size + 1,
                    codigo = codigo,
                    nombre = nombre,
                    descripcion = descripcion,
                    precio = precio
                )

                val productosActualizados = compra.productos + nuevoProducto
                compras[compraIndex] = compra.copy(productos = productosActualizados)

                _uiState.value = _uiState.value.copy(compras = compras)
            }
        }
    }

    // Obtener compra por ID
    fun obtenerCompra(compraId: Int): Compra? {
        return _uiState.value.compras.find { it.id == compraId }
    }

    // Calcular total gastado
    fun calcularTotalGastado(): Double {
        return _uiState.value.compras.sumOf { it.total }
    }

    // Limpiar todas las compras (se usa al cerrar sesión)
    fun limpiarCompras() {
        viewModelScope.launch {
            _uiState.value = CompraUiState()
            proximoId = 1
        }
    }
}

