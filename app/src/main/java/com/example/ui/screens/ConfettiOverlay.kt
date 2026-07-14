package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.withFrameMillis
import kotlin.random.Random

data class ConfettiParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val size: Float,
    val shape: ParticleShape,
    var rotation: Float,
    var rotationSpeed: Float,
    var alpha: Float = 1f
)

enum class ParticleShape {
    Circle, Rect, Triangle
}

val ConfettiColors = listOf(
    Color(0xFFFF5252), // Red
    Color(0xFF448AFF), // Blue
    Color(0xFF69F0AE), // Green
    Color(0xFFFFD740), // Yellow
    Color(0xFFE040FB), // Purple
    Color(0xFFFF6E40), // Orange
    Color(0xFF18FFFF)  // Cyan
)

fun createParticles(widthPx: Float, heightPx: Float): List<ConfettiParticle> {
    val list = mutableListOf<ConfettiParticle>()
    
    // Bottom-left blast shooting up and right
    repeat(35) {
        val angle = Random.nextFloat() * 50f + 15f // 15 to 65 degrees
        val speed = Random.nextFloat() * 800f + 800f // 800 to 1600 px/s
        val angleRad = Math.toRadians(angle.toDouble())
        val vx = (speed * Math.cos(angleRad)).toFloat()
        val vy = -(speed * Math.sin(angleRad)).toFloat()
        
        list.add(
            ConfettiParticle(
                x = 0f,
                y = heightPx,
                vx = vx,
                vy = vy,
                color = ConfettiColors.random(),
                size = Random.nextFloat() * 14f + 14f,
                shape = ParticleShape.values().random(),
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 400f - 200f
            )
        )
    }
    
    // Bottom-right blast shooting up and left
    repeat(35) {
        val angle = Random.nextFloat() * 50f + 115f // 115 to 165 degrees
        val speed = Random.nextFloat() * 800f + 800f
        val angleRad = Math.toRadians(angle.toDouble())
        val vx = (speed * Math.cos(angleRad)).toFloat()
        val vy = -(speed * Math.sin(angleRad)).toFloat()
        
        list.add(
            ConfettiParticle(
                x = widthPx,
                y = heightPx,
                vx = vx,
                vy = vy,
                color = ConfettiColors.random(),
                size = Random.nextFloat() * 14f + 14f,
                shape = ParticleShape.values().random(),
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 400f - 200f
            )
        )
    }
    
    return list
}

@Composable
fun ConfettiOverlay(
    trigger: Long?,
    onFinished: () -> Unit = {}
) {
    if (trigger == null) return

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        var particles by remember(trigger) {
            mutableStateOf(createParticles(widthPx, heightPx))
        }

        LaunchedEffect(trigger) {
            val startTime = withFrameMillis { it }
            var lastTime = startTime
            val duration = 3000L // 3 seconds duration
            while (true) {
                val now = withFrameMillis { it }
                val elapsed = now - startTime
                if (elapsed > duration) {
                    onFinished()
                    break
                }
                val dt = (now - lastTime) / 1000f
                lastTime = now

                particles = particles.map { p ->
                    // Apply gravity
                    val nextVy = p.vy + 380f * dt
                    // Apply air drag
                    val nextVx = p.vx * (1f - 0.25f * dt)
                    val nextX = p.x + nextVx * dt
                    val nextY = p.y + nextVy * dt
                    val nextAlpha = if (elapsed > 2000L) {
                        (1f - (elapsed - 2000f) / 1000f).coerceIn(0f, 1f)
                    } else 1f

                    p.copy(
                        x = nextX,
                        y = nextY,
                        vx = nextVx,
                        vy = nextVy,
                        rotation = p.rotation + p.rotationSpeed * dt,
                        alpha = nextAlpha
                    )
                }.filter { p -> p.y < heightPx + 100f && p.x >= -100f && p.x <= widthPx + 100f && p.alpha > 0f }

                if (particles.isEmpty()) {
                    onFinished()
                    break
                }
            }
        }

        Canvas(modifier = Modifier.fillMaxSize().pointerInput(Unit) {}) {
            particles.forEach { p ->
                when (p.shape) {
                    ParticleShape.Circle -> {
                        drawCircle(
                            color = p.color,
                            radius = p.size / 2,
                            center = Offset(p.x, p.y),
                            alpha = p.alpha
                        )
                    }
                    ParticleShape.Rect -> {
                        rotate(degrees = p.rotation, pivot = Offset(p.x, p.y)) {
                            drawRect(
                                color = p.color,
                                topLeft = Offset(p.x - p.size / 2, p.y - p.size / 3),
                                size = Size(p.size, p.size * 0.6f),
                                alpha = p.alpha
                            )
                        }
                    }
                    ParticleShape.Triangle -> {
                        rotate(degrees = p.rotation, pivot = Offset(p.x, p.y)) {
                            val path = Path().apply {
                                moveTo(p.x, p.y - p.size / 2)
                                lineTo(p.x - p.size / 2, p.y + p.size / 2)
                                lineTo(p.x + p.size / 2, p.y + p.size / 2)
                                close()
                            }
                            drawPath(
                                path = path,
                                color = p.color,
                                alpha = p.alpha
                            )
                        }
                    }
                }
            }
        }
    }
}
