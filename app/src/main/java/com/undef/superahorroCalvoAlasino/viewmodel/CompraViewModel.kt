package com.undef.superahorroCalvoAlasino.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorroCalvoAlasino.data.repository.CompraRepository
import com.undef.superahorroCalvoAlasino.data.repository.NetworkRepository
import com.undef.superahorroCalvoAlasino.model.Compra
import com.undef.superahorroCalvoAlasino.model.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
    private val repository: CompraRepository,
    private val networkRepository: NetworkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompraUiState())
    val uiState: StateFlow<CompraUiState> = _uiState.asStateFlow()

    private val _mensajeRegistroRemoto = MutableStateFlow<String?>(null)
    val mensajeRegistroRemoto: StateFlow<String?> = _mensajeRegistroRemoto.asStateFlow()

    private var currentUserEmail: String = ""
    private var collectJob: Job? = null

    fun setCurrentUser(email: String) {
        if (email == currentUserEmail && collectJob?.isActive == true) return
        currentUserEmail = email
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            if (email.isNotEmpty()) {
                repository.observarComprasDeUsuario(email).collect { comprasList ->
                    _uiState.value = _uiState.value.copy(compras = comprasList)
                }
            } else {
                _uiState.value = _uiState.value.copy(compras = emptyList())
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
                    Compra(
                        id = 0, supermercado = supermercado, fecha = fecha, hora = hora,
                        total = total, productos = productos, usuarioEmail = currentUserEmail
                    )
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
                    Compra(
                        id = 0, supermercado = supermercado, fecha = fecha, hora = hora,
                        total = total, productos = productos, usuarioEmail = currentUserEmail
                    )
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
        collectJob?.cancel()
        collectJob = null
        currentUserEmail = ""
        _uiState.value = CompraUiState()
    }

    fun registrarCompraRemota(compra: Compra) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = networkRepository.registrarCompraRemota(
                usuarioEmail = compra.usuarioEmail,
                supermercado = compra.supermercado,
                total = compra.total,
                fecha = compra.fecha,
                cantidadProductos = compra.productos.size
            )
            result.fold(
                onSuccess = { response ->
                    _mensajeRegistroRemoto.value = "Compra sincronizada correctamente (ID: ${response.id})"
                },
                onFailure = { error ->
                    _mensajeRegistroRemoto.value = "Error al sincronizar: ${error.message}"
                }
            )
        }
    }

    fun limpiarMensajeRegistro() {
        _mensajeRegistroRemoto.value = null
    }
}
