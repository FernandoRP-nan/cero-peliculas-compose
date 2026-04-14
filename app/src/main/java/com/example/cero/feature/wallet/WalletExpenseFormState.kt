package com.example.cero.feature.wallet

internal fun AddCardFormUiState.validated(): AddCardFormUiState {
    val credit = creditLimit.toDoubleOrNull()
    val available = availableLimit.toDoubleOrNull()
    val payment = paymentDay.toIntOrNull()
    val closing = closingDay.toIntOrNull()

    val error = when {
        shortName.isBlank() -> WalletLocalization.shortNameRequired()
        credit == null || credit <= 0.0 -> WalletLocalization.validCreditLimit()
        available == null || available < 0.0 -> WalletLocalization.validAvailable()
        available > credit -> WalletLocalization.availableNotGreaterThanCredit()
        payment == null || payment !in 1..31 -> WalletLocalization.validPaymentDay()
        hasClosingDay && (closing == null || closing !in 1..31) -> WalletLocalization.validClosingDay()
        else -> null
    }

    return copy(
        canSubmit = error == null,
        errorMessage = if (hasAttemptedSubmit) error else null
    )
}

internal fun AddExpenseFormUiState.validated(): AddExpenseFormUiState {
    val amountValue = amount.toDoubleOrNull()
    val installments = installmentCount.toIntOrNull()
    val error = when {
        amountValue == null || amountValue <= 0.0 -> WalletLocalization.validAmount()
        mode == AddExpenseEntryMode.CHARGE && isMsi && (installments == null || installments !in 2..24) ->
            WalletLocalization.validInstallmentsRange()
        else -> null
    }

    return copy(
        canSubmit = error == null,
        errorMessage = if (hasAttemptedSubmit) error else null
    )
}

internal fun PaymentAllocationMode.toDomain(): com.example.cero.domain.model.PaymentAllocationMode {
    return when (this) {
        PaymentAllocationMode.GENERAL -> com.example.cero.domain.model.PaymentAllocationMode.GENERAL
        PaymentAllocationMode.MSI_ONLY -> com.example.cero.domain.model.PaymentAllocationMode.MSI_ONLY
    }
}

internal fun AddExpenseFormUiState.monthlyInstallmentAmount(): Double {
    val amountValue = amount.toDoubleOrNull() ?: return 0.0
    val installments = installmentCount.toIntOrNull() ?: return 0.0
    if (!isMsi || installments <= 0) return 0.0
    return amountValue / installments
}

internal fun String.filterAllowedNumeric(): String {
    var hasDot = false
    return buildString {
        for (char in this@filterAllowedNumeric) {
            when {
                char.isDigit() -> append(char)
                char == '.' && !hasDot -> {
                    hasDot = true
                    append(char)
                }
            }
        }
    }
}
