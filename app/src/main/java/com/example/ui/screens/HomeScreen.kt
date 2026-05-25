package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.PointEvent
import com.example.data.RaceUiState
import com.example.ui.RaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: RaceViewModel,
    onNavigateToRace: () -> Unit,
    onNavigateToGarage: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val events by viewModel.recentEvents.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("מרוץ ההצלחות", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is RaceUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is RaceUiState.NeedsSetup -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading Setup...")
                }
            }
            is RaceUiState.Success -> {
                val profile = state.profile
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(24.dp))
                    
                    // Profile Header Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(profile.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ChipInfo("רמה ${profile.level}", Icons.Default.Star)
                                Spacer(Modifier.width(16.dp))
                                ChipInfo("${profile.coins} מטבעות", Icons.Default.Star)
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(32.dp))
                    
                    // Action Buttons
                    Button(
                        onClick = onNavigateToRace,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .testTag("start_homework_btn"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("התחל שיעורי בית", style = MaterialTheme.typography.titleLarge)
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    OutlinedButton(
                        onClick = onNavigateToGarage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("garage_button"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("מוסך וחנות רכבים", style = MaterialTheme.typography.titleMedium)
                    }
                    
                    Spacer(Modifier.height(32.dp))
                    
                    // Recent History
                    Text(
                        "היסטוריה אחרונה",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.height(8.dp))
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(events) { event ->
                            HistoryItemRow(event)
                        }
                        if (events.isEmpty()) {
                            item {
                                Text("עדיין אין נקודות. בואו נתחיל!", modifier = Modifier.padding(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChipInfo(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(text, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun HistoryItemRow(event: PointEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(event.note, style = MaterialTheme.typography.bodyLarge)
            Text(
                "+${event.points}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
