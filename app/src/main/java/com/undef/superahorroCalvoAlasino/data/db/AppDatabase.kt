package com.undef.superahorroCalvoAlasino.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.undef.superahorroCalvoAlasino.data.db.dao.CompraDao
import com.undef.superahorroCalvoAlasino.data.db.dao.ProductoDao
import com.undef.superahorroCalvoAlasino.data.db.entities.CompraEntity
import com.undef.superahorroCalvoAlasino.data.db.entities.ProductoEntity

@Database(
    entities = [CompraEntity::class, ProductoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun compraDao(): CompraDao
    abstract fun productoDao(): ProductoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "superahorro_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
