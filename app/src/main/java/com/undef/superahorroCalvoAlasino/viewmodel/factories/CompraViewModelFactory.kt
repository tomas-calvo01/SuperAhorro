package com.undef.superahorroCalvoAlasino.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.undef.superahorroCalvoAlasino.data.repository.CompraRepository
import com.undef.superahorroCalvoAlasino.data.repository.NetworkRepository
import com.undef.superahorroCalvoAlasino.viewmodel.CompraViewModel

/**
 * Factory para crear instancias de CompraViewModel con sus dependencias.
 */
class CompraViewModelFactory(
    private val compraRepository: CompraRepository,
    private val networkRepository: NetworkRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CompraViewModel::class.java)) {
            return CompraViewModel(compraRepository, networkRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

