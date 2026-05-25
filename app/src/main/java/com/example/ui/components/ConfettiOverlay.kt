package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlinx.coroutines.isActive
import kotlin.random.Random

class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var color: Color,
    var rotation: Float,
    var rotationSpeed: Float,
    var size: Float
)

@Composable
fun ConfettiOverlay(modifier: Modifier = Modifier, isRunning: Boolean) {
    if (!isRunning) return
    
    val particles = remember {
        List(150) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -1f,
                vx = Random.nextFloat() * 2f - 1f,
                vy = Random.nextFloat() * 3f + 2f,
                color = listOf(Color(0xFFE53935), Color(0xFF1E88E5), Color(0xFF43A047), Color(0xFFFFB300), Color(0xFF8E24AA), Color(0xFF00BCD4)).random(),
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 10f - 5f,
                size = Random.nextFloat() * 20f + 15f
            )
        }
    }

    var ticks by remember { mutableStateOf(0L) }
    
    LaunchedEffect(isRunning) {
        // Reset particles before starting
        particles.forEach {
             it.x = Random.nextFloat()
             it.y = Random.nextFloat() * -1.5f
             it.vy = Random.nextFloat() * 3f + 2f
        }
        
        while (isActive && isRunning) {
            ticks = withFrameNanos { it }
            particles.forEach { p ->
                p.x += p.vx * 0.003f
                p.y += p.vy * 0.005f
                p.rotation += p.rotationSpeed
                if (p.y > 1.2f) { // reset when off screen
                    p.y = Random.nextFloat() * -0.5f
                    p.x = Random.nextFloat()
                }
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        ticks // Read state to trigger redraw
        particles.forEach { p ->
            val px = p.x * size.width
            val py = p.y * size.height
            if (py > -100f && py < size.height + 100f && px > -100f && px < size.width + 100f) {
                 rotate(degrees = p.rotation, pivot = Offset(px + p.size/2, py + p.size/2)) {
                     drawRect(color = p.color, topLeft = Offset(px, py), size = Size(p.size, p.size))
                 }
            }
        }
    }
}
