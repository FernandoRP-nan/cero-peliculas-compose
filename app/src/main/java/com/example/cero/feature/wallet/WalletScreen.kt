package com.example.cero.feature.wallet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cero.domain.model.UiPerformanceMode
import com.example.cero.ui.theme.CeroTheme

@Composable
fun WalletRoute(
    performanceMode: UiPerformanceMode,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    WalletScreen(
        uiState = uiState,
        performanceMode = performanceMode,
        onWalletPressed = viewModel::onWalletPressed,
        onPreviewCardPressed = viewModel::onPreviewCardPressed,
        onDismissCardSelector = viewModel::onDismissCardSelector,
        onCardSelected = viewModel::onCardSelected
    )
}

@Composable
fun WalletScreen(
    uiState: WalletUiState,
    performanceMode: UiPerformanceMode,
    onWalletPressed: () -> Unit,
    onPreviewCardPressed: (String) -> Unit,
    onDismissCardSelector: () -> Unit,
    onCardSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val shouldReduceEffects by remember(performanceMode, listState) {
        derivedStateOf {
            performanceMode == UiPerformanceMode.LOW || listState.isScrollInProgress
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    WalletHeader(uiState = uiState)
                }

                item {
                    WalletHero(
                        uiState = uiState,
                        performanceMode = performanceMode,
                        reduceEffects = shouldReduceEffects,
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
                        enter = if (shouldReduceEffects) {
                            fadeIn(animationSpec = tween(140))
                        } else {
                            fadeIn(animationSpec = tween(500)) +
                                slideInVertically(animationSpec = tween(500)) { it / 3 } +
                                expandVertically(animationSpec = tween(500))
                        },
                        exit = fadeOut(animationSpec = tween(if (shouldReduceEffects) 120 else 250))
                    ) {
                        WalletCardItem(card = card)
                    }
                }
            }

            CardSelectorOverlay(
                uiState = uiState,
                performanceMode = performanceMode,
                reduceEffects = shouldReduceEffects,
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
            performanceMode = UiPerformanceMode.BALANCED,
            onWalletPressed = {},
            onPreviewCardPressed = {},
            onDismissCardSelector = {},
            onCardSelected = {}
        )
    }
}
