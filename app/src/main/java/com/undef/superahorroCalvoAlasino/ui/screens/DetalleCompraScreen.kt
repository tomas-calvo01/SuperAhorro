package com.undef.superahorroCalvoAlasino.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.undef.superahorroCalvoAlasino.navigation.NavRoutes
import com.undef.superahorroCalvoAlasino.viewmodel.CompraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCompraScreen(navController: NavController, compraId: Int, compraViewModel: CompraViewModel) {
    val compra = compraViewModel.obtenerCompra(compraId)
    var mostrarMenu by remember { mutableStateOf(false) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle Compra #${compra?.id ?: compraId}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (compra != null) {
                        IconButton(onClick = { mostrarMenu = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Más opciones",
                                tint = Color.White
                            )
                        }

                        // Menú dropdown
                        DropdownMenu(
                            expanded = mostrarMenu,
                            onDismissRequest = { mostrarMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Compartir") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Share,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32)
                                    )
                                },
                                onClick = {
                                    // TODO: Implementar compartir (Intent)
                                    mostrarMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Editar") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32)
                                    )
                                },
                                onClick = {
                                    // TODO: Navegar a editar compra
                                    mostrarMenu = false
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Eliminar", color = Color(0xFFD32F2F)) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = Color(0xFFD32F2F)
                                    )
                                },
                                onClick = {
                                    mostrarDialogoEliminar = true
                                    mostrarMenu = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if (compra != null) {
                FloatingActionButton(
                    onClick = { navController.navigate(NavRoutes.NuevoProducto.withId(compra.id)) },
                    containerColor = Color(0xFF2E7D32),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Producto")
                }
            }
        }
    ) { padding ->
        if (compra == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Compra no encontrada",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Gray
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Info Compra
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Supermercado", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Text(compra.supermercado, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))

                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Fecha", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Text(compra.fecha, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                            Column {
                                Text("Hora", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Text(compra.hora, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Text("Total", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Text(
                            "$ ${compra.total}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }

                // Productos
                Text(
                    "Productos (${compra.productos.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (compra.productos.isEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Sin productos registrados",
                            modifier = Modifier.padding(24.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(compra.productos) { producto ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Nombre del producto
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                producto.nombre,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = Color(0xFF2E7D32)
                                            )
                                        }
                                        Text(
                                            "$${String.format("%.2f", producto.precio)}",
                                            fontWeight = FontWeight.ExtraBold,
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }

                                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE0E0E0))

                                    // ID del producto
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "ID Producto:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            producto.id.toString(),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF2E7D32),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Código
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "Código:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            if (producto.codigo.isEmpty()) "N/A" else producto.codigo,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF2E7D32),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Descripción
                                    if (producto.descripcion.isNotEmpty()) {
                                        Column {
                                            Text(
                                                "Descripción:",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                producto.descripcion,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF2E7D32)
                                            )
                                        }
                                    }

                                    // Precio
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "Precio Unitario:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            "$${String.format("%.2f", producto.precio)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF2E7D32),
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Resumen
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total de compra:", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "${compra.productos.size} producto${if (compra.productos.size != 1) "s" else ""}",
                                    color = Color.White.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Text(
                                "$ ${"%.2f".format(compra.total)}",
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (mostrarDialogoEliminar && compra != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F)
                )
            },
            title = {
                Text(
                    "Eliminar Compra",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Esta acción no se puede deshacer. ¿Estás seguro que querés eliminar esta compra?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        // TODO: Implementar eliminación cuando haya Room
                        mostrarDialogoEliminar = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}


