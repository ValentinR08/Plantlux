# Changelog - PlantLux 🌱

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Documentación completa del proyecto
- README.md con información general
- Documentación técnica detallada
- Guía de desarrollo para nuevos desarrolladores
- Changelog para seguimiento de versiones

### Changed
- Mejorada la estructura de documentación
- Actualizada la información de configuración

### Fixed
- Documentación de configuración de build
- Especificaciones de versiones de dependencias

## [1.0.0] - 2024-12-19

### Added
- **Arquitectura base del proyecto**
  - Clean Architecture implementada
  - Separación de capas (Presentation, Domain, Data)
  - Patrón MVVM con Jetpack Compose

- **Configuración de build**
  - Android Gradle Plugin 8.12.0
  - Kotlin 2.0.21
  - Compose BOM 2024.05.00
  - Hilt 2.52 para inyección de dependencias

- **Estructura de paquetes**
  - `com.toltev.plantlux` como namespace principal
  - Organización por capas de arquitectura
  - Separación clara de responsabilidades

- **Dependencias principales**
  - Jetpack Compose para UI declarativa
  - Material Design 3 para componentes
  - Navigation Compose para navegación
  - Room 2.6.1 para base de datos local
  - DataStore 1.1.1 para preferencias
  - WorkManager 2.9.0 para tareas en background

- **Configuración de desarrollo**
  - JDK 17 como versión objetivo
  - Android SDK 36 como compileSdk
  - MinSdk 24 para compatibilidad
  - TargetSdk 36 para funcionalidades modernas

- **Clases principales**
  - `MainActivity.kt`: Punto de entrada de la aplicación
  - `PlantLuxApp.kt`: Clase Application con Hilt
  - Estructura base para inyección de dependencias

- **Configuración de testing**
  - JUnit 4 para unit tests
  - Espresso para UI tests
  - AndroidJUnitRunner para tests instrumentados

### Technical Details
- **Versiones de dependencias centralizadas** en `gradle/libs.versions.toml`
- **Configuración de ProGuard** para optimización de release builds
- **Plugins de Gradle** configurados para Compose y Hilt
- **Estructura de módulos** preparada para escalabilidad

### Build Configuration
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

### Dependencies Overview
- **Core**: androidx.core:core-ktx:1.16.0
- **Compose**: androidx.compose:compose-bom:2024.05.00
- **Navigation**: androidx.navigation:navigation-compose:2.7.7
- **Lifecycle**: androidx.lifecycle:lifecycle-runtime-ktx:2.7.0
- **Room**: androidx.room:room-runtime:2.6.1
- **Hilt**: com.google.dagger:hilt-android:2.52
- **WorkManager**: androidx.work:work-runtime-ktx:2.9.0

---

## Tipos de Cambios

- **Added**: para nuevas funcionalidades
- **Changed**: para cambios en funcionalidades existentes
- **Deprecated**: para funcionalidades que serán eliminadas
- **Removed**: para funcionalidades eliminadas
- **Fixed**: para correcciones de bugs
- **Security**: para vulnerabilidades de seguridad

## Convenciones de Versionado

Este proyecto sigue [Semantic Versioning](https://semver.org/):

- **MAJOR.MINOR.PATCH**
  - **MAJOR**: Cambios incompatibles con versiones anteriores
  - **MINOR**: Nuevas funcionalidades compatibles hacia atrás
  - **PATCH**: Correcciones de bugs compatibles hacia atrás

## Notas de Lanzamiento

### v1.0.0
- **Lanzamiento inicial** del proyecto PlantLux
- **Arquitectura base** implementada con Clean Architecture
- **Configuración completa** de build y dependencias
- **Documentación técnica** incluida

---

**PlantLux** - Cuidando tus plantas con tecnología moderna 🌱✨


