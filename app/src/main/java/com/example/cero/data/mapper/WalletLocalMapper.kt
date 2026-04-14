package com.example.cero.data.mapper

import com.example.cero.data.local.CardEntity
import com.example.cero.data.local.CardExpenseEntity
import com.example.cero.domain.model.CardAccount
import com.example.cero.domain.model.CardExpense
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
        isMsi = isMsi,
        installmentCount = installmentCount,
        monthlyInstallmentAmount = monthlyInstallmentAmount,
        financingId = financingId
    )
}

fun CardExpense.toEntity(): CardExpenseEntity {
    return CardExpenseEntity(
        id = id,
        cardId = cardId,
        concept = concept,
        amount = amount,
        createdAt = createdAt.toString(),
        isMsi = isMsi,
        installmentCount = installmentCount,
        monthlyInstallmentAmount = monthlyInstallmentAmount,
        financingId = financingId
    )
}
