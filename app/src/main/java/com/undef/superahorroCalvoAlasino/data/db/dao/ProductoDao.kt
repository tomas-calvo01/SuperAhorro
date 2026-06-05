package com.undef.superahorroCalvoAlasino.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.undef.superahorroCalvoAlasino.data.db.entities.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(producto: ProductoEntity): Long

    @Update
    suspend fun update(producto: ProductoEntity)

    @Delete
    suspend fun delete(producto: ProductoEntity)

    @Query("SELECT * FROM productos WHERE compraId = :compraId")
    fun observarProductosDeCompra(compraId: Int): Flow<List<ProductoEntity>>
}
