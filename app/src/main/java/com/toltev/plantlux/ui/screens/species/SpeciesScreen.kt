package com.toltev.plantlux.ui.screens.species

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Pantalla de catálogo de especies
@Composable
fun SpeciesScreen(
    viewModel: SpeciesViewModel,
    onSpeciesClick: (Long) -> Unit
) {
    val speciesList by viewModel.speciesList.collectAsState()
    var search by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Catálogo de especies",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = search,
            onValueChange = { search = it; viewModel.onSearch(it) },
            label = { Text("Buscar especie") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar especie"
                )
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (speciesList.isEmpty()) {
            // Estado vacío con ilustración y mensaje
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Sin especies",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No se encontraron especies.",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Intenta buscar otro nombre o revisa la base de datos.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        } else {
            LazyColumn {
                items(speciesList) { species ->
                    SpeciesItem(
                        name = species.name,
                        description = species.description,
                        low = "${species.lowFrom}-${species.lowTo} lux",
                        mid = "${species.midFrom}-${species.midTo} lux",
                        high = "${species.highFrom}-${species.highTo} lux",
                        onClick = { onSpeciesClick(species.id) }
                    )
                                            HorizontalDivider()
                }
            }
        }
    }
}

// Composable para mostrar una especie en la lista
@Composable
fun SpeciesItem(name: String, description: String, low: String, mid: String, high: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
            .semantics { contentDescription = "Especie: $name" }
    ) {
        Text(
            name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        )
        Text(
            description,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
        )
        Row(modifier = Modifier.padding(top = 4.dp)) {
            Text(
                "Baja: $low",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Media: $mid",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.secondary, fontSize = 13.sp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Alta: $high",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.tertiary, fontSize = 13.sp)
            )
        }
    }
}
