package com.example.cero.feature.wallet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cero.ui.theme.CeroTheme

@Composable
fun WalletRoute(
    viewModel: WalletViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    WalletScreen(
        uiState = uiState,
        onWalletPressed = viewModel::onWalletPressed
    )
}

@Composable
fun WalletScreen(
    uiState: WalletUiState,
    onWalletPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                WalletHeader(uiState = uiState)
            }

            item {
                WalletHero(
                    uiState = uiState,
                    onWalletPressed = onWalletPressed
                )
            }

            item {
                SpendingSummary(uiState = uiState)
            }

            item {
                Text(
                    text = if (uiState.isWalletOpen) "Todas tus tarjetas" else "Tus tarjetas",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(uiState.cards, key = { it.id }) { card ->
                AnimatedVisibility(
                    visible = uiState.isWalletOpen,
                    enter = fadeIn(animationSpec = tween(500)) +
                        slideInVertically(animationSpec = tween(500)) { it / 3 } +
                        expandVertically(animationSpec = tween(500)),
                    exit = fadeOut(animationSpec = tween(250))
                ) {
                    WalletCardItem(card = card)
                }
            }
        }
    }
}

@Composable
private fun WalletHeader(uiState: WalletUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Decide rapido antes de comprar",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${uiState.walletName}. ${uiState.helperText}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f)
        )
    }
}

@Composable
private fun WalletHero(
    uiState: WalletUiState,
    onWalletPressed: () -> Unit
) {
    val lidRotation by animateFloatAsState(
        targetValue = if (uiState.isWalletOpen) -108f else 0f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "wallet-lid-rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp),
        contentAlignment = Alignment.Center
    ) {
        FloatingCardsCluster(
            cards = uiState.cards,
            isWalletOpen = uiState.isWalletOpen,
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
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFFF7F1E5),
                            Color(0xFFE7D5B2)
                        )
                    )
                )
                .clickable(onClick = onWalletPressed)
                .padding(22.dp)
        ) {
            WalletStatusPill(
                text = if (uiState.isWalletOpen) "Toca para cerrar la cartera" else "Toca para abrir tu cartera",
                modifier = Modifier.align(Alignment.TopCenter)
            )
            WalletBase()
            WalletLid(
                rotationX = lidRotation,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun WalletStatusPill(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(top = 10.dp)
            .clip(CircleShape)
            .background(Color(0x33FFF7ED))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFFFDF4E7),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun FloatingCardsCluster(
    cards: List<WalletCardUiModel>,
    isWalletOpen: Boolean,
    modifier: Modifier = Modifier
) {
    val visibleCards = cards.take(4)
    val hiddenCards = (cards.size - visibleCards.size).coerceAtLeast(0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(170.dp)
    ) {
        visibleCards.forEachIndexed { index, card ->
            val targetX = when (index) {
                0 -> (-118).dp
                1 -> (-24).dp
                2 -> 38.dp
                else -> 112.dp
            }
            val targetY = when (index) {
                0 -> 30.dp
                1 -> 0.dp
                2 -> 36.dp
                else -> 8.dp
            }
            val animatedX by animateDpAsState(
                targetValue = if (isWalletOpen) targetX else 0.dp,
                animationSpec = tween(durationMillis = 520 + (index * 90), easing = FastOutSlowInEasing),
                label = "floating-card-x-$index"
            )
            val animatedY by animateDpAsState(
                targetValue = if (isWalletOpen) targetY else 70.dp,
                animationSpec = tween(durationMillis = 520 + (index * 90), easing = FastOutSlowInEasing),
                label = "floating-card-y-$index"
            )
            val animatedRotation by animateFloatAsState(
                targetValue = if (isWalletOpen) ((index - 1.5f) * 6f) else 0f,
                animationSpec = tween(durationMillis = 520 + (index * 90), easing = FastOutSlowInEasing),
                label = "floating-card-rotation-$index"
            )
            val animatedAlpha by animateFloatAsState(
                targetValue = if (isWalletOpen) 1f else 0f,
                animationSpec = tween(durationMillis = 380 + (index * 70)),
                label = "floating-card-alpha-$index"
            )

            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(x = animatedX, y = animatedY)
                    .graphicsLayer {
                        rotationZ = animatedRotation
                        alpha = animatedAlpha
                    },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .width(154.dp)
                        .height(98.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(card.accentStart), Color(card.accentEnd))
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = card.name,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "**** ${card.lastDigits}",
                            color = Color.White.copy(alpha = 0.92f)
                        )
                        Text(
                            text = card.brand,
                            color = Color.White.copy(alpha = 0.92f)
                        )
                    }
                }
            }
        }

        if (hiddenCards > 0) {
            val chipOffsetX by animateDpAsState(
                targetValue = if (isWalletOpen) 118.dp else 0.dp,
                animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
                label = "hidden-card-chip-x"
            )
            val chipOffsetY by animateDpAsState(
                targetValue = if (isWalletOpen) (-6).dp else 70.dp,
                animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
                label = "hidden-card-chip-y"
            )
            val chipAlpha by animateFloatAsState(
                targetValue = if (isWalletOpen) 1f else 0f,
                animationSpec = tween(durationMillis = 700),
                label = "hidden-card-chip-alpha"
            )

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = chipOffsetX, y = chipOffsetY)
                    .graphicsLayer { alpha = chipAlpha }
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF8E7D2))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "+$hiddenCards mas",
                    color = Color(0xFF7C4526),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
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
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFA5673F), Color(0xFF774422))
                )
            )
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
private fun WalletLid(
    rotationX: Float,
    modifier: Modifier = Modifier
) {
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
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFC88756), Color(0xFF9B5D35))
                )
            )
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

