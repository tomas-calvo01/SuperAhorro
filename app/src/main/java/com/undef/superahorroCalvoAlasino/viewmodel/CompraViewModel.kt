package com.undef.superahorroCalvoAlasino.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorroCalvoAlasino.data.repository.CompraRepository
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

class CompraViewModel(
    private val repository: CompraRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompraUiState())
    val uiState: StateFlow<CompraUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observarTodasLasCompras().collect { comprasList ->
                _uiState.value = _uiState.value.copy(compras = comprasList)
            }
        }
    }

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
                val productos = productosAgregados.map { (nombre, precio) ->
                    Producto(id = 0, codigo = "", nombre = nombre, descripcion = "", precio = precio)
                }
                repository.guardarCompra(
                    Compra(id = 0, supermercado = supermercado, fecha = fecha, hora = hora, total = total, productos = productos)
                )
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

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
                repository.guardarCompra(
                    Compra(id = 0, supermercado = supermercado, fecha = fecha, hora = hora, total = total, productos = productos)
                )
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun agregarProductoACompra(
        compraId: Int,
        codigo: String,
        nombre: String,
        descripcion: String,
        precio: Double
    ) {
        viewModelScope.launch {
            repository.agregarProducto(
                Producto(id = 0, codigo = codigo, nombre = nombre, descripcion = descripcion, precio = precio),
                compraId
            )
        }
    }

    fun obtenerCompra(compraId: Int): Compra? =
        _uiState.value.compras.find { it.id == compraId }

    fun calcularTotalGastado(): Double =
        _uiState.value.compras.sumOf { it.total }

    fun limpiarCompras() {
        // Las compras persisten en Room. Se usa al cerrar sesión para limpiar
        // el estado en memoria; los datos en DB se mantienen para el próximo login.
        _uiState.value = CompraUiState()
    }
}
