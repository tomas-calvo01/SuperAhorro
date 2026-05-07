package com.undef.superahorroCalvoAlasino.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.undef.superahorroCalvoAlasino.navigation.NavRoutes
import com.undef.superahorroCalvoAlasino.ui.components.BottomNavBar
import com.undef.superahorroCalvoAlasino.viewmodel.UsuarioViewModel
import com.undef.superahorroCalvoAlasino.viewmodel.CompraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavController, usuarioViewModel: UsuarioViewModel, compraViewModel: CompraViewModel) {
    val usuario = usuarioViewModel.obtenerUsuario()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mi Perfil") })
        },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar Placeholder
            Surface(
                modifier = Modifier.size(100.dp).clip(CircleShape),
                color = Color(0xFFE8F5E9)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp).padding(16.dp),
                    tint = Color(0xFF2E7D32)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(usuario.nombre.ifEmpty { "Usuario" }, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(usuario.email.ifEmpty { "email@example.com" }, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            Spacer(Modifier.height(32.dp))

            OutlinedButton(
                onClick = { navController.navigate(NavRoutes.EditarPerfil.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Editar Datos")
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    // Limpiar los datos de compras antes de cerrar sesión
                    compraViewModel.limpiarCompras()
                    usuarioViewModel.cerrarSesion()
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }
}

