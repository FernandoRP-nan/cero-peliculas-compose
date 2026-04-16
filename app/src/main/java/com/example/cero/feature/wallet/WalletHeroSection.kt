package com.example.cero.feature.wallet

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cero.domain.model.UiPerformanceMode

@Composable
internal fun WalletHero(
    uiState: WalletUiState,
    performanceMode: UiPerformanceMode,
    reduceEffects: Boolean,
    onWalletPressed: () -> Unit,
    onPreviewCardPressed: (String) -> Unit,
    onHiddenCardsPressed: () -> Unit
) {
    val animationDuration = when (performanceMode) {
        UiPerformanceMode.LOW -> 420
        UiPerformanceMode.BALANCED -> 700
        UiPerformanceMode.HIGH -> 900
    }

    val lidRotation by animateFloatAsState(
        targetValue = if (uiState.isWalletOpen) {
            when {
                reduceEffects -> 72f
                performanceMode == UiPerformanceMode.LOW -> 84f
                else -> 108f
            }
        } else {
            0f
        },
        animationSpec = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing),
        label = "wallet-lid-rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (performanceMode == UiPerformanceMode.LOW) 320.dp else 360.dp),
        contentAlignment = Alignment.Center
    ) {
        FloatingCardsCluster(
            cards = uiState.cards,
            performanceMode = performanceMode,
            reduceEffects = reduceEffects,
            isWalletOpen = uiState.isWalletOpen,
            selectedCardId = uiState.selectedCardId,
            transitioningCardId = uiState.transitioningToExpenseCardId,
            onCardPressed = onPreviewCardPressed,
            onHiddenCardsPressed = onHiddenCardsPressed,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .align(Alignment.BottomCenter)
                .shadow(18.dp, RoundedCornerShape(34.dp))
                .clip(RoundedCornerShape(34.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFFF7F1E5), Color(0xFFE7D5B2))))
                .clickable(onClick = onWalletPressed)
                .padding(22.dp)
        ) {
            WalletStatusPill(
                text = if (uiState.isWalletOpen) "Toca para cerrar la cartera" else "Toca para abrir tu cartera",
                modifier = Modifier.align(Alignment.TopCenter)
            )
            WalletBase()
            WalletLid(rotationX = lidRotation, modifier = Modifier.align(Alignment.TopCenter))
        }
    }
}

