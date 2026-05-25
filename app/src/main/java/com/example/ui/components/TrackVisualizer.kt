package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.ui.screens.getCarDrawable
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.abs

fun getBiomeName(index: Int): String {
    return when (index) {
        0 -> "🏡 עמק ירוק"
        1 -> "🌵 מדבר זהוב"
        2 -> "❄️ שלג חורפי"
        3 -> "👾 סייברפאנק"
        4 -> "🚀 חלל עמוק"
        5 -> "🍭 ארץ הממתקים"
        6 -> "🐙 מצולות הים"
        7 -> "🌋 עולם הלבה"
        8 -> "🌅 חוף השקיעה"
        9 -> "🌿 ג'ונגל טרופי"
        10 -> "🏰 ממלכת האבירים"
        11 -> "☁️ עיר העננים"
        12 -> "🍁 יער שלכת"
        13 -> "⚙️ עולם הקיטור"
        14 -> "🛕 מצרים העתיקה"
        else -> "🏡 עמק ירוק"
    }
}

@Composable
fun SimpleTrack(
    currentPoints: Int,
    targetPoints: Int,
    carId: String,
    modifier: Modifier = Modifier,
    carColor: Color = Color.Unspecified,
    engineLevel: Int = 1,
    turboLevel: Int = 1,
    wheelsLevel: Int = 1
) {
    val progress = (currentPoints.toFloat() / targetPoints.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    
    // Animate progress smoothly
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progress"
    )

    // Infinite ambient tick timer for live waving/flowing animations in the biomes
    val infiniteTransition = rememberInfiniteTransition(label = "ambient")
    val ambientSecs by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f * 2, // Double PI for clean periodic loops
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "seconds"
    )

    // Remember background index per round. We'll automatically pick a new one when point progress returns to 0
    var backgroundIndex by remember { mutableStateOf((0..14).random()) }

    LaunchedEffect(targetPoints, currentPoints == 0) {
        if (currentPoints == 0) {
            backgroundIndex = (0..14).random()
        }
    }

    CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Ltr) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            val totalWidth = maxWidth
            val padding = 32.dp
            val trackLength = totalWidth - (padding * 2)
        
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val skyHeight = h * 0.48f
                val roadY = h - 50.dp.toPx()
                val roadHeight = 50.dp.toPx()
                val startX = padding.toPx()
                val endX = w - padding.toPx()
                val roadLengthPx = endX - startX

                val pTime = ambientSecs // ambient tick factor for custom trigonometric sways

                // RENDER EACH OF THE 15 AMAZING THEMED ENVIRONMENTS
                when (backgroundIndex) {
                    0 -> { // --- 0. CLASSIC GREEN HILLS (🏡 עמק ירוק) ---
                        // Sky
                        drawRect(
                            brush = Brush.verticalGradient(listOf(Color(0xFF80DEEA), Color(0xFFE0F7FA))),
                            topLeft = Offset(0f, 0f), size = Size(w, skyHeight)
                        )
                        // Sun
                        drawCircle(Color(0x22FFEA00), radius = 35.dp.toPx(), center = Offset(w - 60.dp.toPx(), 45.dp.toPx()))
                        drawCircle(Color(0xFFFFEE58), radius = 24.dp.toPx(), center = Offset(w - 60.dp.toPx(), 45.dp.toPx()))
                        
                        // Overlapping Hills
                        drawCircle(Color(0xFFC5E1A5), radius = w * 0.5f, center = Offset(w * 0.2f, skyHeight + w * 0.22f))
                        drawCircle(Color(0xFFA5D6A7), radius = w * 0.6f, center = Offset(w * 0.75f, skyHeight + w * 0.35f))
                        drawRect(Color(0xFF9CCC65), topLeft = Offset(0f, skyHeight), size = Size(w, h - skyHeight))

                        // Fluffy Clouds floating
                        fun drawCloud(cx: Float, cy: Float) {
                            val r = 10.dp.toPx()
                            drawCircle(Color(0xE0FFFFFF), radius = r, center = Offset(cx, cy))
                            drawCircle(Color(0xE0FFFFFF), radius = r * 1.5f, center = Offset(cx + r, cy - r * 0.4f))
                            drawCircle(Color(0xE0FFFFFF), radius = r, center = Offset(cx + r * 2.5f, cy))
                        }
                        val cloudShift = (pTime * 15.dp.toPx()) % w
                        drawCloud((w * 0.1f + cloudShift) % w, 35.dp.toPx())
                        drawCloud((w * 0.6f + cloudShift) % w, 25.dp.toPx())

                        // Cozy Houses & Trees
                        fun drawClassicTree(tx: Float, windSway: Float) {
                            drawRect(Color(0xFF8D6E63), topLeft = Offset(tx - 3.dp.toPx(), skyHeight - 16.dp.toPx()), size = Size(6.dp.toPx(), 16.dp.toPx()))
                            drawCircle(Color(0xFF4CAF50), radius = 14.dp.toPx(), center = Offset(tx + windSway, skyHeight - 20.dp.toPx()))
                        }
                        drawClassicTree(w * 0.08f, sin(pTime) * 3f)
                        drawClassicTree(w * 0.38f, sin(pTime + 1f) * 3f)
                        drawClassicTree(w * 0.88f, sin(pTime + 2f) * 3f)

                        // Classic farm house
                        val hx = w * 0.2f
                        drawRect(Color(0xFFEF9A9A), topLeft = Offset(hx, skyHeight - 25.dp.toPx()), size = Size(30.dp.toPx(), 25.dp.toPx()))
                        val roof = Path().apply {
                            moveTo(hx - 4.dp.toPx(), skyHeight - 25.dp.toPx())
                            lineTo(hx + 15.dp.toPx(), skyHeight - 37.dp.toPx())
                            lineTo(hx + 34.dp.toPx(), skyHeight - 25.dp.toPx())
                            close()
                        }
                        drawPath(roof, Color(0xFFC62828))
                        drawRect(Color(0xFF5D4037), topLeft = Offset(hx + 11.dp.toPx(), skyHeight - 12.dp.toPx()), size = Size(8.dp.toPx(), 12.dp.toPx()))

                        // Road details
                        drawLine(Color(0xFF8D6E63), start = Offset(startX - 24.dp.toPx(), roadY), end = Offset(endX + 24.dp.toPx(), roadY), strokeWidth = roadHeight + 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFF37474F), start = Offset(startX - 20.dp.toPx(), roadY), end = Offset(endX + 20.dp.toPx(), roadY), strokeWidth = roadHeight, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFFFFD54F), start = Offset(startX - 10.dp.toPx(), roadY), end = Offset(endX + 10.dp.toPx(), roadY), strokeWidth = 3.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(18.dp.toPx(), 18.dp.toPx()), 0f))
                    }
                    1 -> { // --- 1. GOLDEN DESERT (🌵 מדבר זהוב) ---
                        // Dusk sky
                        drawRect(
                            brush = Brush.verticalGradient(listOf(Color(0xFFE65100), Color(0xFFF57C00), Color(0xFFFFB74D))),
                            topLeft = Offset(0f, 0f), size = Size(w, skyHeight)
                        )
                        // Big blazing sun
                        drawCircle(Color(0x44FFFFFF), radius = 40.dp.toPx(), center = Offset(w * 0.15f, 40.dp.toPx()))
                        drawCircle(Color(0xFFFFF9C4), radius = 28.dp.toPx(), center = Offset(w * 0.15f, 40.dp.toPx()))

                        // Sand dunes
                        drawCircle(Color(0xFFFFCA28), radius = w * 0.4f, center = Offset(w * 0.3f, skyHeight + w * 0.2f))
                        drawCircle(Color(0xFFFFD54F), radius = w * 0.5f, center = Offset(w * 0.8f, skyHeight + w * 0.28f))
                        drawRect(Color(0xFFFFEE58), topLeft = Offset(0f, skyHeight), size = Size(w, h - skyHeight))

                        // Pyramids
                        fun drawPyramid(px: Float, scale: Float) {
                            val baseW = 60.dp.toPx() * scale
                            val pH = 45.dp.toPx() * scale
                            // left side shadow
                            val pathLeft = Path().apply {
                                moveTo(px, skyHeight)
                                lineTo(px + baseW / 2, skyHeight - pH)
                                lineTo(px + baseW / 2 + 5f, skyHeight)
                                close()
                            }
                            drawPath(pathLeft, Color(0xFFEF6C00))
                            // right side highlight
                            val pathRight = Path().apply {
                                moveTo(px + baseW / 2 + 5f, skyHeight)
                                lineTo(px + baseW / 2, skyHeight - pH)
                                lineTo(px + baseW, skyHeight)
                                close()
                            }
                            drawPath(pathRight, Color(0xFFFFB74D))
                        }
                        drawPyramid(w * 0.45f, 0.9f)
                        drawPyramid(w * 0.62f, 1.2f)

                        // Saguaro Cacti swaying
                        fun drawCactus(cx: Float) {
                            val sway = sin(pTime + cx) * 2f
                            // trunk
                            drawRect(Color(0xFF2E7D32), topLeft = Offset(cx - 3.dp.toPx(), skyHeight - 24.dp.toPx()), size = Size(6.dp.toPx(), 24.dp.toPx()))
                            // left arm
                            drawRect(Color(0xFF2E7D32), topLeft = Offset(cx - 10.dp.toPx() + sway, skyHeight - 18.dp.toPx()), size = Size(8.dp.toPx(), 4.dp.toPx()))
                            drawRect(Color(0xFF2E7D32), topLeft = Offset(cx - 10.dp.toPx() + sway, skyHeight - 26.dp.toPx()), size = Size(4.dp.toPx(), 10.dp.toPx()))
                            // right arm
                            drawRect(Color(0xFF2E7D32), topLeft = Offset(cx + 2.dp.toPx() + sway, skyHeight - 14.dp.toPx()), size = Size(8.dp.toPx(), 4.dp.toPx()))
                            drawRect(Color(0xFF2E7D32), topLeft = Offset(cx + 6.dp.toPx() + sway, skyHeight - 22.dp.toPx()), size = Size(4.dp.toPx(), 10.dp.toPx()))
                        }
                        drawCactus(w * 0.08f)
                        drawCactus(w * 0.3f)
                        drawCactus(w * 0.82f)

                        // Desert clay road
                        drawLine(Color(0xFFE65100), start = Offset(startX - 24.dp.toPx(), roadY), end = Offset(endX + 24.dp.toPx(), roadY), strokeWidth = roadHeight + 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFF4E342E), start = Offset(startX - 20.dp.toPx(), roadY), end = Offset(endX + 20.dp.toPx(), roadY), strokeWidth = roadHeight, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFFFFB74D), start = Offset(startX - 10.dp.toPx(), roadY), end = Offset(endX + 10.dp.toPx(), roadY), strokeWidth = 3.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(18.dp.toPx(), 18.dp.toPx()), 0f))
                    }
                    2 -> { // --- 2. WINTER SNOW (❄️ שלג חורפי) ---
                        // Snowy dawn sky
                        drawRect(
                            brush = Brush.verticalGradient(listOf(Color(0xFF283593), Color(0xFF7986CB), Color(0xFFC5CAE9))),
                            topLeft = Offset(0f, 0f), size = Size(w, skyHeight)
                        )
                        // Soft dim moon
                        drawCircle(Color(0x33FFFFFF), radius = 20.dp.toPx(), center = Offset(w - 50.dp.toPx(), 40.dp.toPx()))
                        drawCircle(Color(0xFFFAFAFA), radius = 14.dp.toPx(), center = Offset(w - 50.dp.toPx(), 40.dp.toPx()))

                        // Snow mounds
                        drawCircle(Color(0xFFE8EAF6), radius = w * 0.45f, center = Offset(w * 0.25f, skyHeight + w * 0.22f))
                        drawCircle(Color(0xFFF5F5F7), radius = w * 0.55f, center = Offset(w * 0.75f, skyHeight + w * 0.28f))
                        drawRect(Color(0xFFFFFFFF), topLeft = Offset(0f, skyHeight), size = Size(w, h - skyHeight))

                        // Falling snowflakes (live animation using tick timer)
                        for (i in 0..12) {
                            val sx = (w * (i * 0.08f) + sin(pTime + i) * 15f) % w
                            val sy = (skyHeight * (i * 0.07f) + (pTime * 25.dp.toPx())) % skyHeight
                            drawCircle(Color.White, radius = 3.dp.toPx(), center = Offset(sx, sy))
                        }

                        // Snowy fir trees
                        fun drawSpruceTree(tx: Float) {
                            val sw = sin(pTime + tx) * 2f
                            drawRect(Color(0xFF5D4037), topLeft = Offset(tx - 3.dp.toPx(), skyHeight - 12.dp.toPx()), size = Size(6.dp.toPx(), 12.dp.toPx()))
                            val treePath = Path().apply {
                                moveTo(tx - 18.dp.toPx() + sw, skyHeight - 10.dp.toPx())
                                lineTo(tx + sw, skyHeight - 28.dp.toPx())
                                lineTo(tx + 18.dp.toPx() + sw, skyHeight - 10.dp.toPx())
                                close()
                            }
                            drawPath(treePath, Color(0xFF1B5E20))
                            // Snow cap
                            val capPath = Path().apply {
                                moveTo(tx - 10.dp.toPx() + sw, skyHeight - 18.dp.toPx())
                                lineTo(tx + sw, skyHeight - 28.dp.toPx())
                                lineTo(tx + 10.dp.toPx() + sw, skyHeight - 18.dp.toPx())
                                close()
                            }
                            drawPath(capPath, Color.White)
                        }
                        drawSpruceTree(w * 0.15f)
                        drawSpruceTree(w * 0.48f)
                        drawSpruceTree(w * 0.85f)

                        // Slushy frozen winter road
                        drawLine(Color(0xFFB0BEC5), start = Offset(startX - 24.dp.toPx(), roadY), end = Offset(endX + 24.dp.toPx(), roadY), strokeWidth = roadHeight + 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFF212121), start = Offset(startX - 20.dp.toPx(), roadY), end = Offset(endX + 20.dp.toPx(), roadY), strokeWidth = roadHeight, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFF80D8FF), start = Offset(startX - 10.dp.toPx(), roadY), end = Offset(endX + 10.dp.toPx(), roadY), strokeWidth = 3.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(18.dp.toPx(), 18.dp.toPx()), 0f))
                    }
                    3 -> { // --- 3. RETRO CYBERPUNK SYNTHWAVE (👾 סייברפאנק) ---
                        // Dark violet magenta grid sky
                        drawRect(
                            brush = Brush.verticalGradient(listOf(Color(0xFF03001e), Color(0xFF7303c0), Color(0xFFec38bc))),
                            topLeft = Offset(0f, 0f), size = Size(w, skyHeight)
                        )
                        // Giant retro sliced sun (concentric segmented lines)
                        val sunC = Offset(w * 0.5f, skyHeight * 0.85f)
                        drawCircle(Color(0xFFFF007F), radius = 42.dp.toPx(), center = sunC)
                        // Horizontal black slicing masks
                        for (i in 0..4) {
                            val sy = sunC.y - 42.dp.toPx() + (i * 18.dp.toPx())
                            drawRect(Color(0xFF03001e).copy(alpha = 0.5f + (i * 0.1f)), topLeft = Offset(sunC.x - 50.dp.toPx(), sy), size = Size(100.dp.toPx(), 4.dp.toPx()))
                        }

                        // Grid mountains / neon silhouettes
                        val netPath = Path().apply {
                            moveTo(0f, skyHeight)
                            lineTo(w * 0.25f, skyHeight - 35.dp.toPx())
                            lineTo(w * 0.45f, skyHeight)
                            lineTo(w * 0.7f, skyHeight - 45.dp.toPx())
                            lineTo(w * 0.9f, skyHeight - 15.dp.toPx())
                            lineTo(w, skyHeight)
                        }
                        drawPath(netPath, Color(0xFF3F004D))

                        // Grid floor
                        drawRect(Color(0xFF120024), topLeft = Offset(0f, skyHeight), size = Size(w, h - skyHeight))
                        // Perspective guide lines from horizon
                        for (i in -5..5) {
                            drawLine(Color(0xFFFF00FF).copy(alpha = 0.4f), start = Offset(w*0.5f + i*20f, skyHeight), end = Offset(w*0.5f + i*160f, h), strokeWidth = 1.5.dp.toPx())
                        }
                        // Moving horizontal scan lines for speed illusion
                        val gridSpeed = (pTime * 22f) % 60f
                        for (j in 0..5) {
                            val ly = skyHeight + (j * 30f) + gridSpeed
                            if (ly < h) {
                                drawLine(Color(0xFF00E5FF).copy(alpha = 0.5f), start = Offset(0f, ly), end = Offset(w, ly), strokeWidth = 2f)
                            }
                        }

                        // Glowing neon roadbed
                        drawLine(Color(0xFFFF00FF), start = Offset(startX - 22.dp.toPx(), roadY), end = Offset(endX + 22.dp.toPx(), roadY), strokeWidth = roadHeight + 6.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFF0F0018), start = Offset(startX - 20.dp.toPx(), roadY), end = Offset(endX + 20.dp.toPx(), roadY), strokeWidth = roadHeight, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFF00FFFF), start = Offset(startX - 10.dp.toPx(), roadY), end = Offset(endX + 10.dp.toPx(), roadY), strokeWidth = 3.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(18.dp.toPx(), 18.dp.toPx()), 0f))
                    }
                    4 -> { // --- 4. DEEP SPACE (🚀 חלל עמוק) ---
                        // Dark cosmos starry sky
                        drawRect(
                            brush = Brush.verticalGradient(listOf(Color(0xFF00030A), Color(0xFF081226))),
                            topLeft = Offset(0f, 0f), size = Size(w, skyHeight)
                        )
                        // Star twinkles using sine loops
                        for (i in 0..15) {
                            val starX = (w * (i * 0.075f) * 1.3f) % w
                            val starY = (skyHeight * (i * 0.09f) * 1.5f) % skyHeight
                            val sizeFact = abs(sin(pTime + i)) * 3.dp.toPx()
                            drawCircle(Color.White.copy(alpha = 0.6f + abs(sin(pTime + i))*0.4f), radius = sizeFact + 1f, center = Offset(starX, starY))
                        }

                        // Big Saturn with ring
                        val sc = Offset(w * 0.25f, 35.dp.toPx())
                        drawCircle(Color(0xBB311B92), radius = 22.dp.toPx(), center = sc) // Ring shadow back
                        drawLine(Color(0xFF8E24AA), start = Offset(sc.x - 36.dp.toPx(), sc.y - 6.dp.toPx()), end = Offset(sc.x + 36.dp.toPx(), sc.y + 6.dp.toPx()), strokeWidth = 5.dp.toPx())
                        drawCircle(Color(0xFFFFB300), radius = 16.dp.toPx(), center = sc) // Main planet body

                        // Galaxy swirl
                        drawCircle(Color(0x1A00E5FF), radius = 45.dp.toPx(), center = Offset(w * 0.8f, 30.dp.toPx()))
                        drawCircle(Color(0x3300E5FF), radius = 25.dp.toPx(), center = Offset(w * 0.8f, 30.dp.toPx()))

                        // Crater-ridden moon surface under the road
                        drawRect(Color(0xFF37474F), topLeft = Offset(0f, skyHeight), size = Size(w, h - skyHeight))
                        fun drawCrater(cx: Float, cy: Float, r: Float) {
                            drawCircle(Color(0xFF263238), radius = r, center = Offset(cx, cy))
                            drawCircle(Color(0xFF455A64), radius = r * 0.8f, center = Offset(cx + 2f, cy + 2f))
                        }
                        drawCrater(w * 0.12f, skyHeight + 25.dp.toPx(), 18f)
                        drawCrater(w * 0.45f, skyHeight + 35.dp.toPx(), 22f)
                        drawCrater(w * 0.78f, skyHeight + 20.dp.toPx(), 14f)

                        // Outer space neon track
                        drawLine(Color(0xFF00E5FF), start = Offset(startX - 24.dp.toPx(), roadY), end = Offset(endX + 24.dp.toPx(), roadY), strokeWidth = roadHeight + 4.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFF1E1E1E), start = Offset(startX - 20.dp.toPx(), roadY), end = Offset(endX + 20.dp.toPx(), roadY), strokeWidth = roadHeight, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xAAFFFFFF), start = Offset(startX - 10.dp.toPx(), roadY), end = Offset(endX + 10.dp.toPx(), roadY), strokeWidth = 3.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(18.dp.toPx(), 18.dp.toPx()), 0f))
                    }
                    5 -> { // --- 5. CANDY LAND (🍭 ארץ הממתקים) ---
                        // Creamy cotton-candy pink sky
                        drawRect(
                            brush = Brush.verticalGradient(listOf(Color(0xFFF8BBD0), Color(0xFFE1BEE7))),
                            topLeft = Offset(0f, 0f), size = Size(w, skyHeight)
                        )
                        // Smiling candy lollipop sun
                        val sC = Offset(w * 0.75f, 36.dp.toPx())
                        drawCircle(Color(0xFFFFF176), radius = 25.dp.toPx(), center = sC)
                        // Swirl inside sun
                        for (i in 0..3) {
                            drawCircle(Color(0xFFF57F17).copy(alpha = 0.4f), radius = (6 + i*5).dp.toPx(), center = sC, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
                        }

                        // Jelly mountains
                        drawCircle(Color(0xFFF48FB1), radius = w * 0.4f, center = Offset(w * 0.2f, skyHeight + w * 0.2f))
                        drawCircle(Color(0xFFCE93D8), radius = w * 0.5f, center = Offset(w * 0.8f, skyHeight + w * 0.3f))
                        drawRect(Color(0xFFFFF59D), topLeft = Offset(0f, skyHeight), size = Size(w, h - skyHeight))

                        // Gumdrops / lollipop trees swaying
                        fun drawLollipop(tx: Float) {
                            val wind = sin(pTime + tx) * 4f
                            drawRect(Color(0xFF795548), topLeft = Offset(tx - 2.dp.toPx(), skyHeight - 20.dp.toPx()), size = Size(4.dp.toPx(), 20.dp.toPx()))
                            drawCircle(Color(0xFFE91E63), radius = 12.dp.toPx(), center = Offset(tx + wind, skyHeight - 25.dp.toPx()))
                            drawCircle(Color.White.copy(alpha = 0.5f), radius = 6.dp.toPx(), center = Offset(tx + wind - 3f, skyHeight - 28.dp.toPx()))
                        }
                        drawLollipop(w * 0.08f)
                        drawLollipop(w * 0.35f)
                        drawLollipop(w * 0.9f)

                        // Chocolate highway
                        drawLine(Color(0xFFFF4081), start = Offset(startX - 24.dp.toPx(), roadY), end = Offset(endX + 24.dp.toPx(), roadY), strokeWidth = roadHeight + 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFF3E2723), start = Offset(startX - 20.dp.toPx(), roadY), end = Offset(endX + 20.dp.toPx(), roadY), strokeWidth = roadHeight, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFFF48FB1), start = Offset(startX - 10.dp.toPx(), roadY), end = Offset(endX + 10.dp.toPx(), roadY), strokeWidth = 3.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(18.dp.toPx(), 18.dp.toPx()), 0f))
                    }
                    6 -> { // --- 6. UNDERSEA ADVENTURE (🐙 מצולות הים) ---
                        // Coral teal ocean background
                        drawRect(
                            brush = Brush.verticalGradient(listOf(Color(0xFF00363A), Color(0xFF006064), Color(0xFF00ACC1))),
                            topLeft = Offset(0f, 0f), size = Size(w, skyHeight)
                        )
                        // Moving currents / light rays
                        for (i in 0..4) {
                            val rayX = w * 0.15f + i * w * 0.2f + sin(pTime) * 10f
                            val rayPath = Path().apply {
                                moveTo(rayX, 0f)
                                lineTo(rayX + 30.dp.toPx(), 0f)
                                lineTo(rayX - 30.dp.toPx(), skyHeight)
                                lineTo(rayX - 60.dp.toPx(), skyHeight)
                                close()
                            }
                            drawPath(rayPath, Color.White.copy(alpha = 0.06f))
                        }

                        // Waving sea kelp/plants on the sea floor
                        fun drawSeawood(tx: Float) {
                            val kw = 10.dp.toPx()
                            val path = Path().apply {
                                moveTo(tx - kw/2, skyHeight)
                                cubicTo(
                                    tx + sin(pTime)*15f, skyHeight - 12.dp.toPx(),
                                    tx - sin(pTime)*15f, skyHeight - 24.dp.toPx(),
                                    tx + sin(pTime)*20f, skyHeight - 34.dp.toPx()
                                )
                                lineTo(tx + kw/2 + sin(pTime)*20f, skyHeight - 34.dp.toPx())
                                cubicTo(
                                    tx + kw/2 - sin(pTime)*15f, skyHeight - 24.dp.toPx(),
                                    tx + kw/2 + sin(pTime)*15f, skyHeight - 12.dp.toPx(),
                                    tx + kw/2, skyHeight
                                )
                                close()
                            }
                            drawPath(path, Color(0xFF00E676).copy(alpha = 0.75f))
                        }
                        drawSeawood(w * 0.1f)
                        drawSeawood(w * 0.2f)
                        drawSeawood(w * 0.45f)
                        drawSeawood(w * 0.85f)

                        // Floating oxygen bubbles using live tick
                        for (i in 0..8) {
                            val bx = (w * (i * 0.11f) + sin(pTime * 2 + i) * 10f) % w
                            val by = (skyHeight * 0.8f - (pTime * 18.dp.toPx() * (1 + i % 2))) % skyHeight
                            if (by > 0) {
                                drawCircle(Color.White.copy(alpha = 0.4f), radius = (3 + i % 3).dp.toPx(), center = Offset(bx, by), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx()))
                            }
                        }

                        // Sandy seabed floor
                        drawRect(Color(0xFFE0C15C), topLeft = Offset(0f, skyHeight), size = Size(w, h - skyHeight))

                        // Ancient sunken block path
                        drawLine(Color(0xFF004D40), start = Offset(startX - 24.dp.toPx(), roadY), end = Offset(endX + 24.dp.toPx(), roadY), strokeWidth = roadHeight + 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFF455A64), start = Offset(startX - 20.dp.toPx(), roadY), end = Offset(endX + 20.dp.toPx(), roadY), strokeWidth = roadHeight, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFF00E5FF), start = Offset(startX - 10.dp.toPx(), roadY), end = Offset(endX + 10.dp.toPx(), roadY), strokeWidth = 3.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(18.dp.toPx(), 18.dp.toPx()), 0f))
                    }
                    7 -> { // --- 7. DINO VOLCANO LAND (🌋 עולם הלבה) ---
                        // Ash red sky
                        drawRect(
                            brush = Brush.verticalGradient(listOf(Color(0xFF260D00), Color(0xFF4E1600), Color(0xFFBF360C))),
                            topLeft = Offset(0f, 0f), size = Size(w, skyHeight)
                        )

                        // Menacing volcanos with lava flows (Triangles)
                        fun drawVolcano(vx: Float) {
                            val path = Path().apply {
                                moveTo(vx - 50.dp.toPx(), skyHeight)
                                lineTo(vx, skyHeight - 38.dp.toPx())
                                lineTo(vx + 50.dp.toPx(), skyHeight)
                                close()
                            }
                            drawPath(path, Color(0xFF1E130F))
                            // Spouting lava drops
                            val bHeight = (abs(sin(pTime * 2)) * 15.dp.toPx())
                            drawCircle(Color(0xFFFF3D00), radius = 5.dp.toPx(), center = Offset(vx, skyHeight - 38.dp.toPx() - bHeight))
                            drawCircle(Color(0xFFFFD600), radius = 3.dp.toPx(), center = Offset(vx + 4f, skyHeight - 38.dp.toPx() - bHeight * 0.7f))
                        }
                        drawVolcano(w * 0.32f)
                        drawVolcano(w * 0.68f)

                        // Obsidian rock ground with orange cracks
                        drawRect(Color(0xFF1A1A1A), topLeft = Offset(0f, skyHeight), size = Size(w, h - skyHeight))
                        drawLine(Color(0xFFFF3D00).copy(alpha = 0.8f), start = Offset(0f, skyHeight + 20f), end = Offset(w, skyHeight + 40f), strokeWidth = 2.dp.toPx())
                        drawLine(Color(0xFFFF9100).copy(alpha = 0.6f), start = Offset(w*0.4f, skyHeight + 5f), end = Offset(w*0.8f, h), strokeWidth = 1.dp.toPx())

                        // Lava roadbed
                        drawLine(Color(0xFFFF3D00), start = Offset(startX - 24.dp.toPx(), roadY), end = Offset(endX + 24.dp.toPx(), roadY), strokeWidth = roadHeight + 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFF2E2E2E), start = Offset(startX - 20.dp.toPx(), roadY), end = Offset(endX + 20.dp.toPx(), roadY), strokeWidth = roadHeight, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFFFF3D00), start = Offset(startX - 10.dp.toPx(), roadY), end = Offset(endX + 10.dp.toPx(), roadY), strokeWidth = 3.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(18.dp.toPx(), 18.dp.toPx()), 0f))
                    }
                    8 -> { // --- 8. SUNSET BEACH (🌅 חוף השקיעה) ---
                        // Gorgeous beach sunset
                        drawRect(
                            brush = Brush.verticalGradient(listOf(Color(0xFF9C27B0), Color(0xFFE91E63), Color(0xFFFF9800))),
                            topLeft = Offset(0f, 0f), size = Size(w, skyHeight)
                        )
                        // Sun touching the sea
                        val scY = skyHeight - 4.dp.toPx()
                        drawCircle(Color(0xFFFFEA00), radius = 32.dp.toPx(), center = Offset(w * 0.5f, scY))

                        // Sea water layer with waving line
                        val seaPath = Path().apply {
                            moveTo(0f, skyHeight)
                            for (x in 0..w.toInt() step 20) {
                                lineTo(x.toFloat(), skyHeight - 4.dp.toPx() + sin(pTime + x*0.02f)*4f)
                            }
                            lineTo(w, skyHeight)
                            close()
                        }
                        drawPath(seaPath, Color(0xFF009688))

                        // Sand shore
                        drawRect(Color(0xFFFFF176), topLeft = Offset(0f, skyHeight), size = Size(w, h - skyHeight))

                        // Silhouette of a palm tree
                        fun drawPalmTree(tx: Float) {
                            val wind = sin(pTime) * 3f
                            // trunk
                            val trunk = Path().apply {
                                moveTo(tx - 3.dp.toPx(), skyHeight)
                                cubicTo(tx - 3.dp.toPx(), skyHeight - 12.dp.toPx(), tx - 10.dp.toPx(), skyHeight - 24.dp.toPx(), tx - 12.dp.toPx() + wind, skyHeight - 32.dp.toPx())
                                lineTo(tx - 6.dp.toPx() + wind, skyHeight - 32.dp.toPx())
                                cubicTo(tx - 4.dp.toPx(), skyHeight - 24.dp.toPx(), tx + 3.dp.toPx(), skyHeight - 12.dp.toPx(), tx + 3.dp.toPx(), skyHeight)
                                close()
                            }
                            drawPath(trunk, Color(0xFF795548))
                            // Palm leaves (using simple lines / drops)
                            val px = tx - 9.dp.toPx() + wind
                            val py = skyHeight - 32.dp.toPx()
                            drawCircle(Color(0xFF2E7D32), radius = 8.dp.toPx(), center = Offset(px, py))
                            drawCircle(Color(0xFF388E3C), radius = 7.dp.toPx(), center = Offset(px - 10f, py - 5f))
                            drawCircle(Color(0xFF388E3C), radius = 7.dp.toPx(), center = Offset(px + 10f, py - 4f))
                        }
                        drawPalmTree(w * 0.12f)
                        drawPalmTree(w * 0.88f)

                        // Seaside asphalt highway
                        drawLine(Color(0xFFE91E63).copy(alpha = 0.4f), start = Offset(startX - 24.dp.toPx(), roadY), end = Offset(endX + 24.dp.toPx(), roadY), strokeWidth = roadHeight + 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFF37474F), start = Offset(startX - 20.dp.toPx(), roadY), end = Offset(endX + 20.dp.toPx(), roadY), strokeWidth = roadHeight, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color.White, start = Offset(startX - 10.dp.toPx(), roadY), end = Offset(endX + 10.dp.toPx(), roadY), strokeWidth = 3.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(18.dp.toPx(), 18.dp.toPx()), 0f))
                    }
                    9 -> { // --- 9. JUNGLE SAFARI (🌿 ג'ונגל טרופי) ---
                        // Tropical green forest gradient
                        drawRect(
                            brush = Brush.verticalGradient(listOf(Color(0xFF004D40), Color(0xFF1B5E20), Color(0xFF4CAF50))),
                            topLeft = Offset(0f, 0f), size = Size(w, skyHeight)
                        )
                        // Deep dark leafy layers
                        drawCircle(Color(0xFF0A3010), radius = w * 0.45f, center = Offset(w * 0.15f, skyHeight + w * 0.2f))
                        drawCircle(Color(0xFF1B5E20), radius = w * 0.55f, center = Offset(w * 0.85f, skyHeight + w * 0.28f))
                        drawRect(Color(0xFF2E7D32), topLeft = Offset(0f, skyHeight), size = Size(w, h - skyHeight))

                        // Falling tropical leaves (drawn as green diagonal shapes)
                        for (i in 0..8) {
                            val lx = (w * (i * 0.12f) + sin(pTime + i)*12f) % w
                            val ly = (skyHeight * (i * 0.08f) + (pTime * 15.dp.toPx())) % skyHeight
                            drawCircle(Color(0xFF7CB342).copy(alpha = 0.8f), radius = 4.dp.toPx(), center = Offset(lx, ly))
                        }

                        // Giant jungle palm trees
                        fun drawJungleTree(tx: Float) {
                            val wind = sin(pTime + tx) * 3f
                            drawRect(Color(0xFF4E342E), topLeft = Offset(tx - 4.dp.toPx(), skyHeight - 20.dp.toPx()), size = Size(8.dp.toPx(), 20.dp.toPx()))
                            drawCircle(Color(0xFF1B5E20), radius = 18.dp.toPx(), center = Offset(tx + wind, skyHeight - 25.dp.toPx()))
                            drawCircle(Color(0xFF558B2F), radius = 13.dp.toPx(), center = Offset(tx + wind + 8f, skyHeight - 30.dp.toPx()))
                            drawCircle(Color(0xFF33691E), radius = 12.dp.toPx(), center = Offset(tx + wind - 8f, skyHeight - 20.dp.toPx()))
                        }
                        drawJungleTree(w * 0.3f)
                        drawJungleTree(w * 0.65f)

                        // Rainforest dirt highway
                        drawLine(Color(0xFF1B5E20), start = Offset(startX - 24.dp.toPx(), roadY), end = Offset(endX + 24.dp.toPx(), roadY), strokeWidth = roadHeight + 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFF5D4037), start = Offset(startX - 20.dp.toPx(), roadY), end = Offset(endX + 20.dp.toPx(), roadY), strokeWidth = roadHeight, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFF81C784), start = Offset(startX - 10.dp.toPx(), roadY), end = Offset(endX + 10.dp.toPx(), roadY), strokeWidth = 3.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(18.dp.toPx(), 18.dp.toPx()), 0f))
                    }
                    10 -> { // --- 10. MEDIEVAL KINGDOM (🏰 ממלכת האבירים) ---
                        // Noble magenta violet sky
                        drawRect(
                            brush = Brush.verticalGradient(listOf(Color(0xFF4A148C), Color(0xFF6A1B9A), Color(0xFFECEFF1))),
                            topLeft = Offset(0f, 0f), size = Size(w, skyHeight)
                        )
                        // Seal sun
                        drawCircle(Color(0xFFFFD54F), radius = 24.dp.toPx(), center = Offset(w - 70.dp.toPx(), 40.dp.toPx()))
                        
                        // Green pasture hills
                        drawCircle(Color(0xFF66BB6A), radius = w * 0.5f, center = Offset(w * 0.3f, skyHeight + w * 0.25f))
                        drawRect(Color(0xFF4CAF50), topLeft = Offset(0f, skyHeight), size = Size(w, h - skyHeight))

                        // Castle towers with red flag!
                        fun drawCastle(cx: Float) {
                            // main grey block
                            drawRect(Color(0xFF78909C), topLeft = Offset(cx, skyHeight - 35.dp.toPx()), size = Size(26.dp.toPx(), 35.dp.toPx()))
                            // crenellation battlements
                            drawRect(Color(0xFF455A64), topLeft = Offset(cx - 2.dp.toPx(), skyHeight - 40.dp.toPx()), size = Size(30.dp.toPx(), 6.dp.toPx()))
                            // window
                            drawRect(Color(0xFF0D47A1), topLeft = Offset(cx + 10.dp.toPx(), skyHeight - 25.dp.toPx()), size = Size(6.dp.toPx(), 10.dp.toPx()))
                            // medieval flagpole with flapping crimson flag
                            val flagOffset = sin(pTime * 3) * 3f
                            drawLine(Color.LightGray, start = Offset(cx + 13.dp.toPx(), skyHeight - 40.dp.toPx()), end = Offset(cx + 13.dp.toPx(), skyHeight - 55.dp.toPx()), strokeWidth = 2.dp.toPx())
                            val flagPath = Path().apply {
                                moveTo(cx + 13.dp.toPx(), skyHeight - 55.dp.toPx())
                                lineTo(cx + 25.dp.toPx() + flagOffset, skyHeight - 50.dp.toPx())
                                lineTo(cx + 13.dp.toPx(), skyHeight - 45.dp.toPx())
                                close()
                            }
                            drawPath(flagPath, Color(0xFFD32F2F))
                        }
                        drawCastle(w * 0.15f)
                        drawCastle(w * 0.72f)

                        // Royal paved highway
                        drawLine(Color(0xFFFFD54F), start = Offset(startX - 24.dp.toPx(), roadY), end = Offset(endX + 24.dp.toPx(), roadY), strokeWidth = roadHeight + 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFF455A64), start = Offset(startX - 20.dp.toPx(), roadY), end = Offset(endX + 20.dp.toPx(), roadY), strokeWidth = roadHeight, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFFFFE082), start = Offset(startX - 10.dp.toPx(), roadY), end = Offset(endX + 10.dp.toPx(), roadY), strokeWidth = 3.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(18.dp.toPx(), 18.dp.toPx()), 0f))
                    }
                    11 -> { // --- 11. SKY CLOUD CITY (☁️ עיר העננים) ---
                        // Bright blue ether sky
                        drawRect(
                            brush = Brush.verticalGradient(listOf(Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF90CAF9))),
                            topLeft = Offset(0f, 0f), size = Size(w, h)
                        )
                        // Glowing star sun
                        drawCircle(Color(0x33FFFFFF), radius = 30.dp.toPx(), center = Offset(w - 60.dp.toPx(), 45.dp.toPx()))
                        drawCircle(Color.White, radius = 18.dp.toPx(), center = Offset(w - 60.dp.toPx(), 45.dp.toPx()))

                        // Giant fluffy white clouds covering the horizon and lower floor!
                        fun drawFluffyCloud(x: Float, y: Float, scale: Float) {
                            val r = 24.dp.toPx() * scale
                            drawCircle(Color.White, radius = r, center = Offset(x, y))
                            drawCircle(Color.White, radius = r * 1.3f, center = Offset(x + r, y - r * 0.4f))
                            drawCircle(Color(0xFFE0F7FA), radius = r, center = Offset(x + r * 2f, y))
                        }
                        drawFluffyCloud(w * 0.05f, skyHeight + 15.dp.toPx(), 1.2f)
                        drawFluffyCloud(w * 0.42f, skyHeight + 25.dp.toPx(), 1.5f)
                        drawFluffyCloud(w * 0.76f, skyHeight + 10.dp.toPx(), 1.1f)

                        // Flying hot air balloon swaying using live tick
                        val hx = w * 0.25f + sin(pTime) * 12f
                        val hy = 32.dp.toPx() + cos(pTime) * 6f
                        drawCircle(Color(0xFFE91E63), radius = 10.dp.toPx(), center = Offset(hx, hy))
                        drawCircle(Color(0xFF00E5FF), radius = 7.dp.toPx(), center = Offset(hx, hy))
                        drawRect(Color(0xFF795548), topLeft = Offset(hx - 2.dp.toPx(), hy + 12.dp.toPx()), size = Size(4.dp.toPx(), 4.dp.toPx()))
                        drawLine(Color.LightGray, start = Offset(hx - 2.dp.toPx(), hy), end = Offset(hx - 2.dp.toPx(), hy + 12.dp.toPx()), strokeWidth = 1f)
                        drawLine(Color.LightGray, start = Offset(hx + 2.dp.toPx(), hy), end = Offset(hx + 2.dp.toPx(), hy + 12.dp.toPx()), strokeWidth = 1f)

                        // Golden floating sky roadbed
                        drawLine(Color(0x80FFFFFF), start = Offset(startX - 24.dp.toPx(), roadY), end = Offset(endX + 24.dp.toPx(), roadY), strokeWidth = roadHeight + 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFFFFD54F), start = Offset(startX - 20.dp.toPx(), roadY), end = Offset(endX + 20.dp.toPx(), roadY), strokeWidth = roadHeight, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color.White, start = Offset(startX - 10.dp.toPx(), roadY), end = Offset(endX + 10.dp.toPx(), roadY), strokeWidth = 3.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(18.dp.toPx(), 18.dp.toPx()), 0f))
                    }
                    12 -> { // --- 12. AUTUMN FOREST (🍁 יער שלכת) ---
                        // Soft copper golden twilight
                        drawRect(
                            brush = Brush.verticalGradient(listOf(Color(0xFFE65100), Color(0xFFFFB74D), Color(0xFFFFF3E0))),
                            topLeft = Offset(0f, 0f), size = Size(w, skyHeight)
                        )
                        // Soft sun disk
                        drawCircle(Color(0xFFFFF9C4), radius = 22.dp.toPx(), center = Offset(w * 0.85f, 35.dp.toPx()))

                        // Rust leaf grounds
                        drawCircle(Color(0xFFBF360C), radius = w * 0.45f, center = Offset(w * 0.2f, skyHeight + w * 0.2f))
                        drawCircle(Color(0xFFE64A19), radius = w * 0.55f, center = Offset(w * 0.8f, skyHeight + w * 0.3f))
                        drawRect(Color(0xFFD84315), topLeft = Offset(0f, skyHeight), size = Size(w, h - skyHeight))

                        // Drifting falling maple leaves using live animation tick
                        for (i in 0..8) {
                            val lx = (w * (i * 0.13f) + sin(pTime + i) * 15f) % w
                            val ly = (skyHeight * (i * 0.08f) + (pTime * 18.dp.toPx())) % skyHeight
                            drawCircle(Color(0xFFFF3D00).copy(alpha = 0.8f), radius = 3.dp.toPx(), center = Offset(lx, ly))
                        }

                        // Autumn woods trees
                        fun drawAutumnTree(tx: Float) {
                            val sway = sin(pTime + tx) * 3f
                            drawRect(Color(0xFF5D4037), topLeft = Offset(tx - 3.dp.toPx(), skyHeight - 16.dp.toPx()), size = Size(6.dp.toPx(), 16.dp.toPx()))
                            drawCircle(Color(0xFFF4511E), radius = 15.dp.toPx(), center = Offset(tx + sway, skyHeight - 22.dp.toPx()))
                            drawCircle(Color(0xFFFFB300), radius = 11.dp.toPx(), center = Offset(tx + sway - 6f, skyHeight - 27.dp.toPx()))
                            drawCircle(Color(0xFFD84315), radius = 10.dp.toPx(), center = Offset(tx + sway + 6f, skyHeight - 18.dp.toPx()))
                        }
                        drawAutumnTree(w * 0.12f)
                        drawAutumnTree(w * 0.48f)
                        drawAutumnTree(w * 0.88f)

                        // Cobalt dark wet highway
                        drawLine(Color(0xFFD84315), start = Offset(startX - 24.dp.toPx(), roadY), end = Offset(endX + 24.dp.toPx(), roadY), strokeWidth = roadHeight + 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFF2E3B4E), start = Offset(startX - 20.dp.toPx(), roadY), end = Offset(endX + 20.dp.toPx(), roadY), strokeWidth = roadHeight, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFFFFB300), start = Offset(startX - 10.dp.toPx(), roadY), end = Offset(endX + 10.dp.toPx(), roadY), strokeWidth = 3.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(18.dp.toPx(), 18.dp.toPx()), 0f))
                    }
                    13 -> { // --- 13. INDUSTRIAL STEAMPUNK (⚙️ עולם הקיטור) ---
                        // Rust-bronze sepia industrial background
                        drawRect(
                            brush = Brush.verticalGradient(listOf(Color(0xFF3E2723), Color(0xFF4E342E), Color(0xFF8D6E63))),
                            topLeft = Offset(0f, 0f), size = Size(w, skyHeight)
                        )
                        // Factory smoking pipe silhouettes
                        fun drawSmokestack(cx: Float) {
                            drawRect(Color(0xFF2D1F1E), topLeft = Offset(cx, skyHeight - 34.dp.toPx()), size = Size(14.dp.toPx(), 34.dp.toPx()))
                            // White steam bubbles emerging using live timers
                            for (i in 0..2) {
                                val sOffset = (pTime * 12.dp.toPx() + i*15f) % 40.dp.toPx()
                                val rScale = 2.dp.toPx() + (sOffset * 0.12f)
                                drawCircle(Color.White.copy(alpha = 0.35f - (sOffset/120f)), radius = rScale, center = Offset(cx + 7.dp.toPx() + sin(pTime + i)*4f, skyHeight - 34.dp.toPx() - sOffset))
                            }
                        }
                        drawSmokestack(w * 0.22f)
                        drawSmokestack(w * 0.76f)

                        // Brass cogs spinning (drawn as layered circles with notch dots)
                        fun drawCog(cx: Float, cy: Float, radius: Float, cogS: Float) {
                            val rSp = pTime * 40f
                            drawCircle(Color(0xFF8D6E63), radius = radius + 4f, center = Offset(cx, cy))
                            drawCircle(Color(0xFFCD7F32), radius = radius, center = Offset(cx, cy))
                            // Draw cog teeth notches using polar math
                            for (angle in 0..360 step 45) {
                                val ar = (angle + rSp) * Math.PI / 180.0
                                val nx = cx + (radius * 0.9f * cos(ar)).toFloat()
                                val ny = cy + (radius * 0.9f * sin(ar)).toFloat()
                                drawCircle(Color(0xFF3E2723), radius = cogS, center = Offset(nx, ny))
                            }
                            drawCircle(Color(0xFF3E2723), radius = radius * 0.4f, center = Offset(cx, cy))
                        }
                        drawCog(w * 0.12f, 25.dp.toPx(), 18.dp.toPx(), 3.dp.toPx())
                        drawCog(w * 0.52f, 32.dp.toPx(), 22.dp.toPx(), 4.dp.toPx())

                        // Iron-plated modular ground
                        drawRect(Color(0xFF2D1F1E), topLeft = Offset(0f, skyHeight), size = Size(w, h - skyHeight))
                        // Stud markers on iron plates
                        for (i in 1..10) {
                            drawCircle(Color.DarkGray, radius = 2f, center = Offset(w * (i * 0.1f), skyHeight + 10f))
                        }

                        // Industrial high-friction iron conveyor track
                        drawLine(Color(0xFFCD7F32), start = Offset(startX - 24.dp.toPx(), roadY), end = Offset(endX + 24.dp.toPx(), roadY), strokeWidth = roadHeight + 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFF263238), start = Offset(startX - 20.dp.toPx(), roadY), end = Offset(endX + 20.dp.toPx(), roadY), strokeWidth = roadHeight, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFFCD7F32), start = Offset(startX - 10.dp.toPx(), roadY), end = Offset(endX + 10.dp.toPx(), roadY), strokeWidth = 3.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(18.dp.toPx(), 18.dp.toPx()), 0f))
                    }
                    14 -> { // --- 14. ANCIENT EGYPT (🛕 מצרים העתיקה) ---
                        // Purple sunset twilight sand
                        drawRect(
                            brush = Brush.verticalGradient(listOf(Color(0xFF3F1B4C), Color(0xFFD81B60), Color(0xFFFFB74D))),
                            topLeft = Offset(0f, 0f), size = Size(w, skyHeight)
                        )
                        // Giant golden mystical solar disk
                        val raC = Offset(w * 0.5f, skyHeight * 0.65f)
                        drawCircle(Color(0xFFFFD54F), radius = 28.dp.toPx(), center = raC)
                        // long sunrays
                        for (i in 0..7) {
                            val rayP = (i * 45) * Math.PI / 180.0
                            val rx = raC.x + (42.dp.toPx() * cos(rayP)).toFloat()
                            val ry = raC.y + (42.dp.toPx() * sin(rayP)).toFloat()
                            drawLine(Color(0xFFFFB74D).copy(alpha = 0.5f), start = raC, end = Offset(rx, ry), strokeWidth = 2f)
                        }

                        // Desert dunes & flowing blue Nile stream
                        drawCircle(Color(0xFFFFB74D), radius = w * 0.45f, center = Offset(w * 0.2f, skyHeight + w * 0.2f))
                        drawCircle(Color(0xFFFFCC80), radius = w * 0.55f, center = Offset(w * 0.8f, skyHeight + w * 0.3f))
                        drawRect(Color(0xFFFFE082), topLeft = Offset(0f, skyHeight), size = Size(w, h - skyHeight))

                        // Blue flowing Nile strip
                        val nilePath = Path().apply {
                            moveTo(0f, skyHeight + 15.dp.toPx())
                            lineTo(w * 0.35f, skyHeight + 18.dp.toPx())
                            lineTo(w * 0.6f, skyHeight + 8.dp.toPx())
                            lineTo(w, skyHeight + 14.dp.toPx())
                            lineTo(w, skyHeight + 22.dp.toPx())
                            lineTo(w * 0.6f, skyHeight + 16.dp.toPx())
                            lineTo(w * 0.35f, skyHeight + 26.dp.toPx())
                            lineTo(0f, skyHeight + 23.dp.toPx())
                            close()
                        }
                        drawPath(nilePath, Color(0xFF00B0FF).copy(alpha = 0.8f))

                        // Egyptian Obelisks (tall pointed pillars sways slightly)
                        fun drawObelisk(ox: Float) {
                            val sw = sin(pTime + ox) * 1.5f
                            val peakY = skyHeight - 40.dp.toPx()
                            val baseW = 10.dp.toPx()
                            val topW = 6.dp.toPx()
                            val path = Path().apply {
                                moveTo(ox - baseW/2, skyHeight)
                                lineTo(ox - topW/2 + sw, peakY)
                                lineTo(ox + sw, peakY - 8.dp.toPx()) // pointed cap top
                                lineTo(ox + topW/2 + sw, peakY)
                                lineTo(ox + baseW/2, skyHeight)
                                close()
                            }
                            drawPath(path, Color(0xFFD7CCC8))
                            // Engraving line vertical representation
                            drawLine(Color(0xFF8D6E63), start = Offset(ox, skyHeight - 4.dp.toPx()), end = Offset(ox + sw, peakY + 2.dp.toPx()), strokeWidth = 1f)
                        }
                        drawObelisk(w * 0.15f)
                        drawObelisk(w * 0.82f)

                        // Limestone block pavement
                        drawLine(Color(0xFFFFD54F), start = Offset(startX - 24.dp.toPx(), roadY), end = Offset(endX + 24.dp.toPx(), roadY), strokeWidth = roadHeight + 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFFEEEEEE), start = Offset(startX - 20.dp.toPx(), roadY), end = Offset(endX + 20.dp.toPx(), roadY), strokeWidth = roadHeight, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        drawLine(Color(0xFF00E5FF), start = Offset(startX - 10.dp.toPx(), roadY), end = Offset(endX + 10.dp.toPx(), roadY), strokeWidth = 3.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(18.dp.toPx(), 18.dp.toPx()), 0f))
                    }
                }

                // 2. RACING CHESSBOARD CHECKERED FINISH LINE FLAGS (Always painted cleanly at endX)
                val finishLineX = endX
                val finishLineY = roadY - roadHeight / 2
                val squareSize = roadHeight / 5
                for (i in 0..4) {
                    for (j in 0..1) {
                        val color = if ((i + j) % 2 == 0) Color.White else Color.Black
                        drawRect(
                            color = color,
                            topLeft = Offset(finishLineX + j * squareSize, finishLineY + i * squareSize),
                            size = Size(squareSize, squareSize)
                        )
                    }
                }

                // 3. DRAW STEEL FLAGPOLES UNDER THE MILESTONES (So stars look pinned to physical posts!)
                val milestones = listOf(100, 250, 400)
                milestones.forEach { ms ->
                    val msProgress = ms.toFloat() / targetPoints.toFloat()
                    if (msProgress < 1f) {
                        val msX = startX + (roadLengthPx * msProgress)
                        drawLine(
                            color = Color(0xFF90A4AE), // metal steel support pole
                            start = Offset(msX, roadY - 24.dp.toPx()),
                            end = Offset(msX, roadY + 8.dp.toPx()),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }

                // 4. CHRONO PROGRESS SPEED GLOW OVERLAY (Bright green tracer flowing behind the car)
                val carProgressX = startX + (roadLengthPx * animatedProgress)
                if (animatedProgress > 0f) {
                    drawLine(
                        color = Color(0x6600E676), // glowing green boost shadow 
                        start = Offset(startX, roadY + roadHeight * 0.45f),
                        end = Offset(carProgressX, roadY + roadHeight * 0.45f),
                        strokeWidth = 4.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }
            }
        
            // Floating interactive Milestone Reward Stars
            val milestones = listOf(100, 250, 400)
            milestones.forEach { ms ->
                val msProgress = ms.toFloat() / targetPoints.toFloat()
                if (msProgress < 1f) {
                    val msOffset = padding + (trackLength * msProgress)
                    Box(
                        modifier = Modifier
                            .offset(x = msOffset - 12.dp, y = 190.dp - 36.dp)
                            .size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Milestone",
                            tint = if (progress >= msProgress) Color(0xFFFFB300) else Color.LightGray.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        
            // Colorful Vehicle sitting beautifully on the road center with slight wheel bounce!
            val currentXPx = padding + (trackLength * animatedProgress)
            val speedFactor = 1f + (wheelsLevel - 1) * 0.4f
            val bounceHeight = 2f + (wheelsLevel - 1) * 0.8f
            val bounceY = (abs(sin(ambientSecs * 6f * speedFactor)) * bounceHeight).dp // cute driving wheel bounce

            Box(
                modifier = Modifier
                    .offset(x = currentXPx - 55.dp, y = 190.dp - 58.dp - bounceY)
                    .size(110.dp, 66.dp),
                contentAlignment = Alignment.Center
            ) {
                // Rocket Booster Fire if Turbo upgraded!
                if (turboLevel > 1) {
                    val fireSize = (14 + turboLevel * 5).sp
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .offset(x = (-16).dp, y = 6.dp)
                    ) {
                        Text(
                            text = "🔥",
                            fontSize = fireSize
                        )
                    }
                }

                com.example.ui.components.CarVisualIcon(
                    carId = carId,
                    currentUpgradeColorArgb = if (carColor != Color.Unspecified && carColor != Color.Transparent) {
                        try {
                            carColor.toArgb()
                        } catch (e: Exception) {
                            0
                        }
                    } else 0,
                    modifier = Modifier.fillMaxSize(),
                    showAura = false
                )
            }

            // Israel Hebrew indicators & easter eggs container for beautiful modern branding
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(Color(0xAA000000), shape = RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = getBiomeName(backgroundIndex),
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(Color(0x77000000), shape = RoundedCornerShape(14.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🔄 רקע משתנה בסיום מסלול",
                    color = Color(0xFFE0E0E0),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
