# Documentación Técnica - PlantLux

## Arquitectura del Proyecto

### Clean Architecture
El proyecto sigue los principios de Clean Architecture con separación clara de responsabilidades:

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │   Compose   │  │ ViewModel   │  │ Navigation  │         │
│  │    UI       │  │             │  │             │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │   Models    │  │ Use Cases   │  │Repository   │         │
│  │             │  │             │  │ Interfaces  │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      DATA LAYER                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │ Repository  │  │    Room     │  │ DataStore   │         │
│  │ Impl        │  │   Database  │  │ Preferences │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

## Configuración de Build

### Versiones de Dependencias (libs.versions.toml)

```toml
[versions]
agp = "8.12.0"                    # Android Gradle Plugin
kotlin = "2.0.21"                 # Kotlin
compose-bom = "2024.05.00"        # Compose BOM
hilt = "2.52"                     # Hilt DI
room = "2.6.1"                    # Room Database
work = "2.9.0"                    # WorkManager
```

### Configuración de Android (app/build.gradle.kts)

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

## Estructura de Paquetes

### Organización por Capas

```
com.toltev.plantlux/
├── MainActivity.kt              # Entry point
├── PlantLuxApp.kt              # Application class
├── di/                         # Dependency injection
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
├── data/                       # Data layer
│   ├── local/                  # Local data sources
│   │   ├── dao/
│   │   ├── entities/
│   │   └── PlantLuxDatabase.kt
│   ├── prefs/                  # Preferences
│   │   └── UserPreferences.kt
│   └── repo/                   # Repository implementations
│       └── PlantRepositoryImpl.kt
├── domain/                     # Domain layer
│   ├── model/                  # Domain models
│   │   └── Plant.kt
│   └── usecase/                # Business logic
│       └── PlantUseCases.kt
├── ui/                         # Presentation layer
│   ├── navigation/             # Navigation
│   │   └── AppNav.kt
│   ├── screens/                # UI screens
│   │   ├── home/
│   │   ├── plants/
│   │   └── settings/
│   └── theme/                  # UI theme
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── sensors/                    # Hardware sensors
│   └── SensorManager.kt
└── workers/                    # Background workers
    └── PlantCareWorker.kt
```

## Patrones de Diseño Implementados

### 1. MVVM (Model-View-ViewModel)
- **View**: Compose UI Components
- **ViewModel**: State management con StateFlow
- **Model**: Domain models y Repository pattern

### 2. Repository Pattern
```kotlin
interface PlantRepository {
    suspend fun getPlants(): Flow<List<Plant>>
    suspend fun addPlant(plant: Plant)
    suspend fun updatePlant(plant: Plant)
    suspend fun deletePlant(plantId: String)
}
```

### 3. Use Case Pattern
```kotlin
class GetPlantsUseCase @Inject constructor(
    private val repository: PlantRepository
) {
    operator fun invoke(): Flow<List<Plant>> = repository.getPlants()
}
```

