# SuperAhorro - Primera Entrega

## Información General del Proyecto

**Nombre del Proyecto:** SuperAhorro

**Materia:** Tecnologías Móviles - 2026

**Integrantes del Grupo:**
- Tomás Calvo
- Benjamín Alasino

**Fecha de Entrega:** Primera Entrega

---

## Descripción del Proyecto

SuperAhorro es una aplicación móvil Android desarrollada en Kotlin que permite a los usuarios gestionar sus gastos de supermercado de manera organizada y eficiente. La aplicación facilita el registro de compras con múltiples productos, visualización del historial, análisis de gastos mediante gráficos estadísticos y seguimiento del presupuesto para identificar oportunidades de ahorro.

## Objetivo de la Primera Entrega

Desarrollar la interfaz visual completa de la aplicación Android con todas las pantallas principales, sistema de navegación funcional y estructura base del proyecto. Esta entrega incluye el diseño general de la app, navegación entre pantallas y datos mockeados para demostrar la funcionalidad de la interfaz.

## Características Implementadas

### 1. Autenticación de Usuarios
- Pantalla Splash de bienvenida con animación de 2 segundos
- Pantalla de Login con validación de credenciales
- Pantalla de Registro para crear nuevas cuentas
- Flujo de cierre de sesión
- Datos de usuario persistidos localmente

### 2. Gestión de Compras
- Registro de nuevas compras con datos básicos:
  - Supermercado
  - Fecha
  - Hora
  - Total de gasto
- Listado de últimas compras en la pantalla principal
- Detalle completo de cada compra con todos sus productos
- Historial ordenado por fecha de compra

### 3. Gestión de Productos
- Agregación de múltiples productos por compra
- Cada producto incluye:
  - ID del producto
  - Código
  - Nombre
  - Descripción
  - Precio unitario
- Cálculo automático del total de compra

### 4. Análisis y Estadísticas
- Gráfico circular que muestra gastos por supermercado
- Cálculo de gasto total acumulado
- Promedio de gasto por compra
- Supermercado más frecuentado
- Compra más cara registrada
- Visualización profesional de indicadores de gastos

### 5. Perfil de Usuario
- Pantalla de perfil con información del usuario
- Edición de datos de usuario (nombre, email, contraseña)
- Validación de cambios de contraseña
- Cierre de sesión seguro

### 6. Navegación e Interfaz
- Sistema de navegación fluida entre pantallas
- Barra de navegación inferior con acceso a principales secciones
- Diseño consistente con tema de color verde
- Componentes Material Design 3

---

## Pantallas Desarrolladas

1. Splash Screen - Pantalla de bienvenida animada
2. Login - Autenticación de usuarios
3. Registro - Creación de nuevas cuentas
4. Home - Pantalla principal con resumen de gastos
5. Nueva Compra - Formulario para registrar compras
6. Detalle Compra - Vista completa de una compra
7. Historial - Listado de todas las compras
8. Estadísticas - Gráficos de análisis de gastos
9. Mi Perfil - Información y configuración de usuario
10. Editar Perfil - Modificación de datos
11. Configuración - Pantalla de ajustes

---

## Tecnologías Utilizadas

### Lenguaje y Entorno
- Kotlin
- Android SDK (API 24 - 36)
- Android Studio

### Framework y Librerías
- Jetpack Compose para construcción de interfaz
- Material Design 3
- Material Icons Extended

### Arquitectura
- Patrón MVVM (Model-View-ViewModel)
- ViewModels para gestión de estado
- Separación de responsabilidades

### Navegación
- Navigation Compose
- NavHost y composable
- Paso de argumentos con navArgument

### Persistencia
- SharedPreferences para datos de usuario
- JSON para serialización de compras
- Almacenamiento local por usuario

### Asincronía
- Corrutinas de Kotlin
- LaunchedEffect para operaciones temporales

---

## Estructura del Proyecto

```
app/src/main/java/com/undef/superahorroCalvoAlasino/
├── MainActivity.kt
├── navigation/
│   ├── AppNavGraph.kt
│   └── NavRoutes.kt
├── ui/
│   ├── screens/
│   │   ├── SplashScreen.kt
│   │   ├── LoginScreen.kt
│   │   ├── RegistroScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── NuevaCompraScreen.kt
│   │   ├── NuevoProductoScreen.kt
│   │   ├── DetalleCompraScreen.kt
│   │   ├── HistorialScreen.kt
│   │   ├── EstadisticasScreen.kt
│   │   ├── PerfilScreen.kt
│   │   ├── EditarPerfilScreen.kt
│   │   └── SettingsScreen.kt
│   ├── components/
│   │   └── BottomNavBar.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── viewmodel/
│   ├── CompraViewModel.kt
│   └── UsuarioViewModel.kt
└── model/
    └── Models.kt
```

