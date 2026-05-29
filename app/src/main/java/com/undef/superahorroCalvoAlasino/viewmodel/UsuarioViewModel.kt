package com.undef.superahorroCalvoAlasino.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Usuario(
    val nombre: String = "",
    val email: String = "",
    val password: String = ""
)

data class UsuarioUiState(
    val usuario: Usuario = Usuario(),
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)

class UsuarioViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UsuarioUiState())
    val uiState: StateFlow<UsuarioUiState> = _uiState.asStateFlow()

    fun registrarUsuario(nombre: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            
            // Simular registro (en la próxima entrega esto iría a un Repository)
            _uiState.value = _uiState.value.copy(
                usuario = Usuario(nombre = nombre, email = email, password = password),
                isLoading = false,
                error = null
            )
        }
    }

    fun actualizarPerfil(nombre: String, email: String, nuevaPassword: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            
            _uiState.value = _uiState.value.copy(
                usuario = Usuario(nombre = nombre, email = email, password = nuevaPassword),
                isLoading = false
            )
        }
    }

    fun validarCredenciales(email: String, password: String): Boolean {
        val usuarioActual = _uiState.value.usuario
        val esValido = usuarioActual.email == email && usuarioActual.password == password
        
        if (esValido) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = true,
                    error = null
                )
            }
        }
        
        return esValido
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            _uiState.value = UsuarioUiState()
        }
    }

    fun usuarioExistente(): Boolean {
        return _uiState.value.usuario.email.isNotEmpty()
    }
}

