# Android Template — Compose + Clean Architecture

Plantilla Android modular con **Kotlin**, **Jetpack Compose** y arquitectura por capas. Incluye integración **TMDB** (películas), un módulo de **cafetería** con animaciones Compose y un submódulo **`security`** en C++/JNI para ofuscar claves de API.

> Proyecto colaborativo. Repositorio original: [Marco-Antonio-AH/android-template](https://github.com/Marco-Antonio-AH/android-template). Copia de referencia mantenida por [FernandoRP-nan](https://github.com/FernandoRP-nan).

## Características

### Rama `aplicacionPeliculas` (default)

- Listado de películas populares vía **The Movie Database (TMDB)**.
- **MVVM** + estados UI tipados (`MovieUiState`).
- **Retrofit** + interceptor de API key.
- **Room** para persistencia local (`UserEntity`, `AppDatabase`).
- Imágenes con **Coil Compose**.
- Claves de API y URLs base en módulo nativo **JNI** (`:security`).

### Feature `coffe` (cafetería)

- UI interactiva para armar pedidos de café.
- Animaciones custom: morphing, loaders espaciales, fondo geométrico.
- Estado con `DragState` y `OrderState` (sin backend remoto; datos mock).

## Stack

| Área | Tecnología |
|------|------------|
| UI | Jetpack Compose, Material 3 |
| Arquitectura | Clean Architecture, MVVM, Repository |
| DI | Hilt (kapt) |
| Red | Retrofit + Gson |
| Local | Room |
| Nativo | CMake, NDK (módulo `security`) |
| Imágenes | Coil |

## Estructura del proyecto

```
android-template/
├── app/                    # Módulo principal (Compose)
│   └── feature/
│       ├── movies/         # TMDB — películas populares
│       └── coffe/          # Pedidos de café + animaciones
└── security/               # Biblioteca JNI (claves y URLs)
    └── src/main/cpp/
        └── security.cpp
```

## Ramas

| Rama | Descripción |
|------|-------------|
| `aplicacionPeliculas` | App de películas TMDB (default) |
| `main` | Línea base |

## Requisitos

- Android Studio Ladybug o superior
- JDK 17
- Android SDK 34
- NDK (para compilar `:security`)
- minSdk 24

## Configuración

### 1. Clonar y abrir

```bash
git clone https://github.com/FernandoRP-nan/android-template.git
cd android-template
git checkout aplicacionPeliculas
```

Abre el proyecto en Android Studio y deja que Gradle sincronice.

### 2. API key de TMDB

La app lee la clave desde código nativo (`NativeKeys.getApiKey()`). **Sustituye la clave por la tuya** antes de publicar:

1. Crea una cuenta en [TMDB](https://www.themoviedb.org/settings/api).
2. Edita `security/src/main/cpp/security.cpp` y reemplaza las partes del token JWT.
3. Recompila: `./gradlew assembleDebug`.

> **Importante:** no subas claves reales al repositorio. El token incluido es de demo; revócalo en TMDB si quedó expuesto.

### 3. Cambiar pantalla activa

En `MainActivity.kt` puedes alternar entre:

```kotlin
setContent {
    MovieScreen()           // Películas TMDB
    // CoffeeOrderScreen()  // UI de cafetería
}
```

## Ejecución

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Módulo `security`

Expone métodos JNI consumidos desde Kotlin:

| Método | Propósito |
|--------|-----------|
| `getApiKey()` | Bearer token TMDB |
| `getBaseUrl()` | URL base de la API v3 |
| `getImageBaseUrl()` | CDN de posters |

Patrón útil para no dejar secretos en bytecode Kotlin puro (aunque no reemplaza un backend proxy).

## Tests

```bash
./gradlew test
./gradlew :security:connectedAndroidTest   # requiere dispositivo/emulador
```

## Colaboradores

- **Marco Antonio** — [Marco-Antonio-AH](https://github.com/Marco-Antonio-AH)
- **Fernando Rodríguez Prianti** — documentación y copia de referencia

## Licencia

Uso educativo y como plantilla de referencia. Consultar a los autores antes de uso comercial.
