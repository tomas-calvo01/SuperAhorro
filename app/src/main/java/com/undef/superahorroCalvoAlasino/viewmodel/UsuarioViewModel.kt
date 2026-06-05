package com.undef.superahorroCalvoAlasino.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.undef.superahorroCalvoAlasino.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class Usuario(
    val nombre: String = "",
    val email: String = "",
    val password: String = ""
)

data class UsuarioUiState(
    val usuario: Usuario = Usuario(),
    val isLoggedIn: Boolean = false
)

class UsuarioViewModel(
    private val repo: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsuarioUiState())
    val uiState: StateFlow<UsuarioUiState> = _uiState.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = repo.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        viewModelScope.launch {
            combine(
                repo.userName,
                repo.userEmail,
                repo.userPassword,
                repo.isLoggedIn
            ) { nombre, email, password, loggedIn ->
                UsuarioUiState(
                    usuario = Usuario(nombre = nombre, email = email, password = password),
                    isLoggedIn = loggedIn
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun registrarUsuario(nombre: String, email: String, password: String) {
        viewModelScope.launch {
            repo.saveCredentials(email, nombre, password)
            repo.saveSession(email, nombre)
        }
    }

    fun actualizarPerfil(nombre: String, email: String, nuevaPassword: String) {
        viewModelScope.launch {
            repo.saveCredentials(email, nombre, nuevaPassword)
            repo.saveSession(email, nombre)
        }
    }

    fun validarCredenciales(email: String, password: String): Boolean {
        val usuarioGuardado = _uiState.value.usuario
        val result = usuarioGuardado.email == email && usuarioGuardado.password == password
        if (result) {
            viewModelScope.launch {
                repo.saveSession(email, usuarioGuardado.nombre)
            }
        }
        return result
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            repo.clearSession()
        }
    }

    fun usuarioExistente(): Boolean = _uiState.value.usuario.email.isNotEmpty()

    fun obtenerUsuario(): Usuario = _uiState.value.usuario

    suspend fun checkInitialLoginState(): Boolean = repo.readInitialLoginState()
}
