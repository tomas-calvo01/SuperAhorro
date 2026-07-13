package com.undef.superahorroCalvoAlasino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.undef.superahorroCalvoAlasino.model.Producto
import com.undef.superahorroCalvoAlasino.viewmodel.CompraViewModel

// Clase de datos auxiliar para el formulario de productos
data class ProductoFormulario(
    val id: String = "",
    val codigo: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaCompraScreen(navController: NavController, compraViewModel: CompraViewModel) {
    var supermercado by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    
    // Estados para el formulario de productos
    var productoForm by remember { mutableStateOf(ProductoFormulario()) }
    var productos by remember { mutableStateOf(listOf<ProductoFormulario>()) }
    
    // Calcular total automáticamente
    val precioTotal = productos.sumOf { 
        it.precio.toDoubleOrNull() ?: 0.0 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Compra") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Registrar Nueva Compra",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = supermercado,
                onValueChange = { supermercado = it },
                label = { Text("Supermercado") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej: Carrefour") }
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("DD/MM/YYYY") }
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = hora,
                onValueChange = { hora = it },
                label = { Text("Hora") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("HH:MM") }
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Agregar Productos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )

            Spacer(Modifier.height(12.dp))

            // Formulario para agregar productos
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ID del producto
                OutlinedTextField(
                    value = productoForm.id,
                    onValueChange = { productoForm = productoForm.copy(id = it) },
                    label = { Text("ID Producto") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("Ej: 1") }
                )

                // C��digo
                OutlinedTextField(
                    value = productoForm.codigo,
                    onValueChange = { productoForm = productoForm.copy(codigo = it) },
                    label = { Text("Código") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej: SKU12345") }
                )

                // Nombre
                OutlinedTextField(
                    value = productoForm.nombre,
                    onValueChange = { productoForm = productoForm.copy(nombre = it) },
                    label = { Text("Nombre del Producto") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej: Leche entera 1L") }
                )

                // Descripción
                OutlinedTextField(
                    value = productoForm.descripcion,
                    onValueChange = { productoForm = productoForm.copy(descripcion = it) },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej: Leche descremada marca La Serenísima") },
                    maxLines = 2
                )

                // Precio
                OutlinedTextField(
                    value = productoForm.precio,
                    onValueChange = { productoForm = productoForm.copy(precio = it) },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    placeholder = { Text("Ej: 150.50") }
                )

                // Botón agregar producto
                Button(
                    onClick = {
                        if (productoForm.nombre.isNotBlank() && productoForm.precio.isNotBlank()) {
                            productos = productos + productoForm
                            productoForm = ProductoFormulario()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Agregar Producto")
                }
            }

            Spacer(Modifier.height(16.dp))

            // Lista de productos agregados
            if (productos.isNotEmpty()) {
                Text(
                    text = "Productos Agregados (${productos.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )

                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                ) {
                    items(productos.withIndex().toList()) { (index, producto) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = producto.nombre,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2E7D32)
                                )
                                Text(
                                    text = "Código: ${producto.codigo}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "Precio: $${String.format("%.2f", producto.precio.toDoubleOrNull() ?: 0.0)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                            IconButton(
                                onClick = {
                                    productos = productos.filterIndexed { i, _ -> i != index }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color(0xFFD32F2F)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Mostrar total calculado automáticamente
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total de Compra",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "$${String.format("%.2f", precioTotal)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (supermercado.isNotBlank() && fecha.isNotBlank() && 
                        hora.isNotBlank() && productos.isNotEmpty()) {
                        try {
                            // Convertir los productos del formulario a objetos Producto
                            val productosGuardar = productos.mapIndexed { index, productoForm ->
                                Producto(
                                    id = productoForm.id.toIntOrNull() ?: (index + 1),
                                    codigo = productoForm.codigo,
                                    nombre = productoForm.nombre,
                                    descripcion = productoForm.descripcion,
                                    precio = productoForm.precio.toDoubleOrNull() ?: 0.0
                                )
                            }
                            
                            // ✅ FLUJO INTEGRADO POST + ROOM:
                            // 1. Guarda en Room primero (optimistic update)
                            // 2. Sincroniza con servidor (POST)
                            // 3. Si exitoso → Room ya notificó el Flow
                            // 4. UI se actualiza automáticamente con collectAsStateWithLifecycle
                            compraViewModel.guardarCompraConSincronizacion(
                                supermercado = supermercado,
                                fecha = fecha,
                                hora = hora,
                                total = precioTotal,
                                productos = productosGuardar
                            )
                            navController.popBackStack()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                enabled = supermercado.isNotBlank() && fecha.isNotBlank() && hora.isNotBlank() && productos.isNotEmpty()
            ) {
                Text("Guardar Compra")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}


