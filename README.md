# SuperAhorro 🛒

Aplicación Android para registrar, consultar y analizar gastos de supermercado, permitiendo llevar un mejor control de las compras y detectar oportunidades de ahorro.

**Trabajo Práctico Integrador — Tecnologías Móviles 2026**  
Autores: Calvo · Alasino

---

## Capturas de pantalla

| Splash | Login | Home |
|---|---|---|
| *Pantalla de bienvenida* | *Inicio de sesión* | *Dashboard principal* |

| Nueva Compra | Detalle | Estadísticas |
|---|---|---|
| *Registro de compra* | *Detalle con ticket* | *Gráficos de gasto* |

---

## Descripción

**SuperAhorro** permite al usuario:

- Registrarse, iniciar sesión y gestionar su cuenta (incluye recuperación de contraseña)
- Registrar compras indicando supermercado, fecha, hora y total
- Agregar, editar y eliminar productos dentro de cada compra
- Adjuntar la foto del ticket mediante cámara o galería
- Ver el historial de compras ordenado por fecha
- Consultar estadísticas: gasto total, gasto por supermercado (gráfico donut), evolución mensual (gráfico de barras) y producto más comprado
- Compartir el resumen de una compra por cualquier app instalada
- Buscar productos en la base de datos de OpenFoodFacts

---

## Arquitectura

El proyecto sigue **MVVM (Model-View-ViewModel)** con las siguientes capas:

```
app/
├── data/
│   ├── db/           → Room (entidades, DAOs, migraciones)
│   ├── network/      → Retrofit + OkHttp (APIs externas)
│   ├── preferences/  → DataStore (sesión y preferencias)
│   └── repository/   → Repositorios (fuente única de verdad)
├── model/            → Modelos de dominio
├── navigation/       → NavRoutes + AppNavGraph
├── ui/
│   ├── screens/      → Composables de cada pantalla
│   ├── components/   → Componentes reutilizables
│   └── theme/        → Material 3 (colores, tipografía)
└── viewmodel/        → ViewModels con StateFlow
```

---

## Tecnologías utilizadas

| Categoría | Tecnología |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Navegación | Navigation Compose |
| Base de datos | Room 2.6 (con migraciones) |
| Preferencias | DataStore Preferences |
| Red | Retrofit 2 + OkHttp + Gson |
| Imágenes | Coil Compose |
| Async | Coroutines + StateFlow |
| Arquitectura | MVVM |

---

## Pantallas

| Ruta | Pantalla |
|---|---|
| `splash` | Splash / Bienvenida |
| `login` | Inicio de sesión |
| `registro` | Registro de usuario |
| `olvido_contrasena` | Recuperación de contraseña |
| `home` | Dashboard con últimas compras |
| `nueva_compra` | Registrar nueva compra |
| `detalle_compra/{id}` | Detalle + productos + ticket |
| `nuevo_producto/{compraId}` | Agregar producto a compra |
| `buscar_producto/{compraId}` | Buscar producto (OpenFoodFacts) |
| `historial` | Historial ordenado por fecha |
| `estadisticas` | Gráficos y métricas |
| `perfil` | Datos del usuario |
| `editar_perfil` | Modificar datos del perfil |
| `settings` | Configuración de la app |

---

## APIs externas

### OpenFoodFacts
- **Endpoint:** `https://world.openfoodfacts.org/`
- **Uso:** Búsqueda de productos por nombre o código de barras
- **Método:** GET

### Reqres.in (simulación de backend)
- **Endpoint:** `https://reqres.in/api/`
- **Uso:** Registro remoto de compras (sincronización)
- **Método:** POST

---

## Funcionalidades con Intents

- **Compartir compra:** `Intent.ACTION_SEND` — comparte el resumen en texto por cualquier app
- **Tomar foto de ticket:** `ActivityResultContracts.TakePicture` — abre la cámara del dispositivo
- **Galería:** `ActivityResultContracts.PickVisualMedia` — selector de imágenes del sistema

---

## Persistencia

| Dato | Almacenamiento |
|---|---|
| Sesión del usuario (email, nombre, contraseña) | DataStore Preferences |
| Preferencias (modo oscuro, orden) | DataStore Preferences |
| Compras y productos | Room (SQLite) |
| URI de imagen del ticket | Room (campo `ticketImageUri`) |

---

## Cómo compilar

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/benjaalasino/SuperAhorro.git
   ```
2. Abrir en **Android Studio Hedgehog** o superior
3. Sincronizar Gradle
4. Ejecutar en emulador o dispositivo físico (minSdk 24 / Android 7.0)

---

## Package

```
com.undef.superahorroCalvoAlasino
```