### 4. Dependency Injection con Hilt
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePlantRepository(
        plantDao: PlantDao,
        userPreferences: UserPreferences
    ): PlantRepository {
        return PlantRepositoryImpl(plantDao, userPreferences)
    }
}
```

## Gestión de Estado

### StateFlow en ViewModels
```kotlin
@HiltViewModel
class PlantViewModel @Inject constructor(
    private val getPlantsUseCase: GetPlantsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PlantUiState())
    val uiState: StateFlow<PlantUiState> = _uiState.asStateFlow()
    
    init {
        loadPlants()
    }
    
    private fun loadPlants() {
        viewModelScope.launch {
            getPlantsUseCase().collect { plants ->
                _uiState.value = _uiState.value.copy(
                    plants = plants,
                    isLoading = false
                )
            }
        }
    }
}
```

### UI State Classes
```kotlin
data class PlantUiState(
    val plants: List<Plant> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
```

## Navegación con Compose Navigation

### Definición de Rutas
```kotlin
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Plants : Screen("plants")
    object PlantDetail : Screen("plant/{plantId}") {
        fun createRoute(plantId: String) = "plant/$plantId"
    }
    object Settings : Screen("settings")
}
```

### Navegación Principal
```kotlin
@Composable
fun AppNav() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Plants.route) {
            PlantsScreen(navController)
        }
        composable(
            route = Screen.PlantDetail.route,
            arguments = listOf(navArgument("plantId") { type = NavType.StringType })
        ) { backStackEntry ->
            val plantId = backStackEntry.arguments?.getString("plantId")
            PlantDetailScreen(plantId = plantId, navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
    }
}
```

## Base de Datos con Room

### Entidades
```kotlin
@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey val id: String,
    val name: String,
    val species: String,
    val lastWatered: Long,
    val waterFrequency: Int,
    val createdAt: Long = System.currentTimeMillis()
)
```

### DAO (Data Access Object)
```kotlin
@Dao
interface PlantDao {
    @Query("SELECT * FROM plants ORDER BY createdAt DESC")
    fun getAllPlants(): Flow<List<PlantEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantEntity)
    
    @Update
    suspend fun updatePlant(plant: PlantEntity)
    
    @Delete
    suspend fun deletePlant(plant: PlantEntity)
}
```

### Database
```kotlin
@Database(
    entities = [PlantEntity::class],
    version = 1
)
abstract class PlantLuxDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    
    companion object {
        @Volatile
        private var INSTANCE: PlantLuxDatabase? = null
        
        fun getDatabase(context: Context): PlantLuxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlantLuxDatabase::class.java,
                    "plantlux_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

## WorkManager para Tareas en Segundo Plano

### Worker de Cuidado de Plantas
```kotlin
class PlantCareWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // Lógica de cuidado de plantas
            // Verificar plantas que necesitan agua
            // Enviar notificaciones
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
```

### Programación de Trabajos
```kotlin
@HiltWorker
class PlantCareWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val plantRepository: PlantRepository
) : CoroutineWorker(context, params) {
    
    companion object {
        fun schedulePlantCare(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()
            
            val plantCareWork = PeriodicWorkRequestBuilder<PlantCareWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.DAYS
            )
                .setConstraints(constraints)
                .build()
            
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "plant_care",
                    ExistingPeriodicWorkPolicy.KEEP,
                    plantCareWork
                )
        }
    }
}
```

## Testing

### Unit Tests
```kotlin
@RunWith(MockitoJUnitRunner::class)
class PlantUseCaseTest {
    
    @Mock
    private lateinit var repository: PlantRepository
    
    @InjectMocks
    private lateinit var getPlantsUseCase: GetPlantsUseCase
    
    @Test
    fun `getPlants returns plants from repository`() = runTest {
        // Given
        val plants = listOf(Plant("1", "Test Plant", "Test Species"))
        whenever(repository.getPlants()).thenReturn(flowOf(plants))
        
        // When
        val result = getPlantsUseCase().first()
        
        // Then
        assertEquals(plants, result)
        verify(repository).getPlants()
    }
}
```

### UI Tests
```kotlin
@RunWith(AndroidJUnit4::class)
class PlantScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun plantScreen_displaysPlants() {
        // Given
        val plants = listOf(Plant("1", "Test Plant", "Test Species"))
        
        // When
        composeTestRule.setContent {
            PlantLuxTheme {
                PlantsScreen(plants = plants)
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("Test Plant").assertIsDisplayed()
    }
}
```

## Configuración de ProGuard

### Reglas de ProGuard (proguard-rules.pro)
```proguard
# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager { *; }

# Compose
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }
```

## Mejores Prácticas

### 1. Nomenclatura
- **Clases**: PascalCase (PlantRepository)
- **Funciones**: camelCase (getPlants)
- **Variables**: camelCase (plantList)
- **Constantes**: UPPER_SNAKE_CASE (MAX_PLANTS)

### 2. Estructura de Archivos
- Un archivo por clase
- Agrupación lógica por funcionalidad
- Separación clara de responsabilidades

### 3. Manejo de Errores
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

### 4. Logging
```kotlin
private const val TAG = "PlantLux"

Log.d(TAG, "Loading plants from database")
Log.e(TAG, "Error loading plants", exception)
```

## Performance y Optimización

### 1. Lazy Loading
```kotlin
@Composable
fun PlantsList(plants: List<Plant>) {
    LazyColumn {
        items(plants) { plant ->
            PlantItem(plant = plant)
        }
    }
}
```

### 2. Remember y DerivedStateOf
```kotlin
@Composable
fun PlantScreen(plants: List<Plant>) {
    val filteredPlants by remember(plants) {
        derivedStateOf {
            plants.filter { it.needsWater }
        }
    }
}
```

### 3. Compose Compiler Metrics
```kotlin
android {
    kotlinOptions {
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                project.buildDir.absolutePath + "/compose_metrics"
        )
    }
}
```

## Seguridad

### 1. Almacenamiento Seguro
```kotlin
@Singleton
class SecurePreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}
```

### 2. Validación de Entrada
```kotlin
fun validatePlantInput(name: String, species: String): ValidationResult {
    return when {
        name.isBlank() -> ValidationResult.Error("El nombre no puede estar vacío")
        name.length > 50 -> ValidationResult.Error("El nombre es demasiado largo")
        species.isBlank() -> ValidationResult.Error("La especie no puede estar vacía")
        else -> ValidationResult.Success
    }
}
```

Esta documentación técnica proporciona una guía completa para entender y trabajar con el proyecto PlantLux, incluyendo todos los aspectos técnicos, patrones de diseño y mejores prácticas implementadas.
