plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android) // Hilt para inyección de dependencias
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.compose.compiler) // Requerido con Kotlin 2.0
}

android {
    namespace = "com.toltev.plantlux" // Cambiado al package solicitado
    compileSdk = 36

    defaultConfig {
        applicationId = "com.toltev.plantlux" // Cambiado al package solicitado
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // Java 17 recomendado
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true // Habilitar Jetpack Compose
    }
    // A partir de Kotlin 2.0, se usa el plugin Compose Compiler separado; no es necesario fijar aqui
}

// Dependencias principales del proyecto
// Se usan las versiones más recientes y Compose BOM para asegurar compatibilidad

dependencies {
    // Core y Material
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.compose.bom)) // Compose BOM
    implementation(libs.compose.material3)
    implementation(libs.material)

    // Activity Compose
    implementation(libs.activity.compose)

    // Jetpack Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.foundation)
    implementation(libs.compose.runtime)
    implementation(libs.compose.navigation)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.compose.material.icons.extended)

    // Lifecycle
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.viewmodel.compose)

    // Room (persistencia local)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // DataStore (ajustes)
    implementation(libs.datastore.preferences)

    // WorkManager (tareas en background)
    implementation(libs.work.runtime.ktx)

    // Hilt (inyección de dependencias)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Herramientas de desarrollo
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.tooling.preview)
}