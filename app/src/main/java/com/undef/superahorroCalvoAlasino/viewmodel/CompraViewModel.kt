package com.undef.superahorroCalvoAlasino.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.undef.superahorroCalvoAlasino.model.Compra
import com.undef.superahorroCalvoAlasino.model.Producto
import org.json.JSONArray
import org.json.JSONObject

class CompraViewModel(private var context: Context? = null) : ViewModel() {
    // Lista de compras del usuario (comienza vacía)
    private val _compras = mutableStateListOf<Compra>()
    val compras: List<Compra> = _compras

    // ID auto-incremental para nuevas compras
    private var proximoId = 1
    
    private var sharedPreferences: SharedPreferences? = null
    private var usuarioActualEmail: String? = null

    @Suppress("UNUSED")
    fun setContext(context: Context) {
        this.context = context
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("super_ahorro_compras", Context.MODE_PRIVATE)
        }
    }

    // Cargar compras del usuario específico
    fun cargarComprasDelUsuario(emailUsuario: String) {
        if (sharedPreferences == null) {
            context?.let {
                sharedPreferences = it.getSharedPreferences("super_ahorro_compras", Context.MODE_PRIVATE)
            }
        }
        
        usuarioActualEmail = emailUsuario
        _compras.clear()
        proximoId = 1

        sharedPreferences?.let {
            val comprasJson = it.getString("compras_$emailUsuario", "[]")
            if (comprasJson != null && comprasJson.isNotEmpty() && comprasJson != "[]") {
                try {
                    val jsonArray = JSONArray(comprasJson)
                    for (i in 0 until jsonArray.length()) {
                        val jsonCompra = jsonArray.getJSONObject(i)
                        val id = jsonCompra.getInt("id")
                        val supermercado = jsonCompra.getString("supermercado")
                        val fecha = jsonCompra.getString("fecha")
                        val hora = jsonCompra.getString("hora")
                        val total = jsonCompra.getDouble("total")

                        // Cargar productos
                        val productosJson = jsonCompra.getJSONArray("productos")
                        val productos = mutableListOf<Producto>()
                        for (j in 0 until productosJson.length()) {
                            val jsonProducto = productosJson.getJSONObject(j)
                            productos.add(
                                Producto(
                                    id = jsonProducto.getInt("id"),
                                    codigo = jsonProducto.getString("codigo"),
                                    nombre = jsonProducto.getString("nombre"),
                                    descripcion = jsonProducto.getString("descripcion"),
                                    precio = jsonProducto.getDouble("precio")
                                )
                            )
                        }

                        _compras.add(Compra(id, supermercado, fecha, hora, total, productos))
                        proximoId = maxOf(proximoId, id + 1)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Guardar compras del usuario actual
    private fun guardarCompras() {
        if (sharedPreferences == null) {
            context?.let {
                sharedPreferences = it.getSharedPreferences("super_ahorro_compras", Context.MODE_PRIVATE)
            }
        }
        
        sharedPreferences?.let {
            if (usuarioActualEmail != null) {
                try {
                    val jsonArray = JSONArray()
                    _compras.forEach { compra ->
                        val jsonCompra = JSONObject()
                        jsonCompra.put("id", compra.id)
                        jsonCompra.put("supermercado", compra.supermercado)
                        jsonCompra.put("fecha", compra.fecha)
                        jsonCompra.put("hora", compra.hora)
                        jsonCompra.put("total", compra.total)

                        // Guardar productos
                        val productosJson = JSONArray()
                        compra.productos.forEach { producto ->
                            val jsonProducto = JSONObject()
                            jsonProducto.put("id", producto.id)
                            jsonProducto.put("codigo", producto.codigo)
                            jsonProducto.put("nombre", producto.nombre)
                            jsonProducto.put("descripcion", producto.descripcion)
                            jsonProducto.put("precio", producto.precio)
                            productosJson.put(jsonProducto)
                        }
                        jsonCompra.put("productos", productosJson)
                        jsonArray.put(jsonCompra)
                    }
                    it.edit {
                        putString("compras_$usuarioActualEmail", jsonArray.toString())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Agregar una nueva compra con múltiples productos
    @Suppress("UNUSED")
    fun agregarCompra(
        supermercado: String,
        fecha: String,
        hora: String,
        total: Double,
        productosAgregados: List<Pair<String, Double>> = emptyList()
    ) {
        try {
            val productos = productosAgregados.mapIndexed { index, (nombre, precio) ->
                Producto(
                    id = index + 1,
                    codigo = "",
                    nombre = nombre,
                    descripcion = "",
                    precio = precio
                )
            }
            
            val nuevaCompra = Compra(
                id = proximoId++,
                supermercado = supermercado,
                fecha = fecha,
                hora = hora,
                total = total,
                productos = productos
            )
            _compras.add(0, nuevaCompra)
            guardarCompras()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Agregar producto a una compra existente
    fun agregarProductoACompra(
        compraId: Int,
        codigo: String,
        nombre: String,
        descripcion: String,
        precio: Double
    ) {
        val compra = _compras.find { it.id == compraId }
        if (compra != null) {
            val nuevoProducto = Producto(
                id = compra.productos.size + 1,
                codigo = codigo,
                nombre = nombre,
                descripcion = descripcion,
                precio = precio
            )
            val productosActualizados = compra.productos.toMutableList()
            productosActualizados.add(nuevoProducto)
            
            // Actualizar la compra con los productos nuevos
            val indexCompra = _compras.indexOf(compra)
            _compras[indexCompra] = compra.copy(productos = productosActualizados)
            guardarCompras()
        }
    }

    // Obtener compra por ID
    fun obtenerCompra(compraId: Int): Compra? {
        return _compras.find { it.id == compraId }
    }

    // Calcular total gastado
    fun calcularTotalGastado(): Double {
        return _compras.sumOf { it.total }
    }

    // Limpiar todas las compras (se usa al cerrar sesión)
    fun limpiarCompras() {
        _compras.clear()
        proximoId = 1
        usuarioActualEmail = null
    }
}

