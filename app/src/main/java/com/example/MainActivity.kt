package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.RaceRepository
import com.example.ui.RaceViewModel
import com.example.ui.ViewModelFactory
import com.example.ui.screens.GarageScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RaceScreen
import com.example.ui.theme.MyApplicationTheme

import com.example.ui.screens.SetupScreen
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.RaceUiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                MyApplicationTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize().systemBarsPadding(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        HomeworkRaceApp()
                    }
                }
            }
        }
    }
}

@Composable
fun HomeworkRaceApp() {
    val context = LocalContext.current
    val appRepository = androidx.compose.runtime.remember { RaceRepository(context) }
    val viewModel: RaceViewModel = viewModel(factory = ViewModelFactory(appRepository))
    val navController = rememberNavController()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is RaceUiState.NeedsSetup) {
            navController.navigate("setup") {
                popUpTo("loading") { inclusive = true }
            }
        } else if (uiState is RaceUiState.Success) {
            val dest = navController.currentDestination?.route
            if (dest == "setup" || dest == "loading" || dest == null) {
                navController.navigate("home") {
                    popUpTo("loading") { inclusive = true }
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = "loading") {
        composable("loading") {
            androidx.compose.foundation.layout.Box(
                Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator()
            }
        }
        composable("setup") {
            SetupScreen(viewModel = viewModel)
        }
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToRace = { navController.navigate("race") },
                onNavigateToGarage = { navController.navigate("garage") }
            )
        }
        composable("race") {
            RaceScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("garage") {
            GarageScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onEditCar = { carId -> navController.navigate("studio/$carId") }
            )
        }
        composable("studio/{carId}") { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: "basic_car"
            com.example.ui.screens.StudioScreen(
                viewModel = viewModel,
                carId = carId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