---

## Modelos de Datos

### Usuario
- nombre: String
- email: String
- password: String

### Compra
- id: Int
- supermercado: String
- fecha: String
- hora: String
- total: Double
- productos: List<Producto>

### Producto
- id: Int
- codigo: String
- nombre: String
- descripcion: String
- precio: Double

---

## Requisitos Cumplidos para Primera Entrega

### Requisitos Funcionales
- Pantalla de bienvenida (Splash)
- Flujo completo de registro e inicio de sesión
- Edición de datos de usuario
- Registro de compras con supermercado, fecha, hora y total
- Registro de productos con múltiples campos por compra
- Listado de últimas compras
- Historial completo de compras
- Estadísticas con gráficos de gastos
- Pantalla de perfil y configuración

### Requisitos Técnicos
- Activity (MainActivity con ComponentActivity)
- Jetpack Compose (100% de la interfaz)
- Navigation Compose (Sistema completo)
- Persistencia (SharedPreferences)
- Corrutinas (LaunchedEffect y delay)
- Arquitectura MVVM (ViewModels implementados)
- Código organizado (Separación clara de capas)

### Elementos Visuales y de Navegación
- Todas las pantallas principales implementadas
- Navegación fluida entre pantallas
- Barra de navegación inferior funcional
- Datos mockeados disponibles
- Estructura visual consistente

---

## Configuración y Ejecución

### Requisitos del Sistema
- Android Studio Jellyfish o posterior
- SDK Android 36
- Mínimo SDK 24 (Android 7.0)
- Java Development Kit (JDK)

### Pasos para Ejecutar
1. Clonar el repositorio: https://github.com/tomas-calvo01/SuperAhorro
2. Abrir el proyecto en Android Studio
3. Esperar a que sincronice las dependencias de Gradle
4. Conectar un dispositivo Android o abrir un emulador
5. Presionar el botón Run (Ctrl+R) en Android Studio

### Compilación de APK
1. En Android Studio: Build > Build Bundle(s) / APK(s) > Build APK(s)
2. El APK se genera en: app/build/outputs/apk/debug/
3. Transferir el APK al dispositivo e instalar

---

## Dependencias Principales

- androidx.core:core-ktx
- androidx.lifecycle:lifecycle-runtime-ktx
- androidx.activity:activity-compose
- androidx.compose.ui:ui
- androidx.compose.material3:material3
- androidx.navigation:navigation-compose
- androidx.lifecycle:lifecycle-viewmodel-compose
- androidx.compose.material:material-icons-extended

---

## Flujo de Uso de la Aplicación

1. Inicio: Usuario ve Splash Screen por 2 segundos
2. Autenticación: Es dirigido a Login o puede registrarse
3. Acceso: Ingresa credenciales para acceder a la app
4. Home: Visualiza resumen de gastos y últimas compras
5. Nuevo Registro: Puede crear una nueva compra desde el botón flotante
6. Agregar Productos: Define supermercado, fecha, hora y agrega productos
7. Visualización: Ve el historial completo de compras
8. Estadísticas: Consulta gráficos de gastos por supermercado
9. Perfil: Accede a información y puede editar datos
10. Cierre: Puede cerrar sesión en cualquier momento

---

## Persistencia de Datos

Los datos se almacenan localmente mediante:

### SharedPreferences
- Información del usuario (nombre, email, contraseña)
- Estado de sesión

### JSON en SharedPreferences
- Compras serializado en formato JSON
- Productos asociados a cada compra
- Historial completo por usuario
- Datos persistentes incluso después de cerrar la aplicación

---

## Validaciones Implementadas

- Validación de campos vacíos en registro y login
- Verificación de credenciales correctas
- Control de existencia de usuario
- Validación de datos obligatorios en compras
- Manejo de excepciones en operaciones de persistencia
- Separación de datos por usuario

---

## Notas Técnicas

- El código sigue las buenas prácticas de Android
- Estructura modular y reutilizable
- Separación clara entre capas (UI, ViewModel, Model)
- Nombres descriptivos de variables y funciones
- Tema visual coherente en toda la aplicación
- Navegación intuitiva y fluida

---

## Información Adicional

Repositorio GitHub: https://github.com/tomas-calvo01/SuperAhorro

Nombre del Paquete: com.undef.superahorroCalvoAlasino

Versión de la Aplicación: 1.0

Estado del Proyecto: Primera Entrega Completada

Última Actualización: Mayo 2026

---

## Integrantes

- Tomás Calvo
- Benjamín Alasino

Tecnologías Móviles - 2026
