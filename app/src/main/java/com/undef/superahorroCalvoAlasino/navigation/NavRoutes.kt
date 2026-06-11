package com.undef.superahorroCalvoAlasino.navigation

sealed class NavRoutes(val route: String) {
    object Splash : NavRoutes("splash")
    object Login : NavRoutes("login")
    object Registro : NavRoutes("registro")
    object Home : NavRoutes("home")
    object NuevaCompra : NavRoutes("nueva_compra")
    object NuevoProducto : NavRoutes("nuevo_producto/{compraId}") {
        fun withId(id: Int) = "nuevo_producto/$id"
    }
    object DetalleCompra : NavRoutes("detalle_compra/{compraId}") {
        fun withId(id: Int) = "detalle_compra/$id"
    }
    object Historial : NavRoutes("historial")
    object Estadisticas : NavRoutes("estadisticas")
    object Perfil : NavRoutes("perfil")
    object EditarPerfil : NavRoutes("editar_perfil")
    object Settings : NavRoutes("settings")
    object BuscarProducto : NavRoutes("buscar_producto/{compraId}") {
        fun withId(id: Int) = "buscar_producto/$id"
    }
    object OlvidoContrasena : NavRoutes("olvido_contrasena")
}

