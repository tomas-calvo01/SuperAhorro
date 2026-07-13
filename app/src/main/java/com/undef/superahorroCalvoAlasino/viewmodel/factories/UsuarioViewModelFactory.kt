package com.undef.superahorroCalvoAlasino.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.undef.superahorroCalvoAlasino.data.preferences.UserPreferencesRepository
import com.undef.superahorroCalvoAlasino.viewmodel.UsuarioViewModel

/**
 * Factory para crear instancias de UsuarioViewModel con sus dependencias.
 */
class UsuarioViewModelFactory(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
            return UsuarioViewModel(userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

