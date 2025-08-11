# PlantLux 🌱

## Descripción

PlantLux es una aplicación Android desarrollada en Kotlin que utiliza las tecnologías más modernas de Android para el monitoreo y cuidado de plantas. La aplicación está construida siguiendo las mejores prácticas de arquitectura limpia y utiliza Jetpack Compose para la interfaz de usuario.

## 🚀 Características

- **Arquitectura MVVM**: Implementada con Clean Architecture
- **Jetpack Compose**: UI moderna y declarativa
- **Hilt**: Inyección de dependencias
- **Room**: Base de datos local
- **DataStore**: Almacenamiento de preferencias
- **WorkManager**: Tareas en segundo plano
- **Navigation Compose**: Navegación entre pantallas
- **Material Design 3**: Diseño moderno y accesible

## 🛠️ Tecnologías Utilizadas

### Versiones Principales
- **Android Gradle Plugin**: 8.12.0
- **Kotlin**: 2.0.21
- **Jetpack Compose BOM**: 2024.05.00
- **Hilt**: 2.52
- **Room**: 2.6.1
- **WorkManager**: 2.9.0

### Dependencias Principales
- **Jetpack Compose**: UI moderna y declarativa
- **Material Design 3**: Componentes de diseño
- **Navigation Compose**: Navegación entre pantallas
- **Lifecycle**: Gestión del ciclo de vida
- **Room**: Base de datos local con Kotlin Coroutines
- **DataStore**: Almacenamiento de preferencias
- **WorkManager**: Tareas programadas en segundo plano
- **Hilt**: Inyección de dependencias

## 📁 Estructura del Proyecto

```
app/src/main/java/com/toltev/plantlux/
├── MainActivity.kt              # Actividad principal
├── PlantLuxApp.kt              # Clase Application
├── di/                         # Módulos de inyección de dependencias
├── data/                       # Capa de datos
│   ├── local/                  # Base de datos local
│   ├── prefs/                  # Preferencias del usuario
│   └── repo/                   # Repositorios
├── domain/                     # Capa de dominio
│   ├── model/                  # Modelos de dominio
│   └── usecase/                # Casos de uso
├── ui/                         # Capa de presentación
│   ├── navigation/             # Navegación
│   ├── screens/                # Pantallas de la aplicación
│   └── theme/                  # Temas y estilos
├── sensors/                    # Sensores y hardware
└── workers/                    # Trabajadores en segundo plano
```

## 🏗️ Arquitectura

El proyecto sigue la arquitectura **Clean Architecture** con las siguientes capas:

### 1. **Capa de Presentación (UI)**
- **Jetpack Compose**: Interfaz de usuario declarativa
- **ViewModel**: Gestión del estado de la UI
- **Navigation**: Navegación entre pantallas
- **Theme**: Temas y estilos de la aplicación

### 2. **Capa de Dominio**
- **Modelos**: Entidades de negocio
- **Casos de Uso**: Lógica de negocio
- **Repositorios**: Interfaces para acceso a datos

### 3. **Capa de Datos**
- **Room**: Base de datos local
- **DataStore**: Almacenamiento de preferencias
- **Repositorios**: Implementación de acceso a datos
- **WorkManager**: Tareas en segundo plano

### 4. **Inyección de Dependencias**
- **Hilt**: Gestión de dependencias
- **Módulos**: Configuración de inyección

## 🚀 Configuración del Proyecto

### Requisitos Previos
- Android Studio Hedgehog | 2023.1.1 o superior
- JDK 17
- Android SDK 36
- Gradle 8.0+

### Instalación

1. **Clonar el repositorio**
   ```bash
   git clone [URL_DEL_REPOSITORIO]
   cd PlantLux
   ```

2. **Abrir en Android Studio**
   - Abrir Android Studio
   - Seleccionar "Open an existing project"
   - Navegar a la carpeta del proyecto y seleccionarla

3. **Sincronizar el proyecto**
   - Esperar a que Gradle sincronice las dependencias
   - Resolver cualquier error de configuración si aparece

