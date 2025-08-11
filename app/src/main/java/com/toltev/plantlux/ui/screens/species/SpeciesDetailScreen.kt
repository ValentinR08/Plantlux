package com.toltev.plantlux.ui.screens.species

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Pantalla de detalle y edición de una especie
@Composable
fun SpeciesDetailScreen(
    speciesId: Long,
    viewModel: SpeciesViewModel,
    onBack: () -> Unit
) {
    // Simplificación: derivar desde la lista actual
    val species = viewModel.speciesList.collectAsState().value.find { it.id == speciesId }
    var lowFrom by remember { mutableStateOf(species?.lowFrom?.toString() ?: "") }
    var lowTo by remember { mutableStateOf(species?.lowTo?.toString() ?: "") }
    var midFrom by remember { mutableStateOf(species?.midFrom?.toString() ?: "") }
    var midTo by remember { mutableStateOf(species?.midTo?.toString() ?: "") }
    var highFrom by remember { mutableStateOf(species?.highFrom?.toString() ?: "") }
    var highTo by remember { mutableStateOf(species?.highTo?.toString() ?: "") }
    var description by remember { mutableStateOf(species?.description ?: "") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Detalle de especie", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = lowFrom,
                onValueChange = { lowFrom = it },
                label = { Text("Baja desde") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = lowTo,
                onValueChange = { lowTo = it },
                label = { Text("Baja hasta") },
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = midFrom,
                onValueChange = { midFrom = it },
                label = { Text("Media desde") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = midTo,
                onValueChange = { midTo = it },
                label = { Text("Media hasta") },
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = highFrom,
                onValueChange = { highFrom = it },
                label = { Text("Alta desde") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = highTo,
                onValueChange = { highTo = it },
                label = { Text("Alta hasta") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* TODO: persistir cambios en DataStore/Room */ }) {
            Text("Guardar cambios")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onBack) {
            Text("Volver")
        }
    }
}
