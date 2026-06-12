package com.undef.superahorroCalvoAlasino.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.undef.superahorroCalvoAlasino.R
import com.undef.superahorroCalvoAlasino.model.Producto
import com.undef.superahorroCalvoAlasino.navigation.NavRoutes
import com.undef.superahorroCalvoAlasino.viewmodel.CompraViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private fun crearArchivoFoto(context: Context): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    return File(context.externalCacheDir ?: context.cacheDir, "TICKET_$timestamp.jpg")
}

private fun getUriParaFoto(context: Context, archivo: File): Uri =
    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCompraScreen(
    navController: NavController,
    compraId: Int,
    compraViewModel: CompraViewModel
) {
    val compra = compraViewModel.obtenerCompra(compraId)
    val context = LocalContext.current
    var mostrarMenu by remember { mutableStateOf(false) }
    var mostrarDialogoEliminarCompra by remember { mutableStateOf(false) }
    var mostrarDialogoEliminarProducto by remember { mutableStateOf<Producto?>(null) }
    var productoAEditar by remember { mutableStateOf<Producto?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val mensajeRegistro by compraViewModel.mensajeRegistroRemoto.collectAsStateWithLifecycle()

    // URI temporal para la foto de cámara
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            // Persistir permiso de lectura para el URI
            context.contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            compraViewModel.actualizarTicketUri(compraId, it.toString())
        }
    }

    // Launcher para cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraUri?.let { compraViewModel.actualizarTicketUri(compraId, it.toString()) }
        }
    }

    LaunchedEffect(mensajeRegistro) {
        mensajeRegistro?.let {
            snackbarHostState.showSnackbar(it)
            compraViewModel.limpiarMensajeRegistro()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("${stringResource(R.string.detalle_titulo)} #${compra?.id ?: compraId}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.btn_volver))
                    }
                },
                actions = {
                    if (compra != null) {
                        // Ícono de búsqueda para networking
                        IconButton(onClick = { navController.navigate(NavRoutes.BuscarProducto.withId(compraId)) }) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar producto", tint = Color.White)
                        }
                        // Menú de 3 puntos
                        IconButton(onClick = { mostrarMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.detalle_mas_opciones), tint = Color.White)
                        }
                        DropdownMenu(expanded = mostrarMenu, onDismissRequest = { mostrarMenu = false }) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.btn_compartir)) },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF2E7D32)) },
                                onClick = {
                                    val texto = buildString {
                                        append("🛒 Compra en ${compra.supermercado}\n")
                                        append("📅 ${compra.fecha} - ${compra.hora}\n")
                                        append("💰 Total: $${"%.2f".format(compra.total)}\n")
                                        append("📦 ${compra.productos.size} producto${if (compra.productos.size != 1) "s" else ""}\n\n")
                                        if (compra.productos.isNotEmpty()) {
                                            append("Productos:\n")
                                            compra.productos.forEach { append("• ${it.nombre} - $${"%.2f".format(it.precio)}\n") }
                                            append("\n")
                                        }
                                        append(context.getString(R.string.msg_compartido_desde))
                                    }
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, texto)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.detalle_compartir_via)))
                                    mostrarMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.btn_sincronizar)) },
                                leadingIcon = { Icon(Icons.Default.Sync, contentDescription = null, tint = Color(0xFF2E7D32)) },
                                onClick = { compraViewModel.registrarCompraRemota(compra); mostrarMenu = false }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.btn_eliminar), color = Color(0xFFD32F2F)) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFD32F2F)) },
                                onClick = { mostrarDialogoEliminarCompra = true; mostrarMenu = false }
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
        }
    ) { padding ->
        if (compra == null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(stringResource(R.string.detalle_compra_no_encontrada), style = MaterialTheme.typography.titleLarge, color = Color.Gray)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Resumen de la compra
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.detalle_supermercado), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Text(compra.supermercado, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(stringResource(R.string.detalle_fecha), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Text(compra.fecha, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                            Column {
                                Text(stringResource(R.string.detalle_hora), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Text(compra.hora, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(stringResource(R.string.detalle_total), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Text(
                            "$ ${compra.total}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }

                // Sección ticket de compra
                TicketSection(
                    ticketUri = compra.ticketImageUri,
                    onTomarFoto = {
                        val archivo = crearArchivoFoto(context)
                        val uri = getUriParaFoto(context, archivo)
                        cameraUri = uri
                        cameraLauncher.launch(uri)
                    },
                    onElegirGaleria = {
                        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    onEliminarImagen = {
                        compraViewModel.actualizarTicketUri(compraId, null)
                    }
                )

                Spacer(Modifier.height(16.dp))

                // Productos
                Text(
                    "${stringResource(R.string.detalle_productos)} (${compra.productos.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (compra.productos.isEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            stringResource(R.string.detalle_sin_productos),
                            modifier = Modifier.padding(24.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        compra.productos.forEach { producto ->
                            ProductoCard(
                                producto = producto,
                                onEditar = { productoAEditar = producto },
                                onEliminar = { mostrarDialogoEliminarProducto = producto }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total de compra:", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                Text("${compra.productos.size} producto${if (compra.productos.size != 1) "s" else ""}", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
                            }
                            Text("$ ${"%.2f".format(compra.total)}", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.headlineSmall, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Diálogo eliminar compra
    if (mostrarDialogoEliminarCompra && compra != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminarCompra = false },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFD32F2F)) },
            title = { Text(stringResource(R.string.detalle_eliminar_titulo), fontWeight = FontWeight.Bold) },
            text = { Text("${stringResource(R.string.accion_no_reversible)} ${stringResource(R.string.detalle_eliminar_pregunta)}") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoEliminarCompra = false
                        compraViewModel.eliminarCompra(compra)
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) { Text(stringResource(R.string.btn_eliminar)) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminarCompra = false }) { Text(stringResource(R.string.btn_cancelar)) }
            }
        )
    }

    // Diálogo eliminar producto
    mostrarDialogoEliminarProducto?.let { producto ->
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminarProducto = null },
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFD32F2F)) },
            title = { Text(stringResource(R.string.detalle_eliminar_producto_titulo), fontWeight = FontWeight.Bold) },
            text = { Text("${stringResource(R.string.accion_no_reversible)} ${stringResource(R.string.detalle_eliminar_producto_pregunta)}") },
            confirmButton = {
                Button(
                    onClick = {
                        compraViewModel.eliminarProducto(producto.id, compraId)
                        mostrarDialogoEliminarProducto = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) { Text(stringResource(R.string.btn_eliminar)) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminarProducto = null }) { Text(stringResource(R.string.btn_cancelar)) }
            }
        )
    }

    // Diálogo editar producto
    productoAEditar?.let { producto ->
        EditarProductoDialog(
            producto = producto,
            onDismiss = { productoAEditar = null },
            onGuardar = { productoEditado ->
                compraViewModel.actualizarProducto(productoEditado, compraId)
                productoAEditar = null
            }
        )
    }
}

@Composable
private fun ProductoCard(
    producto: Producto,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(producto.nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF2E7D32))
                }
                Row {
                    IconButton(onClick = onEditar, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar producto", tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onEliminar, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar producto", tint = Color(0xFFD32F2F), modifier = Modifier.size(18.dp))
                    }
                }
                Text("$${String.format("%.2f", producto.precio)}", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.headlineSmall, color = Color(0xFF2E7D32))
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE0E0E0))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.producto_codigo), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(if (producto.codigo.isEmpty()) "N/A" else producto.codigo, style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
            }
            if (producto.descripcion.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(stringResource(R.string.producto_descripcion), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(producto.descripcion, style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32))
            }
        }
    }
}

