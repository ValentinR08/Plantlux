// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt.android) apply false
}

// Configurar el plugin de Compose Compiler requerido por Kotlin 2.0
// Se aplica en el módulo app vía la dependencia del BOM y el plugin de Android.