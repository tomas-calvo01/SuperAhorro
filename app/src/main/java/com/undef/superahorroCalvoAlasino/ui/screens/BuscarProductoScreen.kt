package com.undef.superahorroCalvoAlasino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.undef.superahorroCalvoAlasino.data.network.dto.ProductoDTO
import com.undef.superahorroCalvoAlasino.viewmodel.BuscarProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscarProductoScreen(
    navController: NavController,
    compraId: Int,
    viewModel: BuscarProductoViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var termino by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    DisposableEffect(Unit) {
        onDispose { viewModel.limpiar() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar Producto") },
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
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = termino,
                    onValueChange = { termino = it },
                    label = { Text("Nombre del producto") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        keyboardController?.hide()
                        viewModel.buscarProductos(termino)
                    }),
                    trailingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF2E7D32))
                    }
                )
                Button(
                    onClick = {
                        keyboardController?.hide()
                        viewModel.buscarProductos(termino)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    enabled = termino.isNotBlank()
                ) {
                    Text("Buscar")
                }
            }

            Spacer(Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF2E7D32))
                    }
                }
                uiState.error != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Text(
                            text = uiState.error!!,
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFFD32F2F)
                        )
                    }
                }
                uiState.productos.isEmpty() && termino.isNotBlank() -> {
                    Text(
                        "Sin resultados para \"$termino\"",
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(uiState.productos) { producto ->
                            ProductoResultadoCard(
                                producto = producto,
                                onAgregar = {
                                    navController.previousBackStackEntry?.savedStateHandle?.apply {
                                        set("busqueda_nombre", producto.nombre ?: "")
                                        set("busqueda_codigo", producto.codigo ?: "")
                                        set("busqueda_descripcion",
                                            listOfNotNull(producto.marca, producto.cantidad)
                                                .joinToString(" — ")
                                        )
                                    }
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductoResultadoCard(
    producto: ProductoDTO,
    onAgregar: () -> Unit
) {
    var mostrarDetalle by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Imagen del producto
                if (!producto.imagenUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = producto.imagenUrl,
                        contentDescription = "Imagen de ${producto.nombre}",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF5F5F5)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF5F5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sin\nimagen", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                // Información del producto
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = producto.nombre?.ifBlank { "Sin nombre" } ?: "Sin nombre",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF2E7D32),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!producto.marca.isNullOrBlank()) {
                        Text(
                            text = producto.marca,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!producto.cantidad.isNullOrBlank()) {
                            Text(
                                text = producto.cantidad,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }

                        // Nutri-Score badge
                        producto.nutriScore?.let { score ->
                            val scoreColor = when(score.uppercase()) {
                                "A" -> Color(0xFF00C851)
                                "B" -> Color(0xFF8BC34A)
                                "C" -> Color(0xFFFFD600)
                                "D" -> Color(0xFFFF9800)
                                "E" -> Color(0xFFFF5252)
                                else -> Color.Gray
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(scoreColor)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Nutri-Score: ${score.uppercase()}",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Información nutricional
            producto.nutriments?.let { nutriments ->
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "📊 Información Nutricional (por 100g/ml)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            nutriments.caloriasKcal?.let {
                                NutrientRow("🔥 Calorías", "${String.format("%.0f", it)} kcal")
                            }
                            nutriments.proteinas100g?.let {
                                NutrientRow("🥩 Proteínas", "${String.format("%.1f", it)} g")
                            }
                            nutriments.grasas100g?.let {
                                NutrientRow("🧈 Grasas", "${String.format("%.1f", it)} g")
                            }
                            nutriments.carbohidratos100g?.let {
                                NutrientRow("🍚 Carbohidratos", "${String.format("%.1f", it)} g")
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            nutriments.azucares100g?.let {
                                NutrientRow("🍬 Azúcares", "${String.format("%.1f", it)} g")
                            }
                            nutriments.fibra100g?.let {
                                NutrientRow("🌾 Fibra", "${String.format("%.1f", it)} g")
                            }
                            nutriments.sal100g?.let {
                                NutrientRow("🧂 Sal", "${String.format("%.2f", it)} g")
                            }
                        }
                    }

                    if (mostrarDetalle) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        if (!producto.ingredientes.isNullOrBlank()) {
                            Text(
                                text = "📋 Ingredientes:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF2E7D32)
                            )
                            Text(
                                text = producto.ingredientes,
                                fontSize = 10.sp,
                                color = Color.Gray,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(4.dp))
                        }

                        if (!producto.alergenos.isNullOrBlank()) {
                            Text(
                                text = "⚠️ Alérgenos:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFFFF5252)
                            )
                            Text(
                                text = producto.alergenos,
                                fontSize = 10.sp,
                                color = Color(0xFFFF5252)
                            )
                        }
                    }

                    TextButton(
                        onClick = { mostrarDetalle = !mostrarDetalle },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = if (mostrarDetalle) "Ver menos ▲" else "Ver más información ▼",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚠️ Sin información nutricional disponible",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // Botón volver
            HorizontalDivider(color = Color(0xFFE0E0E0))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    onClick = onAgregar,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF2E7D32)
                    )
                ) {
                    Text("← Volver sin agregar")
                }
            }
        }
    }
}

@Composable
private fun NutrientRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
