# Guía de Desarrollo - PlantLux 🌱

## 🚀 Inicio Rápido

### Prerrequisitos
- **Android Studio**: Hedgehog | 2023.1.1 o superior
- **JDK**: Versión 17
- **Android SDK**: API 36
- **Gradle**: 8.0+

### Configuración Inicial

1. **Clonar el repositorio**
   ```bash
   git clone [URL_DEL_REPOSITORIO]
   cd PlantLux
   ```

2. **Abrir en Android Studio**
   - File → Open → Seleccionar carpeta del proyecto
   - Esperar sincronización de Gradle

3. **Configurar SDK**
   - File → Project Structure → SDK Location
   - Verificar que Android SDK esté configurado correctamente

4. **Ejecutar la aplicación**
   - Conectar dispositivo o iniciar emulador
   - Run → Run 'app'

## 📁 Estructura del Proyecto

### Organización de Paquetes

```
com.toltev.plantlux/
├── MainActivity.kt              # Punto de entrada
├── PlantLuxApp.kt              # Clase Application
├── di/                         # Inyección de dependencias
├── data/                       # Capa de datos
│   ├── local/                  # Base de datos local
│   ├── prefs/                  # Preferencias
│   └── repo/                   # Repositorios
├── domain/                     # Lógica de negocio
│   ├── model/                  # Modelos de dominio
│   └── usecase/                # Casos de uso
├── ui/                         # Interfaz de usuario
│   ├── navigation/             # Navegación
│   ├── screens/                # Pantallas
│   └── theme/                  # Temas
├── sensors/                    # Sensores
└── workers/                    # Trabajadores
```

## 🏗️ Patrones de Arquitectura

### Clean Architecture

El proyecto sigue Clean Architecture con tres capas principales:

1. **Presentation Layer** (UI)
   - Compose UI Components
   - ViewModels
   - Navigation

2. **Domain Layer** (Business Logic)
   - Use Cases
   - Domain Models
   - Repository Interfaces

3. **Data Layer** (Data Sources)
   - Repository Implementations
   - Room Database
   - DataStore Preferences

### Flujo de Datos

```
UI (Compose) → ViewModel → UseCase → Repository → DataSource
     ↑                                                      ↓
     ←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←←
```

## 🛠️ Herramientas y Tecnologías

### Stack Tecnológico

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Kotlin | 2.0.21 | Lenguaje principal |
| Jetpack Compose | 2024.05.00 | UI declarativa |
| Hilt | 2.52 | Inyección de dependencias |
| Room | 2.6.1 | Base de datos local |
| WorkManager | 2.9.0 | Tareas en background |
| DataStore | 1.1.1 | Preferencias |

### Configuración de Build

```kotlin
// app/build.gradle.kts
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

## 📝 Convenciones de Código

### Nomenclatura

#### Clases y Objetos
```kotlin
// ✅ Correcto
class PlantRepository
object PlantConstants
sealed class PlantState

// ❌ Incorrecto
class plantRepository
object plantConstants
```

#### Funciones y Variables
```kotlin
// ✅ Correcto
fun getPlants(): Flow<List<Plant>>
val plantList: List<Plant>
var isLoading: Boolean

// ❌ Incorrecto
fun GetPlants(): Flow<List<Plant>>
val PlantList: List<Plant>
```

#### Constantes
```kotlin
// ✅ Correcto
const val MAX_PLANTS = 100
const val DATABASE_NAME = "plantlux_db"

// ❌ Incorrecto
const val maxPlants = 100
const val databaseName = "plantlux_db"
```

### Estructura de Archivos

#### Naming Convention
- **Activities**: `MainActivity.kt`
- **Fragments**: `PlantListFragment.kt`
- **ViewModels**: `PlantViewModel.kt`
- **Repositories**: `PlantRepository.kt`
- **Use Cases**: `GetPlantsUseCase.kt`
- **Models**: `Plant.kt`
- **DAOs**: `PlantDao.kt`

#### Organización de Paquetes
```
feature/
├── data/
│   ├── local/
│   ├── remote/
│   └── repository/
├── domain/
│   ├── model/
│   └── usecase/
└── presentation/
    ├── screen/
    ├── viewmodel/
    └── navigation/