@Composable
private fun TicketSection(
    ticketUri: String?,
    onTomarFoto: () -> Unit,
    onElegirGaleria: () -> Unit,
    onEliminarImagen: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBE7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Receipt, contentDescription = null, tint = Color(0xFF558B2F), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.detalle_ticket_titulo), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF558B2F))
            }
            Spacer(Modifier.height(12.dp))

            if (ticketUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEEEEEE))
                ) {
                    AsyncImage(
                        model = Uri.parse(ticketUri),
                        contentDescription = "Ticket de compra",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onEliminarImagen,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.detalle_ticket_eliminar))
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEEEEEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.detalle_ticket_sin_imagen), color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onTomarFoto,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF558B2F))
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.detalle_ticket_tomar_foto), style = MaterialTheme.typography.bodySmall)
                    }
                    OutlinedButton(
                        onClick = onElegirGaleria,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Photo, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.detalle_ticket_galeria), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun EditarProductoDialog(
    producto: Producto,
    onDismiss: () -> Unit,
    onGuardar: (Producto) -> Unit
) {
    var nombre by remember { mutableStateOf(producto.nombre) }
    var codigo by remember { mutableStateOf(producto.codigo) }
    var descripcion by remember { mutableStateOf(producto.descripcion) }
    var precio by remember { mutableStateOf(producto.precio.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Producto", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text(stringResource(R.string.nuevo_producto_nombre)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = codigo,
                    onValueChange = { codigo = it },
                    label = { Text(stringResource(R.string.nuevo_producto_codigo)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text(stringResource(R.string.nuevo_producto_descripcion)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text(stringResource(R.string.nuevo_producto_precio)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isNotBlank()) {
                        onGuardar(
                            producto.copy(
                                nombre = nombre.trim(),
                                codigo = codigo.trim(),
                                descripcion = descripcion.trim(),
                                precio = precio.toDoubleOrNull() ?: producto.precio
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) { Text(stringResource(R.string.btn_guardar)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_cancelar)) }
        }
    )
}
