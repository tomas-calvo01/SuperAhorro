# SuperAhorro 🛒

Aplicación Android de gestión de compras de supermercado con análisis de gastos, búsqueda de información nutricional y sincronización de datos.

**Trabajo Práctico Integrador — Tecnologías Móviles 2026**  
**Defensa Final**  
Autores: Calvo · Alasino

---

## 📋 Descripción del Proyecto

**SuperAhorro** es una aplicación móvil desarrollada en Kotlin con Jetpack Compose que permite a los usuarios llevar un control detallado de sus compras de supermercado. La aplicación implementa arquitectura MVVM, persistencia local con Room, integración con APIs externas mediante Retrofit, y un sistema de caché inteligente para optimizar el rendimiento.

### ✨ Funcionalidades Principales

#### 🔐 Gestión de Usuarios
- Registro e inicio de sesión con persistencia en DataStore
- Recuperación de contraseña
- Perfil de usuario editable
- Sesión persistente entre reinicios de la app

#### 🛒 Gestión de Compras
- **Registro de compras** con datos completos: supermercado, fecha, hora, total
- **CRUD completo** de productos dentro de cada compra
- **Adjuntar foto del ticket** mediante:
  - Cámara (ActivityResultContracts.TakePicture)
  - Galería (ActivityResultContracts.PickVisualMedia)
- **Sincronización automática** con servidor externo (POST + Room)
- **Single Source of Truth**: Room como fuente única de datos

#### 🔍 Búsqueda de Productos (Integración API)
- **Búsqueda en OpenFoodFacts API** (base de datos con +800,000 productos)
- **Sistema de caché inteligente** (7 días de validez)
- Consulta información nutricional: Nutri-Score, calorías, grasas, sal, azúcar
- Visualización de ingredientes, alérgenos e imágenes reales
- **Flujo GET con Room**: API → Room → UI (patrón optimizado)

#### 📊 Estadísticas y Análisis
- Gasto total acumulado
- **Gráfico de torta** (donut chart): distribución por supermercado
- **Gráfico de barras**: evolución mensual de gastos
- Producto más comprado
- Cantidad total de compras

#### 🔎 Filtros Avanzados
- Filtrar por **supermercado** (búsqueda por texto)
- Filtrar por **rango de fechas** (desde/hasta)
- Filtrar por **rango de montos** (mínimo/máximo)
- **Ordenamiento múltiple**:
  - Más reciente / Más antiguo
  - Mayor monto / Menor monto
- Indicador visual de filtros activos
- Contador de resultados en tiempo real

#### 📤 Exportación de Datos
- **Exportar historial a CSV** (incluso con filtros aplicados)
- Compartir archivo mediante cualquier app (WhatsApp, Gmail, Drive, etc.)
- Formato CSV estándar con todas las columnas relevantes
- Uso de FileProvider para compartir de forma segura

#### 📱 Historial y Navegación
- Listado completo de compras con expansión de detalles
- Navegación fluida entre pantallas con Navigation Compose
- Bottom Navigation Bar persistente
- Intents para compartir resumen de compras

---

## 🏗️ Características Técnicas Destacadas

### ✅ Room + Retrofit Integrados
- **GET con caché**: búsqueda de productos primero en Room, luego en API si no hay caché válido
- **POST con persistencia**: compras guardadas primero en Room (optimistic update), luego sincronizadas con API
- **Single Source of Truth**: toda la UI consume datos exclusivamente desde Room mediante Flows
- **Cache inteligente**: productos buscados se almacenan con timestamp para validación de expiración

### ⚡ Arquitectura Reactiva
- **StateFlow** para estados de UI
- **Flow** de Room con `collectAsStateWithLifecycle` para actualizaciones automáticas
- **Coroutines** para operaciones asíncronas
- **ViewModelProvider.Factory** para inyección de dependencias en ViewModels
- **combine()** de Flows para filtrado reactivo en tiempo real

### 🗄️ Persistencia Multinivel
- **DataStore Preferences**: sesión de usuario (email, nombre, contraseña encriptada)
- **Room Database**: compras, productos y caché de búsquedas
- **FileProvider**: almacenamiento seguro de fotos de tickets

### 🎨 UI/UX Moderna
- **Jetpack Compose** con Material 3 Design
- **Animaciones** suaves (AnimatedVisibility, transiciones)
- **Feedback visual** constante (badges, colores, loading states)
- **Modo responsivo** con layouts adaptables

