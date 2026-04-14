package com.example.cero.feature.wallet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
        onWalletPressed = viewModel::onWalletPressed,
        onPreviewCardPressed = viewModel::onPreviewCardPressed,
        onDismissCardSelector = viewModel::onDismissCardSelector,
        onCardSelected = viewModel::onCardSelected
    )
}

@Composable
fun WalletScreen(
    uiState: WalletUiState,
    onWalletPressed: () -> Unit,
    onPreviewCardPressed: (String) -> Unit,
    onDismissCardSelector: () -> Unit,
    onCardSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
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
                        onWalletPressed = onWalletPressed,
                        onPreviewCardPressed = onPreviewCardPressed
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

            CardSelectorOverlay(
                uiState = uiState,
                onDismiss = onDismissCardSelector,
                onCardSelected = onCardSelected
            )
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
    onWalletPressed: () -> Unit,
    onPreviewCardPressed: (String) -> Unit
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
            selectedCardId = uiState.selectedCardId,
            onCardPressed = onPreviewCardPressed,
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
    selectedCardId: String?,
    onCardPressed: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val visibleCards = cards.take(4)
    val hiddenCards = (cards.size - visibleCards.size).coerceAtLeast(0)
    val positions = previewOffsets(visibleCards.size)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(170.dp)
    ) {
        visibleCards.forEachIndexed { index, card ->
            val targetX = positions[index].first
            val targetY = positions[index].second
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
                    }
                    .clickable(enabled = isWalletOpen) {
                        onCardPressed(card.id)
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

            if (selectedCardId == card.id && isWalletOpen) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(x = animatedX + 52.dp, y = animatedY - 14.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF8E7D2))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Activa",
                        color = Color(0xFF7C4526),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
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

private fun previewOffsets(count: Int): List<Pair<Dp, Dp>> = when (count) {
    1 -> listOf(0.dp to 12.dp)
    2 -> listOf((-76).dp to 16.dp, 76.dp to 16.dp)
    3 -> listOf((-96).dp to 28.dp, 0.dp to 0.dp, 96.dp to 28.dp)
    else -> listOf((-118).dp to 30.dp, (-24).dp to 0.dp, 38.dp to 36.dp, 112.dp to 8.dp)
}

@Composable
private fun CardSelectorOverlay(
    uiState: WalletUiState,
    onDismiss: () -> Unit,
    onCardSelected: (String) -> Unit
) {
    val listState = rememberLazyListState()
    val selectedIndex = uiState.cards.indexOfFirst { it.id == uiState.selectedCardId }

    LaunchedEffect(uiState.isCardSelectorVisible, uiState.selectedCardId, uiState.cards.size) {
        if (uiState.isCardSelectorVisible && selectedIndex >= 0) {
            listState.animateScrollToItem(index = selectedIndex)
        }
    }

    AnimatedVisibility(
        visible = uiState.isCardSelectorVisible,
        enter = fadeIn(animationSpec = tween(240)) + scaleIn(initialScale = 0.92f),
        exit = fadeOut(animationSpec = tween(180)) + scaleOut(targetScale = 0.95f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x880E0A08))
                .clickable(onClick = onDismiss)
                .padding(horizontal = 20.dp, vertical = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.72f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    ),
                shape = RoundedCornerShape(34.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F3EA))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(22.dp)
                ) {
                    Text(
                        text = "Elige una tarjeta",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C201A)
                    )
                    Text(
                        text = "Como un menu de items: toca la que quieras enfocar.",
                        color = Color(0xFF6A5548),
                        modifier = Modifier.padding(top = 8.dp, bottom = 18.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(uiState.cards, key = { it.id }) { card ->
                            val isSelected = uiState.selectedCardId == card.id
                            SelectorDialogCard(
                                card = card,
                                emphasized = isSelected,
                                onClick = { onCardSelected(card.id) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFE8D8C7))
                                .clickable(onClick = onDismiss)
                                .padding(horizontal = 18.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "Cerrar",
                                color = Color(0xFF5B3A24),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectorDialogCard(
    card: WalletCardUiModel,
    emphasized: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(186.dp)
                .clip(RoundedCornerShape(30.dp))
                .then(
                    if (emphasized) {
                        Modifier.border(
                            width = 3.dp,
                            color = Color(0xFFF6D39B),
                            shape = RoundedCornerShape(30.dp)
                        )
                    } else {
                        Modifier
                    }
                )
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(card.accentStart), Color(card.accentEnd))
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(18.dp)
                    .clip(CircleShape)
                    .background(if (emphasized) Color(0xFFF6D39B) else Color(0x33FFF8F0))
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Text(
                    text = if (emphasized) "Seleccionada" else "Disponible",
                    color = if (emphasized) Color(0xFF5B3A24) else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = card.brand.uppercase(),
                        color = Color.White.copy(alpha = 0.78f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = card.name,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "****  ****  ****  ${card.lastDigits}",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "MSI al mes",
                            color = Color.White.copy(alpha = 0.72f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = card.monthlyPaymentText,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = card.PaidMsiText,
                            color = Color.White.copy(alpha = 0.88f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = card.installmentsText,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (emphasized) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(3.dp)
                        .clip(RoundedCornerShape(27.dp))
                        .background(Color(0x12FFE7BF))
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
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryChip(title = "MSI pagados", value = card.PaidMsiText)
                SummaryChip(title = "MSI pendientes", value = card.installmentsText)
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
                isWalletOpen = true,
                isCardSelectorVisible = true,
                selectedCardId = "1",
                cards = listOf(
                    WalletCardUiModel(
                        id = "1",
                        name = "BBVA Oro",
                        brand = "Visa",
                        lastDigits = "4821",
                        limitUsageText = "$12,400.00 de $25,000.00",
                        monthlyPaymentText = "$1,180.00",
                        PaidMsiText = "2 MSI pagados",
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
                        PaidMsiText = "2 MSI pagados",

                        installmentsText = "5 MSI pendientes",
                        accentStart = 0xFF0F766E,
                        accentEnd = 0xFF115E59
                    )
                )
            ),
            onWalletPressed = {},
            onPreviewCardPressed = {},
            onDismissCardSelector = {},
            onCardSelected = {}
        )
    }
}
