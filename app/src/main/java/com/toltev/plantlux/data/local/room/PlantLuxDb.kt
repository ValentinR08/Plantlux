package com.toltev.plantlux.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.toltev.plantlux.data.local.room.dao.ReadingDao
import com.toltev.plantlux.data.local.room.dao.SpotDao
import com.toltev.plantlux.data.local.room.dao.SpeciesDao
import com.toltev.plantlux.data.local.room.entities.ReadingEntity
import com.toltev.plantlux.data.local.room.entities.SpotEntity
import com.toltev.plantlux.data.local.room.entities.SpeciesEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Base de datos principal de la app PlantLux
@Database(
    entities = [SpotEntity::class, ReadingEntity::class, SpeciesEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PlantLuxDb : RoomDatabase() {
    abstract fun spotDao(): SpotDao
    abstract fun readingDao(): ReadingDao
    abstract fun speciesDao(): SpeciesDao

    companion object {
        @Volatile
        private var INSTANCE: PlantLuxDb? = null

        fun getInstance(context: Context): PlantLuxDb = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

        private fun buildDatabase(context: Context): PlantLuxDb =
            Room.databaseBuilder(context, PlantLuxDb::class.java, "plantlux.db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Semilla de especies al crear la base de datos
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).speciesDao().insertAll(SpeciesSeed.speciesList)
                        }
                    }
                })
                .build()
    }
}

// Objeto con la semilla de especies y sus rangos recomendados
object SpeciesSeed {
    val speciesList = listOf(
        SpeciesEntity(name = "Sansevieria (Lengua de suegra)", lowFrom = 50, lowTo = 150, midFrom = 150, midTo = 400, highFrom = 400, highTo = 800, description = "Muy resistente, ideal para poca luz."),
        SpeciesEntity(name = "Potos (Epipremnum)", lowFrom = 75, lowTo = 200, midFrom = 200, midTo = 500, highFrom = 500, highTo = 1000, description = "Planta colgante adaptable."),
        SpeciesEntity(name = "Monstera deliciosa", lowFrom = 100, lowTo = 250, midFrom = 250, midTo = 600, highFrom = 600, highTo = 1200, description = "Prefiere luz filtrada."),
        SpeciesEntity(name = "Ficus lyrata", lowFrom = 200, lowTo = 400, midFrom = 400, midTo = 800, highFrom = 800, highTo = 1500, description = "Necesita buena luz indirecta."),
        SpeciesEntity(name = "Suculentas genéricas", lowFrom = 300, lowTo = 600, midFrom = 600, midTo = 1500, highFrom = 1500, highTo = 3000, description = "Requieren mucha luz."),
        SpeciesEntity(name = "Cactus genéricos", lowFrom = 500, lowTo = 1000, midFrom = 1000, midTo = 3000, highFrom = 3000, highTo = 10000, description = "Pleno sol preferido.")
    )
}
