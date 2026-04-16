package com.example.cero.feature.wallet

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cero.R
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
        onCardSelected = viewModel::onCardSelected,
        onEditCardPressed = viewModel::onEditCardPressed,
        onAddExpensePressed = { cardId -> viewModel.onAddExpensePressed(cardId, performanceMode) },
        onQuickEntryPressed = { mode -> viewModel.onQuickEntryPressed(mode, performanceMode) },
        onQuickCardSelected = { cardId -> viewModel.onQuickCardSelected(cardId, performanceMode) },
        onHiddenCardsPressed = viewModel::onHiddenCardsPressed,
        onHiddenCardsScrollHandled = viewModel::onHiddenCardsScrollHandled,
        onBackPressed = viewModel::onBackPressed,
        onAddCardPressed = viewModel::onAddCardPressed,
        onDismissAddCard = viewModel::onDismissAddCard,
        onShortNameChanged = viewModel::onShortNameChanged,
        onBankNameChanged = viewModel::onBankNameChanged,
        onBrandChanged = viewModel::onBrandChanged,
        onCreditLimitChanged = viewModel::onCreditLimitChanged,
        onAvailableLimitChanged = viewModel::onAvailableLimitChanged,
        onPaymentDayChanged = viewModel::onPaymentDayChanged,
        onHasClosingDayChanged = viewModel::onHasClosingDayChanged,
        onClosingDayChanged = viewModel::onClosingDayChanged,
        onSaveCard = viewModel::onSaveCard,
        onAddExpenseFabPressed = viewModel::onAddExpenseFabPressed,
        onDismissAddExpense = viewModel::onDismissAddExpense,
        onMovementFilterModeChanged = viewModel::onMovementFilterModeChanged,
        onWeekDaySelected = viewModel::onWeekDaySelected,
        onExpenseConceptChanged = viewModel::onExpenseConceptChanged,
        onExpenseAmountChanged = viewModel::onExpenseAmountChanged,
        onExpenseModeChanged = viewModel::onExpenseModeChanged,
        onPaymentAllocationModeChanged = viewModel::onPaymentAllocationModeChanged,
        onExpenseIsMsiChanged = viewModel::onExpenseIsMsiChanged,
        onExpenseInstallmentCountChanged = viewModel::onExpenseInstallmentCountChanged,
        onSaveExpense = viewModel::onSaveExpense
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
    onEditCardPressed: (String) -> Unit,
    onAddExpensePressed: (String) -> Unit,
    onQuickEntryPressed: (AddExpenseEntryMode) -> Unit,
    onQuickCardSelected: (String) -> Unit,
    onHiddenCardsPressed: () -> Unit,
    onHiddenCardsScrollHandled: () -> Unit,
    onBackPressed: () -> Unit,
    onAddCardPressed: () -> Unit,
    onDismissAddCard: () -> Unit,
    onShortNameChanged: (String) -> Unit,
    onBankNameChanged: (String) -> Unit,
    onBrandChanged: (CardBrandOption) -> Unit,
    onCreditLimitChanged: (String) -> Unit,
    onAvailableLimitChanged: (String) -> Unit,
    onPaymentDayChanged: (String) -> Unit,
    onHasClosingDayChanged: (Boolean) -> Unit,
    onClosingDayChanged: (String) -> Unit,
    onSaveCard: () -> Unit,
    onAddExpenseFabPressed: () -> Unit,
    onDismissAddExpense: () -> Unit,
    onMovementFilterModeChanged: (MovementFilterMode) -> Unit,
    onWeekDaySelected: (String) -> Unit,
    onExpenseConceptChanged: (String) -> Unit,
    onExpenseAmountChanged: (String) -> Unit,
    onExpenseModeChanged: (AddExpenseEntryMode) -> Unit,
    onPaymentAllocationModeChanged: (PaymentAllocationMode) -> Unit,
    onExpenseIsMsiChanged: (Boolean) -> Unit,
    onExpenseInstallmentCountChanged: (String) -> Unit,
    onSaveExpense: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val shouldReduceEffects by remember(performanceMode, listState) {
        derivedStateOf {
            performanceMode == UiPerformanceMode.LOW || listState.isScrollInProgress
        }
    }

    BackHandler(
        enabled = uiState.currentScreen != WalletScreenDestination.Wallet ||
            uiState.isAddExpenseVisible ||
            uiState.isAddCardVisible ||
            uiState.isCardSelectorVisible ||
            uiState.isWalletOpen
    ) {
        when {
            uiState.isAddExpenseVisible ||
                uiState.currentScreen != WalletScreenDestination.Wallet ||
                uiState.isAddCardVisible ||
                uiState.isCardSelectorVisible ||
                uiState.isWalletOpen -> onBackPressed()
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Crossfade(
            targetState = uiState.currentScreen,
            animationSpec = tween(
                durationMillis = when (performanceMode) {
                    UiPerformanceMode.LOW -> 140
                    UiPerformanceMode.BALANCED -> 220
                    UiPerformanceMode.HIGH -> 320
                }
            ),
            label = "wallet-screen-crossfade"
        ) { screen ->
            when (screen) {
                WalletScreenDestination.Wallet -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = listState,
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            item { WalletHeader(uiState = uiState) }

                            item {
                                WalletHero(
                                    uiState = uiState,
                                    performanceMode = performanceMode,
                                    reduceEffects = shouldReduceEffects,
                                    onWalletPressed = onWalletPressed,
                                    onPreviewCardPressed = onPreviewCardPressed,
                                    onHiddenCardsPressed = onHiddenCardsPressed
                                )
                            }

                            item { SpendingSummary(uiState = uiState) }

                            item {
                                WalletQuickActions(
                                    uiState = uiState,
                                    performanceMode = performanceMode,
                                    onQuickEntryPressed = onQuickEntryPressed
                                )
                            }

                            item {
                                Text(
                                    text = if (uiState.isWalletOpen) stringResource(R.string.wallet_cards_open) else stringResource(R.string.wallet_cards_closed),
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
                            onCardSelected = onCardSelected,
                            onQuickCardSelected = onQuickCardSelected,
                            onAddExpensePressed = onAddExpensePressed,
                            onEditCardPressed = onEditCardPressed,
                            onHiddenCardsScrollHandled = onHiddenCardsScrollHandled
                        )

                        AddCardOverlay(
                            uiState = uiState,
                            performanceMode = performanceMode,
                            reduceEffects = shouldReduceEffects,
                            onDismiss = onDismissAddCard,
                            onShortNameChanged = onShortNameChanged,
                            onBankNameChanged = onBankNameChanged,
                            onBrandChanged = onBrandChanged,
                            onCreditLimitChanged = onCreditLimitChanged,
                            onAvailableLimitChanged = onAvailableLimitChanged,
                            onPaymentDayChanged = onPaymentDayChanged,
                            onHasClosingDayChanged = onHasClosingDayChanged,
                            onClosingDayChanged = onClosingDayChanged,
                            onSaveCard = onSaveCard
                        )

                        AddCardFab(
                            performanceMode = performanceMode,
                            reduceEffects = shouldReduceEffects,
                            expanded = uiState.isAddCardVisible,
                            onClick = if (uiState.isAddCardVisible) onDismissAddCard else onAddCardPressed,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 20.dp, bottom = 24.dp)
                        )
                    }
                }

                WalletScreenDestination.AddExpense -> {
                    AddExpenseScreen(
                        uiState = uiState,
                        performanceMode = performanceMode,
                        onBackPressed = onBackPressed,
                        onAddExpensePressed = onAddExpenseFabPressed,
                        onDismissAddExpense = onDismissAddExpense,
                        onMovementFilterModeChanged = onMovementFilterModeChanged,
                        onWeekDaySelected = onWeekDaySelected,
                        onExpenseConceptChanged = onExpenseConceptChanged,
                        onExpenseAmountChanged = onExpenseAmountChanged,
                        onExpenseModeChanged = onExpenseModeChanged,
                        onPaymentAllocationModeChanged = onPaymentAllocationModeChanged,
                        onExpenseIsMsiChanged = onExpenseIsMsiChanged,
                        onExpenseInstallmentCountChanged = onExpenseInstallmentCountChanged,
                        onSaveExpense = onSaveExpense
                    )
                }
            }
        }
    }
}

