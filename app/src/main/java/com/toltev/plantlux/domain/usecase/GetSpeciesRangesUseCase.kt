package com.toltev.plantlux.domain.usecase

import com.toltev.plantlux.data.repo.SpeciesRepository
import com.toltev.plantlux.domain.model.Species
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Caso de uso para obtener los rangos de luz de las especies
class GetSpeciesRangesUseCase(private val speciesRepository: SpeciesRepository) {
    fun execute(): Flow<List<Species>> =
        speciesRepository.getAllSpecies().map { list ->
            list.map { e ->
                Species(
                    id = e.id,
                    name = e.name,
                    lowFrom = e.lowFrom,
                    lowTo = e.lowTo,
                    midFrom = e.midFrom,
                    midTo = e.midTo,
                    highFrom = e.highFrom,
                    highTo = e.highTo,
                    description = e.description,
                    editable = e.editable
                )
            }
        }
}
