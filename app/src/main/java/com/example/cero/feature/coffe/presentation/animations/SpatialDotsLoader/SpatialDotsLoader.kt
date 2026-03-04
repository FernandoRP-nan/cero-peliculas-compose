package com.example.cero.feature.coffe.presentation.animations.SpatialDotsLoader

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.math.sin

// ==========================================
// SECTION 3: COMPLEX VISUALS & MORPHING CANVAS
// ==========================================



/** Animated loader interface. */
@Composable
fun SpatialDotsLoader() {
    val transition = rememberInfiniteTransition(label = "dots_loader")

    val wave by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    val containerRotation by transition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "containerRotation"
    )

    val dotColors = listOf(
        Color(0xFFD4AF37),
        Color(0xFFFAF9F6),
        Color(0xFFA1887F)
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { rotationZ = containerRotation },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        dotColors.forEachIndexed { index, color ->
            val phaseOffset = index * (Math.PI / 2.5)
            val currentSin = sin(wave + phaseOffset).toFloat()

            val dotScale = 1f + (currentSin * 0.45f)
            val yTranslation = currentSin * -12f
            val dotAlpha = 0.6f + (currentSin * 0.4f)
            val glowElevation = 10f * ((currentSin + 1f) / 2f)

            Box(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .size(12.dp)
                    .graphicsLayer {
                        translationY = yTranslation
                        scaleX = dotScale
                        scaleY = dotScale
                        alpha = dotAlpha
                    }
                    .shadow(
                        elevation = glowElevation.dp,
                        shape = CircleShape,
                        spotColor = color,
                        ambientColor = color
                    )
                    .background(color, CircleShape)
            )
        }
    }
}
