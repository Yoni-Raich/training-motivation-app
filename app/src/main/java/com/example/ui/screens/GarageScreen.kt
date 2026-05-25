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
    // --- 10 STARTER VEHICLES (0 - 150 coins) ---
    CarItem("basic_car", "רכב פשוט", 0, Color(0xFFE57373)),
    CarItem("basic_yellow", "ברק צהוב", 50, Color(0xFFFFEB3B)),
    CarItem("basic_grey", "חץ אפור", 80, Color(0xFF9E9E9E)),
    CarItem("basic_blue", "נחש כחול", 100, Color(0xFF2196F3)),
    CarItem("basic_white", "לבן בוהק", 120, Color(0xFFE0E0E0)),
    CarItem("basic_green", "עלה ירוק", 150, Color(0xFF4CAF50)),
    CarItem("basic_orange", "אש תפוז", 150, Color(0xFFFF9800)),
    CarItem("basic_pink", "מסטיק ורוד", 150, Color(0xFFE91E63)),
    CarItem("basic_teal", "סערת טורקיז", 150, Color(0xFF009688)),
    CarItem("basic_violet", "מנצח סגול", 150, Color(0xFF673AB7)),

    // --- 10 ADVANCED RACERS (200 - 450 coins) ---
    CarItem("blue_racer", "מכונית מרוץ", 200, Color(0xFF1E88E5)),
    CarItem("racer_green", "נמר דשא", 250, Color(0xFF81C784)),
    CarItem("racer_orange", "לבה כתומה", 280, Color(0xFFFF7043)),
    CarItem("racer_cyan", "גל תכלת", 300, Color(0xFF4DD0E1)),
    CarItem("racer_red", "פרארי אדומה", 350, Color(0xFFE53935)),
    CarItem("racer_black", "צל שחור", 400, Color(0xFF212121)),
    CarItem("racer_pink", "מרוץ ברבי", 400, Color(0xFFF06292)),
    CarItem("racer_gold", "זהב רויאל", 450, Color(0xFFFFD54F)),
    CarItem("racer_sunset", "זריחת מדבר", 450, Color(0xFFFF8A65)),
    CarItem("racer_neon", "ברק ניאון", 450, Color(0xFFB2FF59)),

    // --- 10 ROUGH TERRAIN JEEPS (500 - 950 coins) ---
    CarItem("green_jeep", "ג'יפ שטח", 500, Color(0xFF4CAF50)),
    CarItem("jeep_sand", "סייר המדבר", 550, Color(0xFFE0C068)),
    CarItem("jeep_snow", "זאב השלג", 600, Color(0xFFE0F7FA)),
    CarItem("jeep_swamp", "סייד הביצות", 650, Color(0xFF33691E)),
    CarItem("jeep_canyon", "טפסן הסלעים", 700, Color(0xFF8D6E63)),
    CarItem("jeep_safari", "רכב ספארי", 750, Color(0xFFFFCC80)),
    CarItem("jeep_military", "שטח קומנדו", 800, Color(0xFF558B2F)),
    CarItem("jeep_volcanic", "לוע יוקון", 850, Color(0xFFBF360C)),
    CarItem("jeep_shadow", "פומה לילית", 900, Color(0xFF37474F)),
    CarItem("jeep_diamond", "יהלום שקוף", 950, Color(0xFFE0F2F1)),

    // --- 10 HEAVY MONSTER TRUCKS (1000 - 1900 coins) ---
    CarItem("purple_monster", "משאית מפלצת", 1000, Color(0xFF9C27B0)),
    CarItem("monster_flame", "שור זועם", 1100, Color(0xFFD84315)),
    CarItem("monster_skull", "שן דרקון", 1200, Color(0xFF4E342E)),
    CarItem("monster_ice", "קרחון מוחץ", 1300, Color(0xFF80DEEA)),
    CarItem("monster_toxic", "רעל ירוק", 1400, Color(0xFF00E676)),
    CarItem("monster_beast", "חיית הפרא", 1550, Color(0xFF3E2723)),
    CarItem("monster_ocean", "מגלש המצולות", 1700, Color(0xFF0277BD)),
    CarItem("monster_thunder", "ברק המפלצת", 1800, Color(0xFF651FFF)),
    CarItem("monster_earth", "רעש אדמה", 1850, Color(0xFF5D4037)),
    CarItem("monster_goliath", "גוליית מוחץ", 1900, Color(0xFF37474F)),

    // --- 10 GALACTIC SPACE CRAFTS (2000 - 5000 coins) ---
    CarItem("space_car", "רכב חלל", 2000, Color(0xFF00BCD4)),
    CarItem("space_gravity", "ריידר חלל", 2200, Color(0xFFE040FB)),
    CarItem("space_warp", "מנוע עיוות", 2500, Color(0xFF651FFF)),
    CarItem("space_comet", "שביט מהיר", 2800, Color(0xFF00E5FF)),
    CarItem("space_nebula", "ערפילית סגולה", 3100, Color(0xFFD500F9)),
    CarItem("space_plasma", "רובה פלזמה", 3500, Color(0xFFFF1744)),
    CarItem("space_eclipse", "ליקוי חמה", 4000, Color(0xFF263238)),
    CarItem("space_quantum", "מזנק קוונטים", 4250, Color(0xFF00B0FF)),
    CarItem("space_void", "נוסע בריק", 4500, Color(0xFF000000)),
    CarItem("space_supernova", "סופרנובה אינסופית", 5000, Color(0xFFFF3D00))
)

