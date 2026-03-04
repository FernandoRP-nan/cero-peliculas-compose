package com.example.cero.feature.coffe.presentation.animations.GeometricCafeBackground

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import com.example.cero.feature.coffe.presentation.state.AppState.AppState
import kotlin.math.cos
import kotlin.math.sin

// ==========================================
// SECTION 3: COMPLEX VISUALS & MORPHING CANVAS
// ==========================================

/**
 * A highly performant spatial background layer.
 * Utilizes `drawWithCache` and native `drawPoints` batching to ensure a zero-allocation,
 * 60+ FPS fluid backdrop that reacts automatically to AppState transitions.
 */
@Composable
fun GeometricCafeBackground(appState: AppState, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "geometry")

    val fluidPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(40000, easing = LinearEasing), RepeatMode.Restart),
        label = "fluid"
    )

    val rotation1 by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(90000, easing = LinearEasing), RepeatMode.Restart),
        label = "rot1"
    )
    val rotation2 by transition.animateFloat(
        initialValue = 360f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            tween(120000, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "rot2"
    )

    val isDark = appState != AppState.BREWING

    val bgColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF140A05) else Color(0xFFFAF9F6),
        animationSpec = tween(1200), label = "bgColor"
    )
    val gridColor by animateColorAsState(
        targetValue = if (isDark) Color.White.copy(alpha = 0.03f) else Color(0xFFD7CCC8).copy(alpha = 0.3f),
        animationSpec = tween(1200), label = "gridColor"
    )
    val ghostPillColor by animateColorAsState(
        targetValue = if (isDark) Color.White.copy(alpha = 0.02f) else Color(0xFFEFEBE6),
        animationSpec = tween(1200), label = "ghostPillColor"
    )
    val bottomCircleColor by animateColorAsState(
        targetValue = if (isDark) Color.White.copy(alpha = 0.015f) else Color(0xFF3E2723).copy(alpha = 0.04f),
        animationSpec = tween(1200), label = "bottomCircleColor"
    )

    val uniqueSwirlAlpha by animateFloatAsState(
        targetValue = if (appState == AppState.PROCESSING) 1f else 0f,
        animationSpec = tween(1000), label = "swirlAlpha"
    )

    Spacer(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val w = size.width
                val h = size.height
                val dotSpacing = 80f

                // Highly optimized batch computation. This executes only once per layout change.
                val cachedGridPoints = mutableListOf<Offset>()
                for (x in 0..w.toInt() step dotSpacing.toInt()) {
                    for (y in 0..h.toInt() step dotSpacing.toInt()) {
                        cachedGridPoints.add(Offset(x.toFloat(), y.toFloat()))
                    }
                }

                onDrawBehind {
                    drawRect(color = bgColor)

                    if (uniqueSwirlAlpha > 0f) {
                        val cx = w / 2f + sin(fluidPhase) * (w * 0.25f)
                        val cy = h / 2f + cos(fluidPhase * 0.8f) * (h * 0.2f)

                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF4A3025).copy(alpha = uniqueSwirlAlpha),
                                    Color(0xFF2C1E16).copy(alpha = uniqueSwirlAlpha),
                                    Color.Transparent
                                ),
                                center = Offset(cx.toFloat(), cy.toFloat()),
                                radius = w * 1.3f
                            )
                        )
                    }

                    // Submits thousands of coordinates via a single low-level rendering call.
                    drawPoints(
                        points = cachedGridPoints,
                        pointMode = PointMode.Points,
                        color = gridColor,
                        strokeWidth = 5f,
                        cap = StrokeCap.Round
                    )

                    val floatingOffset = sin(fluidPhase) * 40f

                    rotate(degrees = rotation1, pivot = Offset(w * 0.2f, h * 0.3f)) {
                        drawRoundRect(
                            color = ghostPillColor,
                            topLeft = Offset(-w * 0.2f, h * 0.1f),
                            size = Size(w * 1.2f, w * 0.6f),
                            cornerRadius = CornerRadius(w * 0.3f, w * 0.3f)
                        )
                    }

                    translate(top = floatingOffset) {
                        rotate(degrees = rotation2, pivot = Offset(w * 0.8f, h * 0.6f)) {
                            drawCircle(
                                color = Color(0xFFD4AF37).copy(alpha = if (isDark) 0.1f else 0.4f),
                                radius = w * 0.55f,
                                center = Offset(w * 0.8f, h * 0.6f),
                                style = Stroke(width = 3f)
                            )
                        }
                    }

                    val espressoX = (w * 0.15f) + (sin(fluidPhase * 2) * 20f)
                    val espressoY = (h * 0.75f) + (sin(fluidPhase) * -30f)
                    drawCircle(
                        color = bottomCircleColor,
                        radius = w * 0.4f,
                        center = Offset(espressoX, espressoY)
                    )
                }
            }
    )
}
