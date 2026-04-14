package com.example.cero.data.repository

import com.example.cero.domain.model.CardAccount
import com.example.cero.domain.model.CardExpense
import com.example.cero.domain.model.CardExpenseType
import com.example.cero.domain.model.PaymentAllocationMode
import com.example.cero.domain.model.firstDueDateFor
import java.time.LocalDate
import kotlin.math.floor
import kotlin.math.min

internal fun CardAccount.applyMovements(movements: List<CardExpense>): CardAccount {
    val totalCharges = movements.totalCharges()
    val totalPayments = movements.totalPayments()
    val nonMsiCharges = movements.nonMsiCharges()
    val generalPayments = movements.paymentsFor(PaymentAllocationMode.GENERAL)
    val msiPayments = movements.paymentsFor(PaymentAllocationMode.MSI_ONLY)
    val financingPaymentPool = msiPayments + (generalPayments - nonMsiCharges).coerceAtLeast(0.0)

    val financingProgress = movements
        .filter { it.entryType == CardExpenseType.CHARGE && it.isMsi }
        .sortedWith(compareBy<CardExpense> { it.firstDueDate(this) }.thenBy { it.createdAt })
        .calculateFinancingProgress(financingPaymentPool)

    val adjustedAvailable = (availableLimit - totalCharges + totalPayments)
        .coerceIn(0.0, creditLimit)

    return copy(
        availableLimit = adjustedAvailable,
        monthlyInstallmentPayment = financingProgress.sumOf { it.activeMonthlyAmount },
        pendingInstallments = financingProgress.sumOf { it.remainingInstallments },
        pendingMsiBalance = financingProgress.sumOf { it.remainingBalance },
        paidMsi = financingProgress.sumOf { it.paidInstallments }
    )
}

private fun List<CardExpense>.totalCharges(): Double {
    return filter { it.entryType == CardExpenseType.CHARGE }.sumOf(CardExpense::amount)
}

private fun List<CardExpense>.totalPayments(): Double {
    return filter { it.entryType == CardExpenseType.PAYMENT }.sumOf(CardExpense::amount)
}

private fun List<CardExpense>.nonMsiCharges(): Double {
    return filter { it.entryType == CardExpenseType.CHARGE && !it.isMsi }.sumOf(CardExpense::amount)
}

private fun List<CardExpense>.paymentsFor(mode: PaymentAllocationMode): Double {
    return filter {
        it.entryType == CardExpenseType.PAYMENT && it.paymentAllocationMode == mode
    }.sumOf(CardExpense::amount)
}

private fun List<CardExpense>.calculateFinancingProgress(
    totalPayments: Double
): List<FinancingProgress> {
    var paymentPool = totalPayments

    return map { expense ->
        val installments = expense.installmentCount ?: 0
        val monthlyAmount = expense.monthlyInstallmentAmount.takeIf { it > 0.0 } ?: 0.0
        val amountApplied = min(paymentPool, expense.amount).coerceAtLeast(0.0)
        paymentPool = (paymentPool - amountApplied).coerceAtLeast(0.0)

        val paidInstallments = if (installments > 0 && monthlyAmount > 0.0) {
            floor(amountApplied / monthlyAmount).toInt().coerceIn(0, installments)
        } else {
            0
        }
        val remainingInstallments = (installments - paidInstallments).coerceAtLeast(0)
        val remainingBalance = (expense.amount - amountApplied).coerceAtLeast(0.0)

        FinancingProgress(
            activeMonthlyAmount = if (remainingInstallments > 0) monthlyAmount else 0.0,
            paidInstallments = paidInstallments,
            remainingInstallments = remainingInstallments,
            remainingBalance = remainingBalance
        )
    }
}

private fun CardExpense.firstDueDate(card: CardAccount): LocalDate {
    firstDueDate?.let { return it }
    return card.copy(
        paymentDay = paymentDaySnapshot ?: card.paymentDay,
        closingDay = closingDaySnapshot ?: card.closingDay
    ).firstDueDateFor(createdAt.toLocalDate())
}

private data class FinancingProgress(
    val activeMonthlyAmount: Double,
    val paidInstallments: Int,
    val remainingInstallments: Int,
    val remainingBalance: Double
)
