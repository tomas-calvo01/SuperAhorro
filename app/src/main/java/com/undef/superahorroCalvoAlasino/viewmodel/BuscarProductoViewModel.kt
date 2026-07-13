package com.undef.superahorroCalvoAlasino.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorroCalvoAlasino.data.network.dto.ProductoDTO
import com.undef.superahorroCalvoAlasino.data.repository.NetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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

    /**
     * Buscar productos desde Room + Retrofit con caché automático.
     * Ahora consume Flow en vez de Result para observar cambios reactivos desde Room.
     */
    fun buscarProductos(termino: String) {
        if (termino.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // NetworkRepository ahora retorna Flow desde Room (single source of truth)
            networkRepository.buscarProductos(termino)
                .catch { error ->
                    // Manejar errores (sin internet, sin caché, etc.)
                    _uiState.update {
                        it.copy(
                            error = "Error al buscar: ${error.message}",
                            isLoading = false
                        )
                    }
                }
                .collect { productos ->
                    // Actualizar UI con productos desde Room
                    _uiState.update {
                        it.copy(
                            productos = productos,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun limpiar() {
        _uiState.update { BuscarProductoUiState() }
    }
}
