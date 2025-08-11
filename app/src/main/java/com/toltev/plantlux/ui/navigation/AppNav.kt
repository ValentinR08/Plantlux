@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.toltev.plantlux.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.*
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.toltev.plantlux.ui.screens.measure.MeasureScreen
import com.toltev.plantlux.ui.screens.measure.MeasureViewModel
import com.toltev.plantlux.ui.screens.spots.SpotsScreen
import com.toltev.plantlux.ui.screens.spots.SpotsViewModel
import com.toltev.plantlux.ui.screens.spots.SpotDetailScreen
import com.toltev.plantlux.ui.screens.species.SpeciesScreen
import com.toltev.plantlux.ui.screens.species.SpeciesViewModel
import com.toltev.plantlux.ui.screens.species.SpeciesDetailScreen
import com.toltev.plantlux.ui.screens.settings.SettingsScreen
import com.toltev.plantlux.ui.screens.settings.SettingsViewModel

object Routes {
    const val MEASURE = "measure"
    const val SPOTS = "spots"
    const val SPOT_DETAIL = "spot_detail/{spotId}"
    const val SPECIES = "species"
    const val SPECIES_DETAIL = "species_detail/{speciesId}"
    const val SETTINGS = "settings"
}

// Navegación principal + TopAppBar + BottomBar
@Composable
fun AppNav(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(appBarTitleFor(currentDestination?.route)) },
                actions = {
                    when (currentDestination?.route) {
                        Routes.SPOTS -> {
                            // Acción de exportar global desde spots (opcional)
                            IconButton(onClick = { /* TODO: lanzar export global via ViewModel o navegar a ajustes */ }) {
                                Icon(Icons.Default.Share, contentDescription = "Exportar")
                            }
                        }
                        Routes.SPECIES -> {
                            // Acción de búsqueda en especies (si aplica)
                            IconButton(onClick = { /* TODO: abrir búsqueda */ }) {
                                Icon(Icons.Default.Search, contentDescription = "Buscar especie")
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    NavItem("Medir", Routes.MEASURE, Icons.Outlined.Bolt),
                                NavItem("Spots", Routes.SPOTS, Icons.Outlined.List),
                    NavItem("Especies", Routes.SPECIES, Icons.Outlined.Spa),
                    NavItem("Ajustes", Routes.SETTINGS, Icons.Outlined.Settings)
                )
                items.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.MEASURE,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.MEASURE) {
                val vm: MeasureViewModel = hiltViewModel()
                MeasureScreen(viewModel = vm)
            }
            composable(Routes.SPOTS) {
                val vm: SpotsViewModel = hiltViewModel()
                SpotsScreen(
                    viewModel = vm,
                    onSpotClick = { spotId -> navController.navigate("spot_detail/$spotId") }
                )
            }
            composable(Routes.SPOT_DETAIL) { backStackEntry ->
                val spotId = backStackEntry.arguments?.getString("spotId")?.toLongOrNull() ?: return@composable
                val vm: SpotsViewModel = hiltViewModel()
                SpotDetailScreen(
                    spotId = spotId,
                    viewModel = vm,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SPECIES) {
                val vm: SpeciesViewModel = hiltViewModel()
                SpeciesScreen(
                    viewModel = vm,
                    onSpeciesClick = { speciesId -> navController.navigate("species_detail/$speciesId") }
                )
            }
            composable(Routes.SPECIES_DETAIL) { backStackEntry ->
                val speciesId = backStackEntry.arguments?.getString("speciesId")?.toLongOrNull() ?: return@composable
                val vm: SpeciesViewModel = hiltViewModel()
                SpeciesDetailScreen(
                    speciesId = speciesId,
                    viewModel = vm,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SETTINGS) {
                val vm: SettingsViewModel = hiltViewModel()
                SettingsScreen(viewModel = vm)
            }
        }
    }
}

data class NavItem(val label: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private fun appBarTitleFor(route: String?): String = when {
    route == null -> "PlantLux"
    route.startsWith("spot_detail") -> "Detalle"
    route.startsWith("species_detail") -> "Especie"
    else -> when (route) {
        Routes.MEASURE -> "Medir"
        Routes.SPOTS -> "Spots"
        Routes.SPECIES -> "Especies"
        Routes.SETTINGS -> "Ajustes"
        else -> "PlantLux"
    }
}
