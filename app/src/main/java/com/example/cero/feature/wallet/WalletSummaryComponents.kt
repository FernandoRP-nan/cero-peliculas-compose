package com.example.cero.feature.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cero.R
import com.example.cero.domain.model.UiPerformanceMode

@Composable
internal fun SpendingSummary(uiState: WalletUiState) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = if (uiState.canSpendMore) stringResource(R.string.wallet_summary_can_spend) else stringResource(R.string.wallet_summary_hold),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryChip(title = stringResource(R.string.wallet_summary_debt), value = uiState.totalDebt)
                SummaryChip(title = stringResource(R.string.wallet_summary_monthly), value = uiState.monthlyCommitment)
            }

            SummaryHighlight(
                title = stringResource(R.string.wallet_summary_available),
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
internal fun WalletQuickActions(
    uiState: WalletUiState,
    performanceMode: UiPerformanceMode,
    onQuickEntryPressed: (AddExpenseEntryMode) -> Unit
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(R.string.wallet_quick_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = when {
                    uiState.cards.isEmpty() -> stringResource(R.string.wallet_quick_empty)
                    uiState.cards.size == 1 -> stringResource(R.string.wallet_quick_single)
                    else -> stringResource(R.string.wallet_quick_multi)
                },
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionChip(
                    title = stringResource(R.string.wallet_quick_charge),
                    subtitle = stringResource(R.string.wallet_quick_charge_subtitle),
                    performanceMode = performanceMode,
                    onClick = { onQuickEntryPressed(AddExpenseEntryMode.CHARGE) }
                )
                QuickActionChip(
                    title = stringResource(R.string.wallet_quick_payment),
                    subtitle = stringResource(R.string.wallet_quick_payment_subtitle),
                    performanceMode = performanceMode,
                    onClick = { onQuickEntryPressed(AddExpenseEntryMode.PAYMENT) }
                )
            }
        }
    }
}

@Composable
internal fun WalletCardItem(card: WalletCardUiModel) {
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
                    val subtitle = listOfNotNull(
                        card.bankName.takeIf { it.isNotBlank() },
                        card.brand.takeIf { it.isNotBlank() },
                        card.lastDigits.takeIf { it.isNotBlank() }?.let { "**** $it" }
                    ).joinToString(" · ")
                    Text(
                        text = subtitle.ifBlank { stringResource(R.string.wallet_card_saved) },
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryChip(title = stringResource(R.string.wallet_card_paid_msi), value = card.paidMsiText)
                SummaryChip(title = stringResource(R.string.wallet_card_pending_msi), value = card.installmentsText)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryChip(title = stringResource(R.string.wallet_card_msi_balance), value = card.pendingMsiBalanceText)
                SummaryChip(title = stringResource(R.string.wallet_metric_available), value = card.availableLimitText)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryChip(title = stringResource(R.string.wallet_card_used), value = card.limitUsageText)
                SummaryChip(title = stringResource(R.string.wallet_metric_monthly_msi), value = card.monthlyPaymentText)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryChip(title = stringResource(R.string.wallet_card_payment_day), value = card.paymentDayText)
                SummaryChip(title = stringResource(R.string.wallet_card_closing_day), value = card.closingDayText ?: WalletLocalization.noClosingDay())
            }
        }
    }
}

@Composable
private fun RowScope.QuickActionChip(
    title: String,
    subtitle: String,
    performanceMode: UiPerformanceMode,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animationDuration = when (performanceMode) {
        UiPerformanceMode.LOW -> 90
        UiPerformanceMode.BALANCED -> 160
        UiPerformanceMode.HIGH -> 220
    }
    val pressedScale by animateFloatAsState(
        targetValue = if (isPressed) {
            when (performanceMode) {
                UiPerformanceMode.LOW -> 0.985f
                UiPerformanceMode.BALANCED -> 0.97f
                UiPerformanceMode.HIGH -> 0.955f
            }
        } else {
            1f
        },
        animationSpec = tween(animationDuration),
        label = "quick-action-scale"
    )
    val containerColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFFEEDCC6) else MaterialTheme.colorScheme.background,
        animationSpec = tween(animationDuration),
        label = "quick-action-color"
    )

    Card(
        modifier = Modifier
            .weight(1f)
            .graphicsLayer {
                scaleX = pressedScale
                scaleY = pressedScale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = title, fontWeight = FontWeight.Bold)
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f)
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
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
        Text(text = title, color = content.copy(alpha = 0.78f))
        Text(
            text = value,
            color = content,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
