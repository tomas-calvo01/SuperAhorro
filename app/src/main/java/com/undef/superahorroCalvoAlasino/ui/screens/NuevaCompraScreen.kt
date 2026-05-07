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
import com.undef.superahorroCalvoAlasino.viewmodel.CompraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaCompraScreen(navController: NavController, compraViewModel: CompraViewModel) {
    var supermercado by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var precioTotal by remember { mutableStateOf("") }
    var productoNombre by remember { mutableStateOf("") }
    var productos by remember { mutableStateOf(listOf<String>()) }

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

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = productoNombre,
                    onValueChange = { productoNombre = it },
                    label = { Text("Producto") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ej: Leche") }
                )

                Button(
                    onClick = {
                        if (productoNombre.isNotBlank()) {
                            productos = productos + productoNombre
                            productoNombre = ""
                        }
                    },
                    modifier = Modifier.size(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }

            Spacer(Modifier.height(16.dp))

            if (productos.isNotEmpty()) {
                Text(
                    text = "Productos (${productos.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                ) {
                    items(productos) { producto ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = producto,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2E7D32)
                            )
                            IconButton(
                                onClick = {
                                    productos = productos.filter { it != producto }
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

            OutlinedTextField(
                value = precioTotal,
                onValueChange = { precioTotal = it },
                label = { Text("Precio Total de Compra") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                placeholder = { Text("Ej: 5420.50") }
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (supermercado.isNotBlank() && fecha.isNotBlank() && 
                        hora.isNotBlank() && precioTotal.isNotBlank() && productos.isNotEmpty()) {
                        
                        try {
                            val total = precioTotal.toDouble()
                            compraViewModel.agregarCompra(
                                supermercado = supermercado,
                                fecha = fecha,
                                hora = hora,
                                total = total,
                                productosAgregados = productos.map { Pair(it, 0.0) }
                            )
                            navController.popBackStack()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                enabled = supermercado.isNotBlank() && fecha.isNotBlank() && hora.isNotBlank() && precioTotal.isNotBlank() && productos.isNotEmpty()
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


