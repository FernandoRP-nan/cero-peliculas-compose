package com.example.cero.feature.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
                        text = subtitle.ifBlank { "Tarjeta guardada localmente" },
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryChip(title = "MSI pagados", value = card.paidMsiText)
                SummaryChip(title = "Pendiente MSI", value = card.installmentsText)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryChip(title = "Disponible", value = card.availableLimitText)
                SummaryChip(title = "Usado", value = card.limitUsageText)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryChip(title = "Dia de pago", value = card.paymentDayText)
                SummaryChip(title = "MSI al mes", value = card.monthlyPaymentText)
            }
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
