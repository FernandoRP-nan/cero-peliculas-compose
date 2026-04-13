package com.example.cero.feature.wallet

import com.example.cero.domain.model.CardAccount
import com.example.cero.domain.model.WalletSnapshot
import java.text.NumberFormat
import java.util.Locale

private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

internal fun WalletSnapshot.toUiState(isWalletOpen: Boolean): WalletUiState {
    val spendingMessage = if (canSpendMore) {
        "Si puedes gastar mas, pero con margen cuidado"
    } else {
        "Por ahora mejor no cargues otra compra a MSI"
    }

    return WalletUiState(
        totalDebt = currencyFormatter.format(totalDebt),
        monthlyCommitment = "${currencyFormatter.format(monthlyCommitment)}/mes",
        availableToSpend = currencyFormatter.format(availableToSpend),
        spendingMessage = spendingMessage,
        canSpendMore = canSpendMore,
        isWalletOpen = isWalletOpen,
        cards = cards.mapIndexed { index, card ->
            card.toUiModel(index)
        }
    )
}

private fun CardAccount.toUiModel(index: Int): WalletCardUiModel {
    val palette = when (index % 3) {
        0 -> 0xFF355C7D to 0xFF6C5B7B
        1 -> 0xFF0F766E to 0xFF115E59
        else -> 0xFF9A3412 to 0xFFEA580C
    }

    return WalletCardUiModel(
        id = id,
        name = displayName,
        brand = brand,
        lastDigits = lastDigits,
        limitUsageText = "${currencyFormatter.format(usedLimit)} de ${currencyFormatter.format(creditLimit)}",
        monthlyPaymentText = currencyFormatter.format(monthlyInstallmentPayment),
        installmentsText = "$pendingInstallments MSI pendientes",
        accentStart = palette.first,
        accentEnd = palette.second
    )
}