```

## 🔧 Desarrollo de Funcionalidades

### 1. Crear un Nuevo Feature

#### Paso 1: Definir el Modelo de Dominio
```kotlin
// domain/model/Plant.kt
data class Plant(
    val id: String,
    val name: String,
    val species: String,
    val lastWatered: Long,
    val waterFrequency: Int
)
```

#### Paso 2: Crear el Repository Interface
```kotlin
// domain/repository/PlantRepository.kt
interface PlantRepository {
    suspend fun getPlants(): Flow<List<Plant>>
    suspend fun addPlant(plant: Plant)
    suspend fun updatePlant(plant: Plant)
    suspend fun deletePlant(plantId: String)
}
```

#### Paso 3: Implementar Use Cases
```kotlin
// domain/usecase/GetPlantsUseCase.kt
class GetPlantsUseCase @Inject constructor(
    private val repository: PlantRepository
) {
    operator fun invoke(): Flow<List<Plant>> = repository.getPlants()
}
```

#### Paso 4: Crear la Entidad de Room
```kotlin
// data/local/entity/PlantEntity.kt
@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey val id: String,
    val name: String,
    val species: String,
    val lastWatered: Long,
    val waterFrequency: Int
)
```

#### Paso 5: Implementar el DAO
```kotlin
// data/local/dao/PlantDao.kt
@Dao
interface PlantDao {
    @Query("SELECT * FROM plants ORDER BY createdAt DESC")
    fun getAllPlants(): Flow<List<PlantEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantEntity)
}
```

#### Paso 6: Implementar el Repository
```kotlin
// data/repository/PlantRepositoryImpl.kt
class PlantRepositoryImpl @Inject constructor(
    private val plantDao: PlantDao
) : PlantRepository {
    
    override suspend fun getPlants(): Flow<List<Plant>> {
        return plantDao.getAllPlants().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
```

#### Paso 7: Crear el ViewModel
```kotlin
// ui/viewmodel/PlantViewModel.kt
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

#### Paso 8: Crear la Pantalla Compose
```kotlin
// ui/screen/PlantScreen.kt
@Composable
fun PlantScreen(
    viewModel: PlantViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    PlantScreenContent(
        plants = uiState.plants,
        isLoading = uiState.isLoading,
        onPlantClick = { plantId ->
            // Navegación
        }
    )
}

@Composable
private fun PlantScreenContent(
    plants: List<Plant>,
    isLoading: Boolean,
    onPlantClick: (String) -> Unit
) {
    if (isLoading) {
        CircularProgressIndicator()
    } else {
        LazyColumn {
            items(plants) { plant ->
                PlantItem(
                    plant = plant,
                    onClick = { onPlantClick(plant.id) }
                )
            }
        }
    }
}
```

### 2. Configurar Inyección de Dependencias

#### Módulo de Base de Datos
```kotlin
// di/DatabaseModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PlantLuxDatabase {
        return Room.databaseBuilder(
            context,
            PlantLuxDatabase::class.java,
            "plantlux_database"
        ).build()
    }
    
    @Provides
    fun providePlantDao(database: PlantLuxDatabase): PlantDao {
        return database.plantDao()
    }
}
```

#### Módulo de Repositorios
```kotlin
// di/RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun providePlantRepository(
        plantDao: PlantDao
    ): PlantRepository {
        return PlantRepositoryImpl(plantDao)
    }
}
```

## 🧪 Testing

### Unit Tests

#### Testing de Use Cases
```kotlin
@RunWith(MockitoJUnitRunner::class)
class GetPlantsUseCaseTest {
    
    @Mock
    private lateinit var repository: PlantRepository
    
    @InjectMocks
    private lateinit var useCase: GetPlantsUseCase
    
    @Test
    fun `invoke returns plants from repository`() = runTest {
        // Given
        val plants = listOf(Plant("1", "Test Plant", "Test Species", 0L, 7))
        whenever(repository.getPlants()).thenReturn(flowOf(plants))
        
        // When
        val result = useCase().first()
        
        // Then
        assertEquals(plants, result)
        verify(repository).getPlants()
    }
}
```

#### Testing de ViewModels
```kotlin
@RunWith(MockitoJUnitRunner::class)
class PlantViewModelTest {
    
    @Mock
    private lateinit var getPlantsUseCase: GetPlantsUseCase
    
    private lateinit var viewModel: PlantViewModel
    
    @Before
    fun setup() {
        viewModel = PlantViewModel(getPlantsUseCase)
    }
    
    @Test
    fun `loadPlants updates uiState with plants`() = runTest {
        // Given
        val plants = listOf(Plant("1", "Test Plant", "Test Species", 0L, 7))
        whenever(getPlantsUseCase()).thenReturn(flowOf(plants))
        
        // When
        viewModel.loadPlants()
        
        // Then
        val uiState = viewModel.uiState.value
        assertEquals(plants, uiState.plants)
        assertFalse(uiState.isLoading)
    }
}
```

### UI Tests

#### Testing de Pantallas Compose
```kotlin
@RunWith(AndroidJUnit4::class)
class PlantScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun plantScreen_displaysPlants() {
        // Given
        val plants = listOf(Plant("1", "Test Plant", "Test Species", 0L, 7))
        
        // When
        composeTestRule.setContent {
            PlantLuxTheme {
                PlantScreenContent(
                    plants = plants,
                    isLoading = false,
                    onPlantClick = {}
                )
            }
        }
        
        // Then
        composeTestRule.onNodeWithText("Test Plant").assertIsDisplayed()
    }
    
    @Test
    fun plantScreen_showsLoading_whenLoading() {
        // When
        composeTestRule.setContent {
            PlantLuxTheme {
                PlantScreenContent(
                    plants = emptyList(),
                    isLoading = true,
                    onPlantClick = {}
                )
            }
        }
        
        // Then
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }
}
```

## 🔄 Git Workflow

### Flujo de Trabajo

1. **Crear una nueva rama**
   ```bash
   git checkout -b feature/nueva-funcionalidad
   ```

2. **Desarrollar la funcionalidad**
   - Seguir las convenciones de código
   - Escribir tests
   - Documentar cambios importantes

3. **Commit de cambios**
   ```bash
   git add .
   git commit -m "feat: agregar nueva funcionalidad de plantas"
   ```

4. **Push a la rama**
   ```bash
   git push origin feature/nueva-funcionalidad
   ```

5. **Crear Pull Request**
   - Describir los cambios
   - Incluir screenshots si es necesario
   - Solicitar review

### Convenciones de Commits

```
feat: nueva funcionalidad
fix: corrección de bug
docs: documentación
style: formato de código
refactor: refactorización
test: tests
chore: tareas de mantenimiento
```

## 🚨 Troubleshooting

### Problemas Comunes

#### 1. Error de Sincronización de Gradle
```bash
# Solución
./gradlew clean
./gradlew build
```

#### 2. Error de Compose Compiler
```kotlin
// Verificar en build.gradle.kts
android {
    kotlinOptions {
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
        )
    }
}
```

#### 3. Error de Hilt
```kotlin
// Verificar anotaciones
@HiltAndroidApp
class PlantLuxApp : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity()
```

#### 4. Error de Room
```kotlin
// Verificar entidades y DAOs
@Entity(tableName = "plants")
data class PlantEntity(...)

@Dao
interface PlantDao {
    // Métodos
}
```

## 📚 Recursos Adicionales

### Documentación Oficial
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Hilt](https://dagger.dev/hilt/)
- [Room](https://developer.android.com/training/data-storage/room)
- [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)

### Mejores Prácticas
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)
- [Material Design](https://material.io/design)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

### Herramientas Útiles
- [Android Studio](https://developer.android.com/studio)
- [Layout Inspector](https://developer.android.com/studio/debug/layout-inspector)
- [Database Inspector](https://developer.android.com/studio/inspect/database)

## 🤝 Contribución

### Antes de Contribuir
1. Leer la documentación del proyecto
2. Entender la arquitectura
3. Seguir las convenciones de código
4. Escribir tests para nuevas funcionalidades

### Proceso de Review
1. Código limpio y bien documentado
2. Tests pasando
3. Sin warnings de linting
4. Performance aceptable
5. Accesibilidad considerada

---

**¡Feliz desarrollo! 🌱✨**