@Composable
private fun WalletStatusPill(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(top = 10.dp)
            .clip(CircleShape)
            .background(Color(0x33FFF7ED))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = Color(0xFFFDF4E7), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun FloatingCardsCluster(
    cards: List<WalletCardUiModel>,
    performanceMode: UiPerformanceMode,
    reduceEffects: Boolean,
    isWalletOpen: Boolean,
    selectedCardId: String?,
    transitioningCardId: String?,
    onCardPressed: (String) -> Unit,
    onHiddenCardsPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val maxPreviewCards = when (performanceMode) {
        UiPerformanceMode.LOW -> 2
        UiPerformanceMode.BALANCED -> 3
        UiPerformanceMode.HIGH -> 4
    }
    val visibleCards = cards.take(if (reduceEffects) minOf(maxPreviewCards, 2) else maxPreviewCards)
    val hiddenCards = (cards.size - visibleCards.size).coerceAtLeast(0)
    val positions = previewOffsets(visibleCards.size)
    val baseDuration = when (performanceMode) {
        UiPerformanceMode.LOW -> 260
        UiPerformanceMode.BALANCED -> 420
        UiPerformanceMode.HIGH -> 520
    }

    Box(modifier = modifier.fillMaxWidth().height(170.dp)) {
        visibleCards.forEachIndexed { index, card ->
            val targetX = positions[index].first
            val targetY = positions[index].second
            val animatedX by animateDpAsState(
                targetValue = if (isWalletOpen) targetX else 0.dp,
                animationSpec = tween(durationMillis = baseDuration + (index * 70), easing = FastOutSlowInEasing),
                label = "floating-card-x-$index"
            )
            val animatedY by animateDpAsState(
                targetValue = if (isWalletOpen) targetY else 70.dp,
                animationSpec = tween(durationMillis = baseDuration + (index * 70), easing = FastOutSlowInEasing),
                label = "floating-card-y-$index"
            )
            val animatedRotation by animateFloatAsState(
                targetValue = if (isWalletOpen) {
                    if (performanceMode == UiPerformanceMode.LOW || reduceEffects) 0f else ((index - 1.5f) * 6f)
                } else {
                    0f
                },
                animationSpec = tween(durationMillis = baseDuration + (index * 70), easing = FastOutSlowInEasing),
                label = "floating-card-rotation-$index"
            )
            val animatedAlpha by animateFloatAsState(
                targetValue = if (isWalletOpen) 1f else 0f,
                animationSpec = tween(durationMillis = (baseDuration - 40) + (index * 50)),
                label = "floating-card-alpha-$index"
            )
            val emphasizedScale by animateFloatAsState(
                targetValue = if (transitioningCardId == card.id) {
                    when (performanceMode) {
                        UiPerformanceMode.LOW -> 1.02f
                        UiPerformanceMode.BALANCED -> 1.08f
                        UiPerformanceMode.HIGH -> 1.12f
                    }
                } else {
                    1f
                },
                animationSpec = tween(durationMillis = baseDuration),
                label = "floating-card-scale-$index"
            )

            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(x = animatedX, y = animatedY)
                    .graphicsLayer {
                        rotationZ = animatedRotation
                        alpha = animatedAlpha
                        scaleX = emphasizedScale
                        scaleY = emphasizedScale
                    }
                    .clickable(enabled = isWalletOpen) { onCardPressed(card.id) },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .width(154.dp)
                        .height(98.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.horizontalGradient(listOf(Color(card.accentStart), Color(card.accentEnd))))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(text = card.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "**** ${card.lastDigits.ifBlank { "0000" }}",
                            color = Color.White.copy(alpha = 0.92f),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = card.brand.ifBlank { "Local" },
                            color = Color.White.copy(alpha = 0.92f)
                        )
                    }
                }
            }

            if (selectedCardId == card.id && isWalletOpen && performanceMode != UiPerformanceMode.LOW && !reduceEffects) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(x = animatedX + 52.dp, y = animatedY - 14.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF8E7D2))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = "Activa", color = Color(0xFF7C4526), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        if (hiddenCards > 0) {
            val chipOffsetX by animateDpAsState(
                targetValue = if (isWalletOpen) {
                    if (reduceEffects) 102.dp else 118.dp
                } else {
                    0.dp
                },
                animationSpec = tween(durationMillis = if (reduceEffects) 180 else baseDuration + 200, easing = FastOutSlowInEasing),
                label = "hidden-card-chip-x"
            )
            val chipOffsetY by animateDpAsState(
                targetValue = if (isWalletOpen) {
                    if (reduceEffects) 8.dp else (-6).dp
                } else {
                    70.dp
                },
                animationSpec = tween(durationMillis = if (reduceEffects) 180 else baseDuration + 200, easing = FastOutSlowInEasing),
                label = "hidden-card-chip-y"
            )
            val chipAlpha by animateFloatAsState(
                targetValue = if (isWalletOpen) 1f else 0f,
                animationSpec = tween(durationMillis = if (reduceEffects) 140 else baseDuration + 120),
                label = "hidden-card-chip-alpha"
            )

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = chipOffsetX, y = chipOffsetY)
                    .graphicsLayer { alpha = chipAlpha }
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF8E7D2))
                    .clickable(enabled = isWalletOpen, onClick = onHiddenCardsPressed)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(text = "+$hiddenCards mas", color = Color(0xFF7C4526), fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun previewOffsets(count: Int): List<Pair<Dp, Dp>> = when (count) {
    1 -> listOf(0.dp to 12.dp)
    2 -> listOf((-76).dp to 16.dp, 76.dp to 16.dp)
    3 -> listOf((-96).dp to 28.dp, 0.dp to 0.dp, 96.dp to 28.dp)
    else -> listOf((-118).dp to 30.dp, (-24).dp to 0.dp, 38.dp to 36.dp, 112.dp to 8.dp)
}

@Composable
private fun BoxScope.WalletBase() {
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .height(138.dp)
            .shadow(12.dp, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFFA5673F), Color(0xFF774422))))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 18.dp)
                .width(108.dp)
                .height(10.dp)
                .clip(CircleShape)
                .background(Color(0xCCF6E7D8))
        )
    }
}

@Composable
private fun WalletLid(rotationX: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(126.dp)
            .graphicsLayer {
                this.rotationX = rotationX
                cameraDistance = 24f * density
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0f)
            }
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFFC88756), Color(0xFF9B5D35))))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5DFC4))
        )
    }
}