---

## 🏛️ Arquitectura del Proyecto

El proyecto implementa **MVVM (Model-View-ViewModel)** con separación clara de responsabilidades:

```
app/
├── data/
│   ├── db/
│   │   ├── entities/        → Entidades Room: CompraEntity, ProductoEntity, ProductoBuscadoEntity
│   │   ├── dao/             → DAOs: CompraDao, ProductoDao, ProductoBuscadoDao
│   │   ├── mappers/         → Conversión Entity ↔ Model
│   │   └── AppDatabase.kt   → Configuración de Room
│   ├── network/
│   │   ├── dtos/            → Data Transfer Objects para APIs
│   │   ├── api/             → Interfaces Retrofit (OpenFoodFactsApi, CompraApiService)
│   │   └── RetrofitClient.kt → Configuración Retrofit + OkHttp
│   ├── preferences/         → DataStore para sesión y preferencias
│   └── repository/
│       ├── CompraRepository.kt       → CRUD de compras (Room)
│       ├── NetworkRepository.kt      → GET/POST con caché integrado
│       └── UserPreferencesRepository.kt → Gestión de sesión
├── model/                   → Modelos de dominio (Compra, Producto, Usuario, FiltroCompra)
├── navigation/
│   ├── NavRoutes.kt         → Definición de rutas
│   └── AppNavGraph.kt       → Grafo de navegación con ViewModels
├── ui/
│   ├── screens/             → 12 pantallas Composables
│   ├── components/          → Componentes reutilizables (BottomNavBar, etc.)
│   └── theme/               → Colores, tipografía, shapes (Material 3)
├── viewmodel/
│   ├── CompraViewModel.kt           → Lógica de compras + filtros
│   ├── BuscarProductoViewModel.kt   → Búsqueda con API + caché
│   ├── UsuarioViewModel.kt          → Gestión de sesión
│   └── factories/                   → ViewModelProvider.Factory para DI
└── MainActivity.kt          → Activity principal con Navigation Host
```

### 📐 Flujos de Datos

#### Flujo GET (Búsqueda de Productos)
```
Usuario busca "coca cola"
    ↓
BuscarProductoViewModel llama NetworkRepository.buscarProductos()
    ↓
NetworkRepository consulta ProductoBuscadoDao
    ↓
¿Hay caché válido (< 7 días)?
    ├─ SÍ → Emite desde Room
    └─ NO → Llama OpenFoodFacts API
            ↓
        Guarda en Room (ProductoBuscadoEntity)
            ↓
        Emite desde Room (Single Source of Truth)
            ↓
        UI se actualiza con collectAsStateWithLifecycle
```

#### Flujo POST (Registro de Compra)
```
Usuario guarda compra
    ↓
CompraViewModel.guardarCompraConSincronizacion()
    ↓
1. Guarda en Room PRIMERO (optimistic update)
    ↓
2. Room emite Flow → UI se actualiza inmediatamente
    ↓
3. POST a Reqres.in API
    ↓
4. Si exitoso: mensaje "✅ Compra sincronizada"
   Si falla: mensaje "⚠️ Guardado localmente, sincronización pendiente"
```

---

## 🛠️ Stack Tecnológico

| Categoría | Tecnología | Versión |
|---|---|---|
| **Lenguaje** | Kotlin | 1.9+ |
| **UI** | Jetpack Compose | - |
| **Design System** | Material 3 | - |
| **Navegación** | Navigation Compose | 2.7+ |
| **Base de datos** | Room | 2.6+ |
| **Preferencias** | DataStore Preferences | 1.0+ |
| **Networking** | Retrofit 2 + OkHttp | 2.9+ |
| **Serialización** | Gson | 2.10+ |
| **Imágenes** | Coil Compose | 2.5+ |
| **Concurrencia** | Coroutines + Flow | 1.7+ |
| **Arquitectura** | MVVM + StateFlow | - |
| **Lifecycle** | ViewModel + LiveData | 2.6+ |
| **Inyección** | ViewModelProvider.Factory | - |
| **Gráficos** | Canvas Compose (custom) | - |

---

## 📱 Pantallas Implementadas

