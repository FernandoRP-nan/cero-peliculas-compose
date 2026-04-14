package com.example.cero.feature.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cero.domain.model.UiPerformanceMode

@Composable
fun SplashScreen(
    performanceMode: UiPerformanceMode,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "splash")
    val lidRotation by transition.animateFloat(
        initialValue = -4f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "splash-lid"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF6EFE4), Color(0xFFE9D7BE), Color(0xFFD1B08C))
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Cero",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF3A2316)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(244.dp)
                        .height(168.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .width(224.dp)
                            .height(106.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(Brush.verticalGradient(listOf(Color(0xFFA5673F), Color(0xFF774422))))
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .width(224.dp)
                            .height(96.dp)
                            .graphicsLayer {
                                rotationX = lidRotation
                                cameraDistance = 22f * density
                                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0f)
                            }
                            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 32.dp, bottomEnd = 32.dp))
                            .background(Brush.verticalGradient(listOf(Color(0xFFC88756), Color(0xFF9B5D35))))
                    )
                }

                Text(
                    text = "Preparando tu cartera",
                    color = Color(0xFF4B3221),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )

                Text(
                    text = modeLabel(performanceMode),
                    color = Color(0xFF6C4A34),
                    fontSize = 15.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { index ->
                    val pulse by transition.animateFloat(
                        initialValue = 0.55f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(700 + (index * 120), easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulse-$index"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(12.dp)
                            .graphicsLayer { alpha = pulse }
                            .clip(CircleShape)
                            .background(Color(0xFF8B5B3E))
                    )
                }
            }
        }
    }
}

private fun modeLabel(mode: UiPerformanceMode): String = when (mode) {
    UiPerformanceMode.LOW -> "Optimizando para un dispositivo ligero"
    UiPerformanceMode.BALANCED -> "Ajustando una experiencia balanceada"
    UiPerformanceMode.HIGH -> "Cargando la experiencia visual completa"
}
