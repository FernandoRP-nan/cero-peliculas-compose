package com.example.cero.domain.model

import java.time.LocalDate
import kotlin.math.min

fun CardAccount.firstDueDateFor(purchaseDate: LocalDate): LocalDate {
    val closingDay = closingDay
    if (closingDay == null) {
        return paymentDateForMonth(
            monthDate = if (purchaseDate.dayOfMonth > paymentDay) purchaseDate.plusMonths(1) else purchaseDate,
            paymentDay = paymentDay
        )
    }

    val closingMonth = if (purchaseDate.dayOfMonth > closingDay) {
        purchaseDate.plusMonths(1)
    } else {
        purchaseDate
    }

    val dueMonth = if (paymentDay > closingDay) {
        closingMonth
    } else {
        closingMonth.plusMonths(1)
    }

    return paymentDateForMonth(monthDate = dueMonth, paymentDay = paymentDay)
}

private fun paymentDateForMonth(
    monthDate: LocalDate,
    paymentDay: Int
): LocalDate {
    val dueDay = min(paymentDay, monthDate.lengthOfMonth())
    return monthDate.withDayOfMonth(dueDay)
}
