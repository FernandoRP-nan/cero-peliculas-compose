package com.example.cero.data.mapper

import com.example.cero.data.local.CardEntity
import com.example.cero.data.local.CardExpenseEntity
import com.example.cero.domain.model.CardAccount
import com.example.cero.domain.model.CardExpense
import com.example.cero.domain.model.CardExpenseType
import com.example.cero.domain.model.PaymentAllocationMode
import java.time.LocalDate
import java.time.LocalDateTime

fun CardEntity.toDomain(): CardAccount {
    return CardAccount(
        id = id,
        displayName = displayName,
        bankName = bankName,
        brand = brand,
        lastDigits = lastDigits,
        creditLimit = creditLimit,
        availableLimit = availableLimit,
        paymentDay = paymentDay,
        closingDay = closingDay,
        monthlyInstallmentPayment = monthlyInstallmentPayment,
        pendingInstallments = pendingInstallments,
        pendingMsiBalance = pendingMsiBalance,
        paidMsi = paidMsi
    )
}

fun CardAccount.toEntity(): CardEntity {
    return CardEntity(
        id = id,
        displayName = displayName,
        bankName = bankName,
        brand = brand,
        lastDigits = lastDigits,
        creditLimit = creditLimit,
        availableLimit = availableLimit,
        paymentDay = paymentDay,
        closingDay = closingDay,
        monthlyInstallmentPayment = monthlyInstallmentPayment,
        pendingInstallments = pendingInstallments,
        pendingMsiBalance = pendingMsiBalance,
        paidMsi = paidMsi
    )
}

fun CardExpenseEntity.toDomain(): CardExpense {
    return CardExpense(
        id = id,
        cardId = cardId,
        concept = concept,
        amount = amount,
        createdAt = LocalDateTime.parse(createdAt),
        entryType = CardExpenseType.valueOf(entryType),
        isMsi = isMsi,
        installmentCount = installmentCount,
        monthlyInstallmentAmount = monthlyInstallmentAmount,
        financingId = financingId,
        firstDueDate = firstDueDate?.let(LocalDate::parse),
        paymentDaySnapshot = paymentDaySnapshot,
        closingDaySnapshot = closingDaySnapshot,
        paymentAllocationMode = PaymentAllocationMode.valueOf(paymentAllocationMode)
    )
}

fun CardExpense.toEntity(): CardExpenseEntity {
    return CardExpenseEntity(
        id = id,
        cardId = cardId,
        concept = concept,
        amount = amount,
        createdAt = createdAt.toString(),
        entryType = entryType.name,
        isMsi = isMsi,
        installmentCount = installmentCount,
        monthlyInstallmentAmount = monthlyInstallmentAmount,
        financingId = financingId,
        firstDueDate = firstDueDate?.toString(),
        paymentDaySnapshot = paymentDaySnapshot,
        closingDaySnapshot = closingDaySnapshot,
        paymentAllocationMode = paymentAllocationMode.name
    )
}
