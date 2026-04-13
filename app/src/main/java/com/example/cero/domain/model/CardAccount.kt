package com.example.cero.domain.model

data class CardAccount(
    val id: String,
    val displayName: String,
    val brand: String,
    val lastDigits: String,
    val creditLimit: Double,
    val usedLimit: Double,
    val monthlyInstallmentPayment: Double,
    val pendingInstallments: Int
)
