package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.RaceUiState
import com.example.ui.RaceViewModel

data class CarItem(
    val id: String,
    val name: String,
    val price: Int,
    val color: Color
)

val availableCars = listOf(
    CarItem("basic_car", "רכב פשוט", 0, Color(0xFFE57373)),
    CarItem("blue_racer", "מכונית מרוץ", 200, Color(0xFF1E88E5)),
    CarItem("green_jeep", "ג'יפ שטח", 500, Color(0xFF4CAF50)),
    CarItem("purple_monster", "משאית מפלצת", 1000, Color(0xFF9C27B0)),
    CarItem("space_car", "רכב חלל", 2000, Color(0xFF00BCD4))
)

fun getCarDrawable(id: String): Int {
    return when (id) {
        "basic_car" -> com.example.R.drawable.ic_car_basic
        "blue_racer" -> com.example.R.drawable.ic_car_racer
        "green_jeep" -> com.example.R.drawable.ic_car_jeep
        "purple_monster" -> com.example.R.drawable.ic_car_monster
        "space_car" -> com.example.R.drawable.ic_car_space
        else -> com.example.R.drawable.ic_car_basic
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarageScreen(
    viewModel: RaceViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val ownedItems by viewModel.ownedItems.collectAsStateWithLifecycle()
    
    val ownedIds = ownedItems.map { it.itemId }.toSet()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("מוסך וחנות") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "חזור")
                    }
                }
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
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("המטבעות שלך:", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "${profile.coins}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Text("רכבים", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(availableCars) { car ->
                            val isOwned = ownedIds.contains(car.id) || car.price == 0
                            val isEquipped = profile.currentCarId == car.id
                            
                            CarCard(
                                car = car,
                                isOwned = isOwned,
                                isEquipped = isEquipped,
                                canAfford = profile.coins >= car.price,
                                onAction = {
                                    if (isOwned) {
                                        viewModel.equipCar(car.id)
                                    } else {
                                        viewModel.buyItem(car.id, car.price)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CarCard(
    car: CarItem,
    isOwned: Boolean,
    isEquipped: Boolean,
    canAfford: Boolean,
    onAction: () -> Unit
) {
    val borderColor = if (isEquipped) MaterialTheme.colorScheme.primary else Color.Transparent
    val borderWidth = if (isEquipped) 3.dp else 0.dp
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onAction, enabled = isOwned || canAfford),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Visual block simulating a car
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = getCarDrawable(car.id)),
                    contentDescription = car.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
            }
            
            Text(car.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            
            if (isEquipped) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("נבחר", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                }
            } else if (isOwned) {
                Button(onClick = onAction, modifier = Modifier.height(32.dp), contentPadding = PaddingValues(0.dp)) {
                    Text("בחר")
                }
            } else {
                Button(
                    onClick = onAction,
                    enabled = canAfford,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("${car.price} קנה")
                }
            }
        }
    }
}
