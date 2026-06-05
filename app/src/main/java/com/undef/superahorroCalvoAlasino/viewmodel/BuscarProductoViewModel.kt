package com.undef.superahorroCalvoAlasino.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorroCalvoAlasino.data.network.dto.ProductoDTO
import com.undef.superahorroCalvoAlasino.data.repository.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BuscarProductoUiState(
    val productos: List<ProductoDTO> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class BuscarProductoViewModel(
    private val networkRepository: NetworkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BuscarProductoUiState())
    val uiState: StateFlow<BuscarProductoUiState> = _uiState.asStateFlow()

    fun buscarProductos(termino: String) {
        if (termino.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = networkRepository.buscarProductos(termino)
            result.fold(
                onSuccess = { productos ->
                    _uiState.update { it.copy(productos = productos, isLoading = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(error = "Error al buscar: ${error.message}", isLoading = false) }
                }
            )
        }
    }

    fun limpiar() {
        _uiState.update { BuscarProductoUiState() }
    }
}
