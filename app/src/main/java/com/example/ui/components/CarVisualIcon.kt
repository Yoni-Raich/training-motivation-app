package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.getCarDrawable

data class UniqueCarVisuals(
    val defaultColor: Color,
    val decalEmoji: String,
    val exhaustEmoji: String,
    val auraColor: Color?,
    val spoiler: String
)

fun getCarUniqueVisuals(carId: String): UniqueCarVisuals {
    val hash = carId.hashCode()
    val absHash = kotlin.math.abs(hash)
    
    val colors = listOf(
        Color(0xFFFF5252), // אדום זועם
        Color(0xFFE91E63), // ורוד מסטיק
        Color(0xFF9C27B0), // סגול מלכותי
        Color(0xFF673AB7), // ויולט סילון
        Color(0xFF3F51B5), // כחול עמוק
        Color(0xFF2196F3), // כחול בהיר
        Color(0xFF03A9F4), // תכול שמיים
        Color(0xFF00BCD4), // טורקיז זוהר
        Color(0xFF009688), // ירוק בקבוק
        Color(0xFF4CAF50), // ירוק דשא
        Color(0xFF8BC34A), // פיסטוק בהיר
        Color(0xFFCDDC39), // ליים חומצי
        Color(0xFFFFEB3B), // צהוב מבריק
        Color(0xFFFFC107), // זהב טהור
        Color(0xFFFF9800), // כתום להבה
        Color(0xFFFF5722), // אדום תפוז
        Color(0xFF795548), // חום שוקולד
        Color(0xFF9E9E9E), // כסף מטאלי
        Color(0xFF607D8B), // פלדה כהה
        Color(0xFF00E5FF), // טורקיז עתידני
        Color(0xFFFF007F)  // פוקסיה ניאון
    )
    val color = colors[absHash % colors.size]

    val stickers = listOf(
        "⚡", "🏁", "🔥", "⭐", "🚀", "👑", "🍕", "🍩", "💎", "🎯",
        "🦕", "🦁", "🦈", "👾", "👽", "🛸", "☄️", "💀", "🍀", "🦅",
        "🌪️", "🌈", "🎈", "🕶️", "☀️", "🌙", "⚔️", "🛡️", "🧬", "🔮"
    )
    val decal = stickers[absHash % stickers.size]

    val staticAuras = listOf(
        Color(0x99FF5252), Color(0x994CAF50), Color(0x992196F3),
        Color(0x99FFEB3B), Color(0x99E91E63), Color(0x9900BCD4),
        Color(0x99FF9800), Color(0x999C27B0)
    )
    val aura = staticAuras[absHash % staticAuras.size]

    val exhausts = listOf("💨", "✨", "🔥", "💭", "☄️", "💨", "✨", "🔥")
    val exhaust = exhausts[absHash % exhausts.size]

    val spoiler = when (absHash % 4) {
        0 -> "🔺" // ספוילר זוויתי
        1 -> "⚡" // אנטנת ברק
        2 -> "🔱" // כנף כפולה
        else -> ""
    }

    return UniqueCarVisuals(
        defaultColor = color,
        decalEmoji = decal,
        exhaustEmoji = exhaust,
        auraColor = if (carId.contains("space") || carId.contains("monster") || carId.contains("jeep") || absHash % 2 == 0) aura else null,
        spoiler = spoiler
    )
}

@Composable
fun CarVisualIcon(
    carId: String,
    currentUpgradeColorArgb: Int,
    modifier: Modifier = Modifier,
    overrideBaseColor: Color? = null,
    showAura: Boolean = true
) {
    val visuals = getCarUniqueVisuals(carId)
    val baseColor = overrideBaseColor ?: visuals.defaultColor
    val activeColor = if (currentUpgradeColorArgb != 0) Color(currentUpgradeColorArgb) else baseColor

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Underglow Aura Shadow if applicable and requested
        if (showAura && visuals.cameraAuraGlow() != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize(0.85f)
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        spotColor = visuals.cameraAuraGlow() ?: Color.Transparent,
                        ambientColor = visuals.cameraAuraGlow() ?: Color.Transparent
                    )
                    .background(visuals.cameraAuraGlow()?.copy(alpha = 0.25f) ?: Color.Transparent, CircleShape)
            )
        }

        // Inside layout containing accessories + car + decals
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Spoiler Wing Accessory on top-left / back of car
            if (visuals.spoiler.isNotEmpty()) {
                Text(
                    text = visuals.spoiler,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 10.dp, y = 14.dp)
                )
            }

            // Main Car Image Asset
            Image(
                painter = painterResource(id = getCarDrawable(carId)),
                contentDescription = "Car: $carId",
                modifier = Modifier
                    .fillMaxSize(0.85f)
                    .align(Alignment.Center),
                colorFilter = ColorFilter.tint(activeColor, androidx.compose.ui.graphics.BlendMode.Modulate)
            )

            // Dynamic Unique Decal/Sticker Badge centered on the door of the car
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = 6.dp, y = 2.dp)
                    .background(Color.White.copy(alpha = 0.85f), shape = CircleShape)
                    .border(1.dp, activeColor.copy(alpha = 0.7f), CircleShape)
                    .padding(2.dp)
            ) {
                Text(
                    text = visuals.decalEmoji,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Neon Underlight Tube line
            if (showAura && visuals.cameraAuraGlow() != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-4).dp)
                        .fillMaxWidth(0.5f)
                        .height(3.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    visuals.cameraAuraGlow() ?: Color.Transparent,
                                    Color.Transparent
                                )
                            ),
                            shape = RoundedCornerShape(1.dp)
                        )
                )
            }
        }
    }
}

// Extension to bridge names
fun UniqueCarVisuals.cameraAuraGlow(): Color? = auraColor
