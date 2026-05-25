package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.RaceUiState
import com.example.ui.RaceViewModel

data class CustomPaintOption(
    val name: String,
    val color: Color,
    val isPremium: Boolean,
    val price: Int
)

val paintOptions = listOf(
    CustomPaintOption("צבע ברירת מחדל", Color.Transparent, false, 0),
    CustomPaintOption("אדום אש", Color(0xFFFF3333), false, 0),
    CustomPaintOption("כחול ספורט", Color(0xFF3366FF), false, 0),
    CustomPaintOption("ירוק ליים", Color(0xFF33CC33), false, 0),
    CustomPaintOption("כתום זריחה", Color(0xFFFF9900), false, 0),
    CustomPaintOption("ורוד ורוד", Color(0xFFFF66CC), false, 0),
    CustomPaintOption("תכלת אקווה", Color(0xFF33CCCC), false, 0),
    // Premium paints (require coins to buy/apply)
    CustomPaintOption("זהב מלכותי 👑", Color(0xFFFFD700), true, 100),
    CustomPaintOption("סגול פלזמה 👾", Color(0xFF9D4EDD), true, 120),
    CustomPaintOption("ניאון עתידני 🔋", Color(0xFF00F5D4), true, 150),
    CustomPaintOption("פחמן כהה 🌑", Color(0xFF343A40), true, 80)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen(
    viewModel: RaceViewModel,
    carId: String,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val carUpgrades by viewModel.carUpgrades.collectAsStateWithLifecycle()
    
    val currentUpgrade = carUpgrades.find { it.carId == carId } ?: com.example.data.CarUpgrade(carId = carId)
    val carItem = availableCars.find { it.id == carId } ?: CarItem(carId, "חללית הבית", 0, Color.LightGray)

    var snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Upgrade costs
    val engineUpgradePrice = 120
    val turboUpgradePrice = 150
    val wheelsUpgradePrice = 90

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("סטודיו לעיצוב ושיפורים") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "חזור")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is RaceUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is RaceUiState.NeedsSetup -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("נא לבצע הגדרת פרופיל תחילה")
                }
            }
            is RaceUiState.Success -> {
                val profile = state.profile
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Card with Kid's coin status
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = carItem.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "משפרים ומעצבים את המכונית!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("מטבעות ברשותך", style = MaterialTheme.typography.labelMedium)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "🪙 ${profile.coins}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    // Large glowing car preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        com.example.ui.components.CarVisualIcon(
                            carId = carId,
                            currentUpgradeColorArgb = currentUpgrade.colorArgb,
                            overrideBaseColor = carItem.color,
                            modifier = Modifier
                                .fillMaxHeight(0.7f)
                                .aspectRatio(1.5f),
                            showAura = true
                        )
                        
                        // Active indicator icons
                        Row(
                            Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (currentUpgrade.engineLevel > 1) {
                                Badge(containerColor = Color(0xFFFFB300)) { Text("🔧 מנוע L${currentUpgrade.engineLevel}") }
                            }
                            if (currentUpgrade.turboLevel > 1) {
                                Badge(containerColor = Color(0xFFFF5252)) { Text("🔥 טורבו") }
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))

                    // 1. PERFORMANCE UPGRADE AREA
                    Text(
                        text = "🛠️ שדרוג ביצועים",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Engine Upgrade
                            UpgradeRow(
                                name = "מנוע סילון",
                                currentLevel = currentUpgrade.engineLevel,
                                upgradePrice = engineUpgradePrice,
                                hasMaxed = currentUpgrade.engineLevel >= 5,
                                coins = profile.coins,
                                icon = "🔧",
                                onUpgrade = {
                                    viewModel.buyCarUpgrade(carId, "engine", engineUpgradePrice)
                                }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            // Turbo Upgrade
                            UpgradeRow(
                                name = "מאיץ טורבו",
                                currentLevel = currentUpgrade.turboLevel,
                                upgradePrice = turboUpgradePrice,
                                hasMaxed = currentUpgrade.turboLevel >= 5,
                                coins = profile.coins,
                                icon = "🔥",
                                onUpgrade = {
                                    viewModel.buyCarUpgrade(carId, "turbo", turboUpgradePrice)
                                }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            // Tires Upgrade
                            UpgradeRow(
                                name = "צמיגי ספורט אחיזה",
                                currentLevel = currentUpgrade.wheelsLevel,
                                upgradePrice = wheelsUpgradePrice,
                                hasMaxed = currentUpgrade.wheelsLevel >= 5,
                                coins = profile.coins,
                                icon = "⚙️",
                                onUpgrade = {
                                    viewModel.buyCarUpgrade(carId, "wheels", wheelsUpgradePrice)
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // 2. PAINT SHOP
                    Text(
                        text = "🎨 בית מלאכה לצבעים",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.height(8.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "בחר צבע עבור הרכב שלך. צבעים מלכותיים וניאון דורשים תשלום קטן להרכבה!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(16.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                paintOptions.chunked(4).forEach { rowOptions ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        rowOptions.forEach { option ->
                                            val isSelected = if (option.color == Color.Transparent) {
                                                currentUpgrade.colorArgb == 0
                                            } else {
                                                currentUpgrade.colorArgb == option.color.toArgb()
                                            }
                                            
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(
                                                        if (isSelected) MaterialTheme.colorScheme.secondaryContainer 
                                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                                    )
                                                    .border(
                                                        width = if (isSelected) 2.dp else 1.dp,
                                                        color = if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent,
                                                        shape = RoundedCornerShape(12.dp)
                                                    )
                                                    .clickable {
                                                        if (option.isPremium && !isSelected) {
                                                            if (profile.coins >= option.price) {
                                                                viewModel.buyCarPaint(carId, option.color.toArgb(), option.price)
                                                            }
                                                        } else {
                                                            // Standard paint applies immediately (or reset 0)
                                                            viewModel.buyCarPaint(carId, option.color.toArgb(), 0)
                                                        }
                                                    }
                                                    .padding(8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    // Little color circle preview
                                                    Box(
                                                        modifier = Modifier
                                                            .size(32.dp)
                                                            .clip(CircleShape)
                                                            .background(if (option.color == Color.Transparent) Color.LightGray else option.color)
                                                            .border(1.dp, Color.White, CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        if (option.color == Color.Transparent) {
                                                            Text("❌", fontSize = 10.sp)
                                                        } else if (isSelected) {
                                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                        }
                                                    }
                                                    Spacer(Modifier.height(4.dp))
                                                    Text(
                                                        option.name,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                        maxLines = 1
                                                    )
                                                    if (option.isPremium && !isSelected) {
                                                        Text(
                                                            "${option.price} 🪙",
                                                            fontSize = 10.sp,
                                                            color = MaterialTheme.colorScheme.primary,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    } else if (option.isPremium) {
                                                        Text(
                                                            "נרכש",
                                                            fontSize = 10.sp,
                                                            color = MaterialTheme.colorScheme.secondary
                                                        )
                                                    } else {
                                                        Text(
                                                            "חינם",
                                                            fontSize = 10.sp,
                                                            color = Color.Gray
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UpgradeRow(
    name: String,
    currentLevel: Int,
    upgradePrice: Int,
    hasMaxed: Boolean,
    coins: Int,
    icon: String,
    onUpgrade: () -> Unit
) {
    val canAfford = coins >= upgradePrice

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Text(name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(6.dp))
            // Render 5 progress blocks indicatings level
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (i in 1..5) {
                    val isActive = i <= currentLevel
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isActive) MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (hasMaxed) "רמה מקסימלית! 🌟" else "רמה: $currentLevel מתוך 5",
                style = MaterialTheme.typography.bodySmall,
                color = if (hasMaxed) MaterialTheme.colorScheme.secondary else Color.Gray
            )
        }
        
        Spacer(Modifier.width(16.dp))

        if (hasMaxed) {
            Button(
                onClick = {},
                enabled = false,
                colors = ButtonDefaults.buttonColors(disabledContainerColor = Color.LightGray)
            ) {
                Text("מקס ⚡")
            }
        } else {
            Button(
                onClick = onUpgrade,
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("שפר רמה")
                    Text("$upgradePrice 🪙", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