@Composable
private fun SpendingSummary(uiState: WalletUiState) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = if (uiState.canSpendMore) "Todavia tienes aire" else "Toca apretarte un poco",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryChip(title = "Debes", value = uiState.totalDebt)
                SummaryChip(title = "Pagas", value = uiState.monthlyCommitment)
            }

            SummaryHighlight(
                title = "Disponible para gastar sin desordenarte",
                value = uiState.availableToSpend,
                positive = uiState.canSpendMore
            )

            Text(
                text = uiState.spendingMessage,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun RowScope.SummaryChip(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f)
            )
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SummaryHighlight(
    title: String,
    value: String,
    positive: Boolean
) {
    val background = if (positive) Color(0xFFE1F3EA) else Color(0xFFFBE4E1)
    val content = if (positive) Color(0xFF14532D) else Color(0xFF991B1B)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(background)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            color = content.copy(alpha = 0.78f)
        )
        Text(
            text = value,
            color = content,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun WalletCardItem(card: WalletCardUiModel) {
    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = card.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${card.brand} **** ${card.lastDigits}",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = card.installmentsText,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryChip(title = "Usado", value = card.limitUsageText)
                SummaryChip(title = "MSI al mes", value = card.monthlyPaymentText)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5EFE6)
@Composable
private fun WalletScreenPreviewClosed() {
    CeroTheme {
        WalletScreen(
            uiState = WalletUiState(
                totalDebt = "$18,650.00",
                monthlyCommitment = "$2,430.00/mes",
                availableToSpend = "$1,570.00",
                spendingMessage = "Si puedes gastar mas, pero con margen cuidado",
                canSpendMore = true,
                cards = listOf(
                    WalletCardUiModel(
                        id = "1",
                        name = "BBVA Oro",
                        brand = "Visa",
                        lastDigits = "4821",
                        limitUsageText = "$12,400.00 de $25,000.00",
                        monthlyPaymentText = "$1,180.00",
                        installmentsText = "8 MSI pendientes",
                        accentStart = 0xFF355C7D,
                        accentEnd = 0xFF6C5B7B
                    ),
                    WalletCardUiModel(
                        id = "2",
                        name = "Nu",
                        brand = "Mastercard",
                        lastDigits = "1904",
                        limitUsageText = "$6,250.00 de $18,000.00",
                        monthlyPaymentText = "$750.00",
                        installmentsText = "5 MSI pendientes",
                        accentStart = 0xFF0F766E,
                        accentEnd = 0xFF115E59
                    )
                )
            ),
            onWalletPressed = {}
        )
    }
}
