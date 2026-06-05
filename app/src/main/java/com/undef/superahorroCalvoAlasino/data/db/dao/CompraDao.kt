package com.undef.superahorroCalvoAlasino.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.undef.superahorroCalvoAlasino.data.db.entities.CompraConProductos
import com.undef.superahorroCalvoAlasino.data.db.entities.CompraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompraDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(compra: CompraEntity): Long

    @Update
    suspend fun update(compra: CompraEntity)

    @Delete
    suspend fun delete(compra: CompraEntity)

    @Transaction
    @Query("SELECT * FROM compras WHERE usuarioEmail = :email ORDER BY fecha DESC, hora DESC")
    fun observarComprasDeUsuario(email: String): Flow<List<CompraConProductos>>

    @Transaction
    @Query("SELECT * FROM compras ORDER BY fecha DESC, hora DESC")
    fun observarTodasLasCompras(): Flow<List<CompraConProductos>>

    @Transaction
    @Query("SELECT * FROM compras WHERE id = :id")
    suspend fun getCompraConProductos(id: Int): CompraConProductos?
}
