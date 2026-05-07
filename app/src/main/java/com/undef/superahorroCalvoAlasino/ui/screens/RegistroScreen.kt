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
import androidx.navigation.NavController
import com.undef.superahorroCalvoAlasino.navigation.NavRoutes
import com.undef.superahorroCalvoAlasino.viewmodel.UsuarioViewModel
import com.undef.superahorroCalvoAlasino.viewmodel.CompraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(navController: NavController, usuarioViewModel: UsuarioViewModel, @Suppress("UNUSED_PARAMETER") compraViewModel: CompraViewModel) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
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
                text = "Unite a Super Ahorro",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )

            Text(
                text = "Empezá a ahorrar en tus compras diarias",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
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

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; mensajeError = "" },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; mensajeError = "" },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; mensajeError = "" },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; mensajeError = "" },
                label = { Text("Confirmar contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    when {
                        nombre.isBlank() || email.isBlank() || password.isBlank() -> {
                            mensajeError = "Por favor completa todos los campos"
                        }
                        password != confirmPassword -> {
                            mensajeError = "Las contraseñas no coinciden"
                        }
                        password.length < 6 -> {
                            mensajeError = "La contraseña debe tener al menos 6 caracteres"
                        }
                        else -> {
                            usuarioViewModel.registrarUsuario(nombre, email, password)
                            // Mostrar mensaje de éxito
                            mensajeError = "¡Cuenta creada exitosamente! Ahora inicia sesión con tus credenciales"
                            // Limpiar los campos
                            nombre = ""
                            email = ""
                            password = ""
                            confirmPassword = ""
                            // Esperar un poco y navegar a Login
                            navController.navigate(NavRoutes.Login.route) {
                                popUpTo(NavRoutes.Registro.route) { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Registrarse", modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}
