package com.undef.superahorroCalvoAlasino.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.undef.superahorroCalvoAlasino.model.Compra
import com.undef.superahorroCalvoAlasino.model.FiltroCompra
import com.undef.superahorroCalvoAlasino.model.OrdenCompra
import com.undef.superahorroCalvoAlasino.ui.components.BottomNavBar
import com.undef.superahorroCalvoAlasino.viewmodel.CompraViewModel
import java.io.File

private fun generarYCompartirCsv(context: Context, compras: List<Compra>) {
    val csv = buildString {
        appendLine("ID,Supermercado,Fecha,Hora,Total,Cantidad Productos,Productos")
        compras.forEach { compra ->
            val productos = compra.productos.joinToString(";") { it.nombre }
            appendLine(
                "${compra.id},\"${compra.supermercado}\",${compra.fecha},${compra.hora}," +
                "${"%.2f".format(compra.total)},${compra.productos.size},\"$productos\""
            )
        }
    }
    val archivo = File(context.cacheDir, "historial_compras.csv")
    archivo.writeText(csv)
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_SUBJECT, "Historial de compras - SuperAhorro")
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Exportar historial via"))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(navController: NavController, compraViewModel: CompraViewModel) {
    val comprasFiltradas by compraViewModel.comprasFiltradas.collectAsStateWithLifecycle()
    val filtro by compraViewModel.filtro.collectAsStateWithLifecycle()
    var compraExpandidaId by remember { mutableStateOf<Int?>(null) }
    var mostrarFiltros by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val filtroActivo = filtro != FiltroCompra()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Compras") },
                actions = {
                    if (comprasFiltradas.isNotEmpty()) {
                        IconButton(onClick = { generarYCompartirCsv(context, comprasFiltradas) }) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Exportar CSV",
                                tint = LocalContentColor.current
                            )
                        }
                    }
                    BadgedBox(
                        badge = { if (filtroActivo) Badge() }
                    ) {
                        IconButton(onClick = { mostrarFiltros = !mostrarFiltros }) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filtros",
                                tint = if (filtroActivo) Color(0xFF2E7D32) else LocalContentColor.current
                            )
                        }
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedVisibility(visible = mostrarFiltros) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Filtros",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = filtro.supermercado,
                            onValueChange = { compraViewModel.setFiltro(filtro.copy(supermercado = it)) },
                            label = { Text("Supermercado") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = filtro.fechaDesde,
                                onValueChange = { compraViewModel.setFiltro(filtro.copy(fechaDesde = it)) },
                                label = { Text("Fecha desde") },
                                placeholder = { Text("DD/MM/YYYY") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = filtro.fechaHasta,
                                onValueChange = { compraViewModel.setFiltro(filtro.copy(fechaHasta = it)) },
                                label = { Text("Fecha hasta") },
                                placeholder = { Text("DD/MM/YYYY") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = filtro.montoMin,
                                onValueChange = { compraViewModel.setFiltro(filtro.copy(montoMin = it)) },
                                label = { Text("Monto mín $") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                            OutlinedTextField(
                                value = filtro.montoMax,
                                onValueChange = { compraViewModel.setFiltro(filtro.copy(montoMax = it)) },
                                label = { Text("Monto máx $") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }

                        Text("Ordenar por", style = MaterialTheme.typography.labelMedium, color = Color.Gray)

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            FilterChip(
                                selected = filtro.orden == OrdenCompra.MAS_RECIENTE,
                                onClick = { compraViewModel.setFiltro(filtro.copy(orden = OrdenCompra.MAS_RECIENTE)) },
                                label = { Text(OrdenCompra.MAS_RECIENTE.etiqueta, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = filtro.orden == OrdenCompra.MAS_ANTIGUO,
                                onClick = { compraViewModel.setFiltro(filtro.copy(orden = OrdenCompra.MAS_ANTIGUO)) },
                                label = { Text(OrdenCompra.MAS_ANTIGUO.etiqueta, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            FilterChip(
                                selected = filtro.orden == OrdenCompra.MAYOR_MONTO,
                                onClick = { compraViewModel.setFiltro(filtro.copy(orden = OrdenCompra.MAYOR_MONTO)) },
                                label = { Text(OrdenCompra.MAYOR_MONTO.etiqueta, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = filtro.orden == OrdenCompra.MENOR_MONTO,
                                onClick = { compraViewModel.setFiltro(filtro.copy(orden = OrdenCompra.MENOR_MONTO)) },
                                label = { Text(OrdenCompra.MENOR_MONTO.etiqueta, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (filtroActivo) {
                            TextButton(
                                onClick = { compraViewModel.limpiarFiltro() },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Limpiar filtros", color = Color(0xFF2E7D32))
                            }
                        }
                    }
                }
            }

            if (filtroActivo) {
                Text(
                    "${comprasFiltradas.size} resultado${if (comprasFiltradas.size != 1) "s" else ""}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            if (comprasFiltradas.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (filtroActivo) "Sin resultados para los filtros aplicados"
                        else "No hay compras registradas",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(comprasFiltradas) { compra ->
                        val estaExpandida = compraExpandidaId == compra.id

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    compraExpandidaId = if (estaExpandida) null else compra.id
                                }
                                .background(Color.White, RoundedCornerShape(12.dp)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            compra.supermercado,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = Color(0xFF2E7D32)
                                        )
                                        Text(
                                            "${compra.fecha} - ${compra.hora}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            "$ ${"%.2f".format(compra.total)}",
                                            fontWeight = FontWeight.ExtraBold,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = Color(0xFF2E7D32)
                                        )
                                        Icon(
                                            imageVector = if (estaExpandida) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = Color.Gray
                                        )
                                    }
                                }

                                if (estaExpandida && compra.productos.isNotEmpty()) {
                                    Spacer(Modifier.height(12.dp))
                                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                                    Spacer(Modifier.height(12.dp))

                                    Text(
                                        "Productos (${compra.productos.size})",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray
                                    )

                                    Spacer(Modifier.height(8.dp))

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        compra.productos.forEach { producto ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                                    .padding(12.dp),
                                                horizontalArrangement = Arrangement.Start,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    producto.nombre,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF2E7D32)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