fun getCarDrawable(id: String): Int {
    return when {
        id.contains("racer") || id.contains("race") -> com.example.R.drawable.ic_car_racer
        id.contains("jeep") || id.contains("crawler") || id.contains("buggy") -> com.example.R.drawable.ic_car_jeep
        id.contains("monster") || id.contains("truck") -> com.example.R.drawable.ic_car_monster
        id.contains("space") || id.contains("ufo") || id.contains("rocket") || id.contains("cosmic") -> com.example.R.drawable.ic_car_space
        else -> com.example.R.drawable.ic_car_basic
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarageScreen(
    viewModel: RaceViewModel,
    onBack: () -> Unit,
    onEditCar: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val ownedItems by viewModel.ownedItems.collectAsStateWithLifecycle()
    val carUpgrades by viewModel.carUpgrades.collectAsStateWithLifecycle()
    
    val ownedIds = ownedItems.map { it.itemId }.toSet()
    val upgradeMap = remember(carUpgrades) { carUpgrades.associateBy { it.carId } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("מוסך וחנות רכבים") },
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
                    Text("נא לבצע הגדרה תחילה...")
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("המטבעות שלך:", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "🪙 ${profile.coins}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Text(
                        "קטלוג מוסך הרכבים (${availableCars.size} רכבים זמינים)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        items(availableCars) { car ->
                            val isOwned = ownedIds.contains(car.id) || car.price == 0
                            val isEquipped = profile.currentCarId == car.id
                            val upgrade = upgradeMap[car.id]
                            
                            CarCard(
                                car = car,
                                upgradeColorArgb = upgrade?.colorArgb ?: 0,
                                isOwned = isOwned,
                                isEquipped = isEquipped,
                                canAfford = profile.coins >= car.price,
                                onAction = {
                                    if (isOwned) {
                                        viewModel.equipCar(car.id)
                                    } else {
                                        viewModel.buyItem(car.id, car.price)
                                    }
                                },
                                onEditClick = {
                                    onEditCar(car.id)
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
    upgradeColorArgb: Int,
    isOwned: Boolean,
    isEquipped: Boolean,
    canAfford: Boolean,
    onAction: () -> Unit,
    onEditClick: () -> Unit
) {
    val borderColor = if (isEquipped) MaterialTheme.colorScheme.primary else Color.Transparent
    val borderWidth = if (isEquipped) 3.dp else 0.dp
    
    val carColor = if (upgradeColorArgb != 0) Color(upgradeColorArgb) else car.color

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(borderWidth, borderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Visual block simulating a car
            com.example.ui.components.CarVisualIcon(
                carId = car.id,
                currentUpgradeColorArgb = upgradeColorArgb,
                overrideBaseColor = car.color,
                modifier = Modifier.size(80.dp)
            )
            
            Spacer(Modifier.height(4.dp))
            Text(car.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1)
            Spacer(Modifier.height(8.dp))

            if (isEquipped) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("נבחר", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                }
                
                Spacer(Modifier.height(8.dp))
                
                Button(
                    onClick = onEditClick,
                    modifier = Modifier.height(32.dp).fillMaxWidth(),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("🛠️ סטודיו", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            } else if (isOwned) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onAction,
                        modifier = Modifier.height(32.dp).weight(1.1f),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("בחר", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onEditClick,
                        modifier = Modifier.height(32.dp).weight(0.9f),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("🛠️", style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else {
                Button(
                    onClick = onAction,
                    enabled = canAfford,
                    modifier = Modifier.height(32.dp).fillMaxWidth(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("${car.price} 🪙", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
