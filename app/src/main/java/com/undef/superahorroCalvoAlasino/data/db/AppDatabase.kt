package com.undef.superahorroCalvoAlasino.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.undef.superahorroCalvoAlasino.data.db.dao.CompraDao
import com.undef.superahorroCalvoAlasino.data.db.dao.ProductoBuscadoDao
import com.undef.superahorroCalvoAlasino.data.db.dao.ProductoDao
import com.undef.superahorroCalvoAlasino.data.db.entities.CompraEntity
import com.undef.superahorroCalvoAlasino.data.db.entities.ProductoBuscadoEntity
import com.undef.superahorroCalvoAlasino.data.db.entities.ProductoEntity

@Database(
    entities = [CompraEntity::class, ProductoEntity::class, ProductoBuscadoEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun compraDao(): CompraDao
    abstract fun productoDao(): ProductoDao
    abstract fun productoBuscadoDao(): ProductoBuscadoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE compras ADD COLUMN ticketImageUri TEXT")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Crear tabla para cachear productos buscados (integración Room + Retrofit)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS productos_buscados (
                        codigo TEXT PRIMARY KEY NOT NULL,
                        nombre TEXT NOT NULL,
                        marca TEXT,
                        imagenUrl TEXT,
                        nutriScore TEXT,
                        ingredientes TEXT,
                        alergenos TEXT,
                        energia TEXT,
                        grasas TEXT,
                        grasasSaturadas TEXT,
                        carbohidratos TEXT,
                        azucares TEXT,
                        proteinas TEXT,
                        sal TEXT,
                        fibra TEXT,
                        timestamp INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "superahorro_db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