4. **Ejecutar la aplicación**
   - Conectar un dispositivo Android o iniciar un emulador
   - Presionar el botón "Run" (▶️) en Android Studio

## 📱 Configuración de Build

### Configuración de Android
```kotlin
android {
    namespace = "com.toltev.plantlux"
    compileSdk = 36
    
    defaultConfig {
        applicationId = "com.toltev.plantlux"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
    
    buildFeatures {
        compose = true
    }
}
```

### Plugins Utilizados
- `com.android.application`: Plugin de aplicación Android
- `org.jetbrains.kotlin.android`: Plugin de Kotlin
- `com.google.dagger.hilt.android`: Plugin de Hilt
- `org.jetbrains.kotlin.kapt`: Plugin de KAPT
- `org.jetbrains.kotlin.plugin.compose`: Plugin de Compose Compiler

## 🔧 Configuración de Desarrollo

### Archivos de Configuración Importantes

1. **`build.gradle.kts` (Root)**: Configuración global del proyecto
2. **`app/build.gradle.kts`**: Configuración específica de la aplicación
3. **`gradle/libs.versions.toml`**: Gestión centralizada de versiones
4. **`settings.gradle.kts`**: Configuración de módulos y repositorios

### Variables de Entorno
- Crear archivo `local.properties` con las rutas del SDK:
  ```properties
  sdk.dir=C\:\\Users\\[USERNAME]\\AppData\\Local\\Android\\Sdk
  ```

## 🧪 Testing

El proyecto incluye configuración para diferentes tipos de testing:

### Unit Tests
- **JUnit 4**: Framework de testing unitario
- Ubicación: `app/src/test/`

### Instrumented Tests
- **Espresso**: Testing de UI
- **AndroidJUnitRunner**: Runner para tests instrumentados
- Ubicación: `app/src/androidTest/`

## 📦 Gestión de Dependencias

Las dependencias se gestionan centralmente en `gradle/libs.versions.toml`:

### Categorías de Dependencias
- **Core**: Funcionalidades básicas de Android
- **Compose**: Componentes de UI
- **Lifecycle**: Gestión del ciclo de vida
- **Room**: Base de datos
- **Hilt**: Inyección de dependencias
- **WorkManager**: Tareas en segundo plano
- **Testing**: Frameworks de testing

## 🔄 Flujo de Desarrollo

### 1. **Nuevas Funcionalidades**
- Crear casos de uso en `domain/usecase/`
- Implementar repositorios en `data/repo/`
- Crear pantallas en `ui/screens/`
- Actualizar navegación en `ui/navigation/`

### 2. **Modificaciones de UI**
- Actualizar componentes en `ui/screens/`
- Modificar temas en `ui/theme/`
- Actualizar navegación si es necesario

### 3. **Cambios en Datos**
- Modificar modelos en `domain/model/`
- Actualizar DAOs en `data/local/`
- Modificar repositorios en `data/repo/`

## 🚨 Troubleshooting

### Problemas Comunes

1. **Error de sincronización de Gradle**
   - Limpiar proyecto: `Build > Clean Project`
   - Invalidar caché: `File > Invalidate Caches and Restart`

2. **Error de compilación de Compose**
   - Verificar versión del Compose Compiler
   - Asegurar compatibilidad con Kotlin 2.0

3. **Error de Hilt**
   - Verificar anotaciones `@HiltAndroidApp` y `@AndroidEntryPoint`
   - Revisar configuración de módulos en `di/`

## 📄 Licencia

Este proyecto está bajo la licencia [ESPECIFICAR_LICENCIA].

## 👥 Contribución

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## 📞 Contacto

- **Desarrollador**: [NOMBRE_DEL_DESARROLLADOR]
- **Email**: [EMAIL]
- **Proyecto**: [URL_DEL_PROYECTO]

---

**PlantLux** - Cuidando tus plantas con tecnología moderna 🌱✨
