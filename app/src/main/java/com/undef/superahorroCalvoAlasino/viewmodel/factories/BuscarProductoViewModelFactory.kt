package com.undef.superahorroCalvoAlasino.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.undef.superahorroCalvoAlasino.data.repository.NetworkRepository
import com.undef.superahorroCalvoAlasino.viewmodel.BuscarProductoViewModel

/**
 * Factory para crear instancias de BuscarProductoViewModel con sus dependencias.
 */
class BuscarProductoViewModelFactory(
    private val networkRepository: NetworkRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BuscarProductoViewModel::class.java)) {
            return BuscarProductoViewModel(networkRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

