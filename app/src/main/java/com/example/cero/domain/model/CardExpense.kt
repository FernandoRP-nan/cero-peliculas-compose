package com.example.cero.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class CardExpense(
    val id: String,
    val cardId: String,
    val concept: String,
    val amount: Double,
    val createdAt: LocalDateTime,
    val entryType: CardExpenseType = CardExpenseType.CHARGE,
    val isMsi: Boolean = false,
    val installmentCount: Int? = null,
    val monthlyInstallmentAmount: Double = 0.0,
    val financingId: String? = null,
    val firstDueDate: LocalDate? = null,
    val paymentDaySnapshot: Int? = null,
    val closingDaySnapshot: Int? = null,
    val paymentAllocationMode: PaymentAllocationMode = PaymentAllocationMode.GENERAL
)

enum class CardExpenseType {
    CHARGE,
    PAYMENT
}

enum class PaymentAllocationMode {
    GENERAL,
    MSI_ONLY
}
