# Cero — Android Compose + Clean Architecture

App Android modular en **Kotlin** y **Jetpack Compose**: catálogo de películas con **TMDB**, persistencia **Room**, inyección **Hilt** y módulo nativo **NDK** de ejemplo. Incluye una UI interactiva de pedidos de cafetería con animaciones custom.

## Características

### Rama `aplicacionPeliculas` (default)

- Listado de películas populares vía **The Movie Database (TMDB)**.
- **MVVM** + estados UI tipados (`MovieUiState`).
- **Retrofit** + interceptor de API key (Bearer desde `local.properties`).
- **Room** para persistencia local (`UserEntity`, `AppDatabase`).
- Imágenes con **Coil Compose**.
- Módulo `:security` con código nativo C++ (ejemplo JNI, sin secretos embebidos).

### Feature `coffe` (cafetería)

- UI interactiva para armar pedidos de café.
- Animaciones custom: morphing, loaders espaciales, fondo geométrico.
- Estado con `DragState` y `OrderState` (datos mock locales).

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
└── security/               # Biblioteca JNI (módulo nativo de ejemplo)
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

Las claves **no van en el código ni en Git**. Configúralas en `local.properties`:

```bash
cp local.properties.example local.properties
```

Edita `local.properties`:

```properties
sdk.dir=/ruta/a/Android/Sdk
TMDB_API_KEY=tu_token_v4_de_lectura
```

Obtén el token en [TMDB → API Settings](https://www.themoviedb.org/settings/api).

Gradle inyecta la clave en `BuildConfig` en tiempo de compilación. Si falta, la app falla al iniciar la red con un mensaje claro.

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

## Seguridad

- `local.properties` está en `.gitignore` — nunca commitees tokens.
- El módulo `:security` expone solo un string de ejemplo vía JNI; **no almacena API keys**.
- Si un token llegó a quedar expuesto en el historial de Git, revócalo en TMDB y genera uno nuevo.

## Tests

```bash
./gradlew test
./gradlew :security:connectedAndroidTest   # requiere dispositivo/emulador
```

## Colaboradores

- [Fernando Rodríguez Prianti](https://github.com/FernandoRP-nan)
- [Marco Antonio](https://github.com/Marco-Antonio-AH)

## Licencia

Uso educativo y como base de referencia. Consultar a los autores antes de uso comercial.