| Ruta | Pantalla | Funcionalidad |
|---|---|---|
| `splash` | Splash Screen | Animación de bienvenida + navegación automática |
| `login` | Login | Inicio de sesión con validación |
| `registro` | Registro | Alta de nuevo usuario |
| `olvido_contrasena` | Recuperar Contraseña | Recuperación de acceso |
| `home` | Dashboard | Últimas compras + gasto total + acceso rápido |
| `nueva_compra` | Nueva Compra | Formulario de registro con productos |
| `detalle_compra/{id}` | Detalle Compra | Vista completa + edición + foto ticket |
| `nuevo_producto/{compraId}` | Agregar Producto | Formulario manual de producto |
| `buscar_producto/{compraId}` | Buscar Producto | **GET API**: búsqueda en OpenFoodFacts |
| `historial` | Historial | Lista con **filtros avanzados** + **exportar CSV** |
| `estadisticas` | Estadísticas | Gráficos donut + barras + métricas |
| `perfil` | Perfil | Datos del usuario + sesión |
| `editar_perfil` | Editar Perfil | Modificar nombre/email |
| `settings` | Configuración | Preferencias de la app |

---

## 🌐 APIs Externas Integradas

### 1. OpenFoodFacts API (Búsqueda de Productos)
```
URL Base: https://world.openfoodfacts.org/
Método: GET
Endpoint: /cgi/search.pl
```

**Implementación:**
- Sistema de **caché con Room** (tabla `productos_buscados`)
- Validación de caché por timestamp (7 días de validez)
- Fallback a caché expirado si no hay conexión
- Mapeo completo de 16 campos nutricionales

**Datos obtenidos:**
- Código de barras
- Nombre y marca
- Nutri-Score (A, B, C, D, E)
- Información nutricional (por 100g): calorías, grasas, sal, azúcar, proteínas, fibra
- Ingredientes y alérgenos
- Imagen del producto
- País de origen

### 2. Reqres.in API (Sincronización de Compras)
```
URL Base: https://reqres.in/api/
Método: POST
Endpoint: /compras
```

**Implementación:**
- **Optimistic Update**: guarda en Room primero, sincroniza después
- Manejo de errores con mensajes diferenciados
- Payload JSON con datos de compra completos
- Response con ID remoto generado

**Estructura del request:**
```json
{
  "usuarioEmail": "user@example.com",
  "supermercado": "Carrefour",
  "total": 1250.50,
  "fecha": "20/07/2026",
  "cantidadProductos": 5
}
```

---

## 📲 Uso de Intents y Permisos

### Intents Implementados

#### 1. Compartir Compra (ACTION_SEND)
```kotlin
Intent.ACTION_SEND
Type: "text/plain"
```
Permite compartir el resumen de una compra por cualquier app instalada (WhatsApp, email, etc.)

#### 2. Exportar CSV (ACTION_SEND con FileProvider)
```kotlin
Intent.ACTION_SEND
Type: "text/csv"
FileProvider: android:authorities="${applicationId}.fileprovider"
```
Comparte el archivo CSV del historial de forma segura usando FileProvider.

#### 3. Tomar Foto del Ticket
```kotlin
ActivityResultContracts.TakePicture
```
Abre la cámara nativa del dispositivo para capturar el ticket.

#### 4. Seleccionar desde Galería
```kotlin
ActivityResultContracts.PickVisualMedia
MediaType: ImageOnly
```
Abre el selector de imágenes del sistema operativo.

### Permisos Declarados
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

---

## 💾 Modelo de Persistencia

### DataStore Preferences
```kotlin
Datos almacenados:
- userName (String)
- userEmail (String)
- userPassword (String, encriptada)
- isLoggedIn (Boolean)
```

### Room Database (SQLite)

#### Tabla: `compras`
```sql
CREATE TABLE compras (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    supermercado TEXT NOT NULL,
    fecha TEXT NOT NULL,
    hora TEXT NOT NULL,
    total REAL NOT NULL,
    usuarioEmail TEXT NOT NULL,
    ticketImageUri TEXT
)
```

#### Tabla: `productos`
```sql
CREATE TABLE productos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo TEXT,
    nombre TEXT NOT NULL,
    descripcion TEXT,
    precio REAL NOT NULL,
    cantidad INTEGER DEFAULT 1,
    compraId INTEGER,
    FOREIGN KEY(compraId) REFERENCES compras(id) ON DELETE CASCADE
)
```

