package com.undef.superahorroCalvoAlasino.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.undef.superahorroCalvoAlasino.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilScreen(navController: NavController, usuarioViewModel: UsuarioViewModel) {
    val uiState by usuarioViewModel.uiState.collectAsStateWithLifecycle()
    val usuarioActual = uiState.usuario

    var nombre by remember { mutableStateOf(usuarioActual.nombre) }
    var email by remember { mutableStateOf(usuarioActual.email) }
    var passwordActual by remember { mutableStateOf("") }
    var nuevaPassword by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf("") }
    var errorPasswordActual by remember { mutableStateOf(false) }

    // Sincronizar campos del formulario cuando cambie el usuario en el estado
    LaunchedEffect(usuarioActual) {
        if (nombre.isEmpty() && email.isEmpty()) {
            nombre = usuarioActual.nombre
            email = usuarioActual.email
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Actualiza tu información",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )

            Spacer(Modifier.height(32.dp))

            if (mensajeError.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = mensajeError,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (mensajeExito.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Text(
                        text = mensajeExito,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Sección de Nombre
            Text(
                text = "Nombre",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; mensajeError = ""; mensajeExito = "" },
                label = { Text("Tu nombre") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(Modifier.height(24.dp))

            // Sección de Email
            Text(
                text = "Email",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; mensajeError = ""; mensajeExito = "" },
                label = { Text("Tu email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(Modifier.height(24.dp))

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Sección de Contraseña
            Text(
                text = "Cambiar Contraseña (Opcional)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )

            Text(
                text = "Si deseas cambiar tu contraseña, completa los campos a continuación",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = passwordActual,
                onValueChange = { 
                    passwordActual = it
                    mensajeError = ""
                    mensajeExito = ""
                    // Validar contraseña actual en tiempo real si intenta cambiar contraseña
                    if (nuevaPassword.isNotEmpty() && it.isNotEmpty()) {
                        errorPasswordActual = it != usuarioActual.password
                    } else {
                        errorPasswordActual = false
                    }
                },
                label = { Text("Contraseña actual") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = MaterialTheme.shapes.medium,
                isError = errorPasswordActual
            )

            if (errorPasswordActual) {
                Text(
                    text = "❌ La contraseña actual es incorrecta",
                    color = Color(0xFFD32F2F),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 4.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = nuevaPassword,
                onValueChange = { nuevaPassword = it; mensajeError = ""; mensajeExito = "" },
                label = { Text("Nueva contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmarPassword,
                onValueChange = { confirmarPassword = it; mensajeError = ""; mensajeExito = "" },
                label = { Text("Confirmar nueva contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(Modifier.height(32.dp))

            val puedeGuardar = when {
                nombre.isBlank() || email.isBlank() -> false
                passwordActual.isNotEmpty() && nuevaPassword.isEmpty() -> false
                nuevaPassword.isNotEmpty() && confirmarPassword.isEmpty() -> false
                nuevaPassword != confirmarPassword && nuevaPassword.isNotEmpty() -> false
                errorPasswordActual -> false
                nuevaPassword.isNotEmpty() && nuevaPassword.length < 6 -> false
                else -> true
            }

            Button(
                onClick = {
                    when {
                        nombre.isBlank() || email.isBlank() -> {
                            mensajeError = "El nombre y email son requeridos"
                        }
                        passwordActual.isNotEmpty() && nuevaPassword.isEmpty() -> {
                            mensajeError = "Si completas la contraseña actual, debes completar la nueva"
                        }
                        nuevaPassword.isNotEmpty() && confirmarPassword.isEmpty() -> {
                            mensajeError = "Debes confirmar la nueva contraseña"
                        }
                        nuevaPassword != confirmarPassword && nuevaPassword.isNotEmpty() -> {
                            mensajeError = "Las nuevas contraseñas no coinciden"
                        }
                        passwordActual.isNotEmpty() && passwordActual != usuarioActual.password -> {
                            errorPasswordActual = true
                            mensajeError = "La contraseña actual es incorrecta"
                        }
                        nuevaPassword.isNotEmpty() && nuevaPassword.length < 6 -> {
                            mensajeError = "La nueva contraseña debe tener al menos 6 caracteres"
                        }
                        else -> {
                            val passwordFinal = if (nuevaPassword.isNotEmpty()) nuevaPassword else usuarioActual.password
                            usuarioViewModel.actualizarPerfil(nombre, email, passwordFinal)
                            mensajeExito = "Perfil actualizado correctamente"
                            passwordActual = ""
                            nuevaPassword = ""
                            confirmarPassword = ""
                            errorPasswordActual = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                enabled = puedeGuardar
            ) {
                Text("Guardar Cambios", fontSize = MaterialTheme.typography.titleSmall.fontSize)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}


