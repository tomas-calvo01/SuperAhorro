package com.undef.superahorroCalvoAlasino.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.undef.superahorroCalvoAlasino.R
import com.undef.superahorroCalvoAlasino.ui.components.BottomNavBar
import com.undef.superahorroCalvoAlasino.viewmodel.CompraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(navController: NavController, compraViewModel: CompraViewModel) {
    val uiState by compraViewModel.uiState.collectAsStateWithLifecycle()
    val compras = uiState.compras
    val totalGastado = compraViewModel.calcularTotalGastado()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (compras.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Text(
                        "Sin datos de compras para mostrar estadísticas",
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Gasto Total
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Total Gastado", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "$ ${"%.2f".format(totalGastado)}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Text("En ${compras.size} compra(s)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }

                // Gasto por Supermercado
                val gastosPorSupermercado = compras.groupBy { it.supermercado }
                    .mapValues { (_, comprasDelSuper) -> comprasDelSuper.sumOf { it.total } }
                    .toList()
                    .sortedByDescending { it.second }

                if (gastosPorSupermercado.isNotEmpty()) {
                    Text(
                        "Gastos por Supermercado",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // Gráfico Circular
                            PieChart(
                                data = gastosPorSupermercado,
                                totalGastado = totalGastado,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .size(280.dp)
                            )

                            Spacer(Modifier.height(32.dp))

                            // Leyenda profesional
                            Text(
                                "Desglose por Supermercado",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF212121),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            gastosPorSupermercado.forEachIndexed { index, (supermercado, gasto) ->
                                val porcentaje = (gasto / totalGastado * 100)
                                val color = getColorForIndex(index)

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Indicador de color
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .background(color, RoundedCornerShape(6.dp))
                                        )
                                        
                                        // Información
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                supermercado,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                            Text(
                                                "$ ${"%.2f".format(gasto)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                        
                                        // Porcentaje destacado
                                        Box(
                                            modifier = Modifier
                                                .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                "${"%.1f".format(porcentaje)}%",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = color
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Producto más comprado
                val productoMasComprado = compras.flatMap { it.productos }
                    .groupBy { it.nombre }
                    .maxByOrNull { it.value.size }

                if (productoMasComprado != null) {
                    Text(
                        "Producto más comprado",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(Color(0xFF1976D2), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "🛒",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    productoMasComprado.key,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF212121)
                                )
                                Text(
                                    "Comprado ${productoMasComprado.value.size} veces",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                // Promedio de gasto
                val promedioGasto = totalGastado / compras.size
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            stringResource(R.string.estadisticas_promedio),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "$ ${"%.2f".format(promedioGasto)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6F00)
                        )
                    }
                }

                // Evolución mensual (gráfico de barras)
                val gastosPorMes = compras
                    .groupBy { compra ->
                        // Formato esperado DD/MM/YYYY — tomamos MM/YYYY
                        val partes = compra.fecha.split("/")
                        if (partes.size >= 3) "${partes[1]}/${partes[2]}" else compra.fecha
                    }
                    .mapValues { (_, c) -> c.sumOf { it.total } }
                    .toList()
                    .sortedBy { (mes, _) ->
                        val p = mes.split("/")
                        if (p.size == 2) (p[1] + p[0].padStart(2, '0')) else mes
                    }

                if (gastosPorMes.size >= 2) {
                    Text(
                        stringResource(R.string.estadisticas_evolucion_mensual),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            BarChart(
                                data = gastosPorMes,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PieChart(
    data: List<Pair<String, Double>>,
    totalGastado: Double,
    modifier: Modifier = Modifier
) {
    val total = data.sumOf { it.second }
    val colors = data.indices.map { getColorForIndex(it) }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2.3f
            val centerX = size.width / 2
            val centerY = size.height / 2

            var currentAngle = -90f

            data.forEachIndexed { index, (_, value) ->
                val sweepAngle = (value.toFloat() / total.toFloat()) * 360f
                
                // Dibujar segmento del gráfico
                drawArc(
                    color = colors[index],
                    startAngle = currentAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(centerX - radius, centerY - radius),
                    size = Size(radius * 2, radius * 2)
                )
                
                // Dibujar borde del segmento
                drawArc(
                    color = Color.White,
                    startAngle = currentAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(centerX - radius, centerY - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = 2f)
                )

                currentAngle += sweepAngle
            }

            // Círculo blanco en el centro para efecto donut
            drawCircle(
                color = Color.White,
                radius = radius * 0.6f,
                center = Offset(centerX, centerY)
            )
        }

        // Texto central
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Total",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                "$ ${"%.2f".format(totalGastado)}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2E7D32)
            )
        }
    }
}

fun getColorForIndex(index: Int): Color {
    val colors = listOf(
        Color(0xFF2E7D32),
        Color(0xFF1976D2),
        Color(0xFFD32F2F),
        Color(0xFFF57C00),
        Color(0xFF7B1FA2),
        Color(0xFF0097A7),
        Color(0xFF388E3C),
        Color(0xFFC2185B),
    )
    return colors[index % colors.size]
}

@Composable
fun BarChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return
    val maxValue = data.maxOf { it.second }.takeIf { it > 0 } ?: 1.0
    val barColor = Color(0xFF2E7D32)

    Column(modifier = modifier) {
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val barCount = data.size
            val totalWidth = size.width
            val totalHeight = size.height
            val barWidth = (totalWidth / barCount) * 0.55f
            val gap = (totalWidth / barCount) * 0.45f
            val totalGasto = data.sumOf { it.second }

            data.forEachIndexed { index, (_, value) ->
                val barHeight = ((value / maxValue) * totalHeight).toFloat()
                val left = index * (barWidth + gap) + gap / 2f
                val top = totalHeight - barHeight

                drawRect(
                    color = barColor,
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight)
                )
                
                // Dibujar porcentaje dentro de la barra
                val percentage = (value / totalGasto * 100).toInt()
                if (percentage > 0 && barHeight > 40f) {
                    val paint = android.graphics.Paint().apply {
                        color = Color.White.toArgb()
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 32f
                        isFakeBoldText = true
                    }
                    drawContext.canvas.nativeCanvas.drawText(
                        "$percentage%",
                        left + barWidth / 2f,
                        top + barHeight / 2f + 12f,
                        paint
                    )
                }
            }
        }

        // Labels del eje X
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            data.forEach { (label, _) ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