@Composable
private fun WalletHeader(uiState: WalletUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.wallet_header_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.wallet_header_format, uiState.walletName, uiState.helperText),
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
                        bankName = "BBVA",
                        brand = "Visa",
                        lastDigits = "4821",
                        creditLimitAmount = 25000.0,
                        usedLimitAmount = 12400.0,
                        limitUsageText = "$12,400.00 de $25,000.00",
                        availableLimitText = "$12,600.00",
                        availableLimitAmount = 12600.0,
                        monthlyPaymentText = "$1,180.00",
                        monthlyPaymentAmount = 1180.0,
                        installmentsText = "8 meses",
                        pendingMsiBalanceText = "$8,420.00",
                        pendingInstallmentsAmount = 8420.0,
                        paidMsiText = "2 meses pagados",
                        paymentDayText = "Pago el dia 12",
                        closingDayText = "Corte el dia 7",
                        accentStart = 0xFF355C7D,
                        accentEnd = 0xFF6C5B7B
                    ),
                    WalletCardUiModel(
                        id = "2",
                        name = "Nu",
                        bankName = "Nu",
                        brand = "Mastercard",
                        lastDigits = "1904",
                        creditLimitAmount = 18000.0,
                        usedLimitAmount = 6250.0,
                        limitUsageText = "$6,250.00 de $18,000.00",
                        availableLimitText = "$11,750.00",
                        availableLimitAmount = 11750.0,
                        monthlyPaymentText = "$750.00",
                        monthlyPaymentAmount = 750.0,
                        installmentsText = "5 meses",
                        pendingMsiBalanceText = "$5,150.00",
                        pendingInstallmentsAmount = 5150.0,
                        paidMsiText = "2 meses pagados",
                        paymentDayText = "Pago el dia 18",
                        closingDayText = "Corte el dia 13",
                        accentStart = 0xFF0F766E,
                        accentEnd = 0xFF115E59
                    )
                )
            ),
            performanceMode = UiPerformanceMode.BALANCED,
            onWalletPressed = {},
            onPreviewCardPressed = {},
            onDismissCardSelector = {},
            onCardSelected = {},
            onEditCardPressed = {},
            onAddExpensePressed = {},
            onQuickEntryPressed = {},
            onQuickCardSelected = {},
            onHiddenCardsPressed = {},
            onHiddenCardsScrollHandled = {},
            onBackPressed = {},
            onAddCardPressed = {},
            onDismissAddCard = {},
            onShortNameChanged = {},
            onBankNameChanged = {},
            onBrandChanged = {},
            onCreditLimitChanged = {},
            onAvailableLimitChanged = {},
            onPaymentDayChanged = {},
            onHasClosingDayChanged = {},
            onClosingDayChanged = {},
            onSaveCard = {},
            onAddExpenseFabPressed = {},
            onDismissAddExpense = {},
            onMovementFilterModeChanged = {},
            onWeekDaySelected = {},
            onExpenseConceptChanged = {},
            onExpenseAmountChanged = {},
            onExpenseModeChanged = {},
            onPaymentAllocationModeChanged = {},
            onExpenseIsMsiChanged = {},
            onExpenseInstallmentCountChanged = {},
            onSaveExpense = {}
        )
    }
}
