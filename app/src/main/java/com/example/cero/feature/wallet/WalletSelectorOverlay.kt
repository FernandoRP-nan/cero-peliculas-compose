package com.example.cero.feature.wallet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cero.domain.model.UiPerformanceMode

@Composable
internal fun CardSelectorOverlay(
    uiState: WalletUiState,
    performanceMode: UiPerformanceMode,
    reduceEffects: Boolean,
    onDismiss: () -> Unit,
    onCardSelected: (String) -> Unit,
    onAddExpensePressed: (String) -> Unit,
    onEditCardPressed: (String) -> Unit,
    onHiddenCardsScrollHandled: () -> Unit
) {
    val listState = rememberLazyListState()
    val selectedIndex = uiState.cards.indexOfFirst { it.id == uiState.selectedCardId }
    val hiddenCardsStartIndex = hiddenCardsStartIndex(
        cardsCount = uiState.cards.size,
        performanceMode = performanceMode,
        reduceEffects = reduceEffects
    )

    LaunchedEffect(
        uiState.isCardSelectorVisible,
        uiState.selectedCardId,
        uiState.cards.size,
        uiState.pendingSelectorScrollToHidden
    ) {
        if (uiState.isCardSelectorVisible && uiState.pendingSelectorScrollToHidden && hiddenCardsStartIndex >= 0) {
            listState.animateScrollToItem(index = hiddenCardsStartIndex)
            onHiddenCardsScrollHandled()
        } else if (uiState.isCardSelectorVisible && selectedIndex >= 0) {
            listState.animateScrollToItem(index = selectedIndex)
        }
    }

    AnimatedVisibility(
        visible = uiState.isCardSelectorVisible,
        enter = if (performanceMode == UiPerformanceMode.LOW || reduceEffects) {
            fadeIn(animationSpec = tween(160))
        } else {
            fadeIn(animationSpec = tween(240)) + scaleIn(initialScale = 0.92f)
        },
        exit = if (performanceMode == UiPerformanceMode.LOW || reduceEffects) {
            fadeOut(animationSpec = tween(120))
        } else {
            fadeOut(animationSpec = tween(180)) + scaleOut(targetScale = 0.95f)
        }
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
                            SelectorDialogCard(
                                card = card,
                                performanceMode = performanceMode,
                                reduceEffects = reduceEffects,
                                emphasized = uiState.selectedCardId == card.id,
                                showActions = uiState.actionMenuCardId == card.id,
                                isTransitioning = uiState.transitioningToExpenseCardId == card.id,
                                onClick = { onCardSelected(card.id) },
                                onAddExpensePressed = { onAddExpensePressed(card.id) },
                                onEditPressed = { onEditCardPressed(card.id) }
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

private fun hiddenCardsStartIndex(
    cardsCount: Int,
    performanceMode: UiPerformanceMode,
    reduceEffects: Boolean
): Int {
    val previewCount = when (performanceMode) {
        UiPerformanceMode.LOW -> 2
        UiPerformanceMode.BALANCED -> 3
        UiPerformanceMode.HIGH -> 4
    }
    val visiblePreviewCards = minOf(cardsCount, if (reduceEffects) minOf(previewCount, 2) else previewCount)
    return if (cardsCount > visiblePreviewCards) visiblePreviewCards else -1
}

@Composable
private fun SelectorDialogCard(
    card: WalletCardUiModel,
    performanceMode: UiPerformanceMode,
    reduceEffects: Boolean,
    emphasized: Boolean,
    showActions: Boolean,
    isTransitioning: Boolean,
    onClick: () -> Unit,
    onAddExpensePressed: () -> Unit,
    onEditPressed: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                    .height(if (performanceMode == UiPerformanceMode.LOW) 170.dp else 186.dp)
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
                            text = card.brand.ifBlank { "Local" }.uppercase(),
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
                        text = "****  ****  ****  ${card.lastDigits.ifBlank { "0000" }}",
                        color = Color.White,
                        fontSize = if (performanceMode == UiPerformanceMode.LOW) 18.sp else 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
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
                                    text = card.paidMsiText,
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

                        if (showActions) {
                            Text(
                                text = "Elige lo que quieres hacer con esta tarjeta.",
                                color = Color.White.copy(alpha = 0.92f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                if (emphasized && performanceMode != UiPerformanceMode.LOW && !reduceEffects) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .padding(3.dp)
                            .clip(RoundedCornerShape(27.dp))
                            .background(if (isTransitioning) Color(0x20FFF3D4) else Color(0x12FFE7BF))
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showActions,
            enter = fadeIn(animationSpec = tween(180)) + expandVertically(animationSpec = tween(220)),
            exit = fadeOut(animationSpec = tween(120))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionChip(
                    title = "Agregar gastos",
                    icon = {
                        Text(
                            text = "+",
                            color = Color(0xFFFFF7ED),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    containerColor = Color(0xFF9B5D35),
                    contentColor = Color(0xFFFFF7ED),
                    onClick = onAddExpensePressed,
                    modifier = Modifier.weight(1f)
                )

                ActionChip(
                    title = "Editar tarjeta",
                    icon = {
                        Text(
                            text = "E",
                            color = Color(0xFF5B3A24),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    containerColor = Color(0xFFE8D8C7),
                    contentColor = Color(0xFF5B3A24),
                    onClick = onEditPressed,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ActionChip(
    title: String,
    icon: @Composable () -> Unit,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon()
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = title,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