#### Tabla: `productos_buscados` (Caché API)
```sql
CREATE TABLE productos_buscados (
    codigo TEXT PRIMARY KEY,
    nombre TEXT,
    marca TEXT,
    nutriScore TEXT,
    calorias TEXT,
    grasas TEXT,
    ...
    timestamp INTEGER NOT NULL  -- Para validación de caché
)
```

### FileProvider (Cache Directory)
- **Fotos de tickets**: almacenadas en `context.cacheDir`
- **Archivos CSV exportados**: generados en `context.cacheDir`
- Compartidos mediante `FileProvider.getUriForFile()`

---

## 🎯 Patrones y Buenas Prácticas Aplicadas

✅ **MVVM (Model-View-ViewModel)**: separación clara entre UI, lógica y datos  
✅ **Single Source of Truth**: Room como única fuente de datos  
✅ **Repository Pattern**: capa de abstracción para acceso a datos  
✅ **StateFlow**: manejo reactivo de estados de UI  
✅ **Coroutines**: operaciones asíncronas sin bloquear UI thread  
✅ **Optimistic Update**: UI se actualiza inmediatamente, sincroniza después  
✅ **ViewModelProvider.Factory**: inyección manual de dependencias  
✅ **Clean Architecture**: capas bien definidas y desacopladas  
✅ **Compose State Hoisting**: estados gestionados correctamente  
✅ **Error Handling**: manejo de errores de red y base de datos  
✅ **Cache Strategy**: reducción de llamadas innecesarias a APIs  
✅ **Material Design 3**: UI consistente y moderna  

---

## 📊 Estadísticas del Proyecto

- **Pantallas**: 14 screens completas
- **ViewModels**: 3 ViewModels con factories
- **Repositorios**: 3 (Compra, Network, UserPreferences)
- **DAOs**: 3 (CompraDao, ProductoDao, ProductoBuscadoDao)
- **Entidades Room**: 3 tablas
- **APIs integradas**: 2 (OpenFoodFacts, Reqres)
- **Líneas de código**: ~3,500 líneas Kotlin
- **Compose functions**: ~50 composables
- **Flows implementados**: GET con caché + POST con sincronización

---

## 🚀 Instalación y Ejecución

### Requisitos Previos
- **Android Studio**: Hedgehog (2023.1.1) o superior
- **JDK**: 17 o superior
- **SDK mínimo**: 24 (Android 7.0 Nougat)
- **SDK objetivo**: 34 (Android 14)
- **Gradle**: 8.0+

### Pasos de Instalación

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/benjaalasino/SuperAhorro.git
   cd SuperAhorro
   ```

2. **Abrir en Android Studio:**
   - File → Open → Seleccionar carpeta del proyecto

3. **Sincronizar Gradle:**
   - Android Studio sincronizará automáticamente
   - O manualmente: File → Sync Project with Gradle Files

4. **Configurar emulador o dispositivo físico:**
   - Emulador: API 24+ recomendado (API 33 óptimo)
   - Dispositivo físico: activar "Depuración USB"

5. **Ejecutar la aplicación:**
   ```bash
   ▶️ Run 'app'
   ```
   O mediante terminal:
   ```bash
   ./gradlew installDebug
   ```

### Compilar APK Release
```bash
./gradlew assembleRelease
```
El APK se generará en: `app/build/outputs/apk/release/app-release.apk`

---

## 📂 Estructura del Package

```
com.undef.superahorroCalvoAlasino
```

---

## 👥 Autores

- **Calvo**  
- **Alasino**

**Universidad**: [Nombre de la Universidad]  
**Materia**: Tecnologías Móviles  
**Año**: 2026  
**Entrega**: Trabajo Final (Defensa)

---

## 📄 Licencia

Este proyecto es de carácter académico y fue desarrollado para la materia Tecnologías Móviles.

---

## 🙏 Agradecimientos

- **OpenFoodFacts** por su API pública de productos alimenticios
- **Reqres.in** por proporcionar API de testing gratuita
- **Android Developers** por la documentación oficial de Jetpack Compose
- **Material Design 3** por las guías de diseño
- Profesores y equipo docente de Tecnologías Móviles 2026

---

**Última actualización**: Julio 2026  
**Versión**: 1.0 (Final)

