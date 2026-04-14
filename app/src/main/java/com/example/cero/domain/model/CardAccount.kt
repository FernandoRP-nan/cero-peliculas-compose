package com.example.cero.domain.model

data class CardAccount(
    val id: String,
    val displayName: String,
    val bankName: String? = null,
    val brand: String? = null,
    val lastDigits: String = "",
    val creditLimit: Double,
    val availableLimit: Double,
    val paymentDay: Int,
    val closingDay: Int? = null,
    val monthlyInstallmentPayment: Double,
    val pendingInstallments: Int,
    val pendingMsiBalance: Double = 0.0,
    val paidMsi: Int
) {
    val usedLimit: Double
        get() = (creditLimit - availableLimit).coerceAtLeast(0.0)
}
