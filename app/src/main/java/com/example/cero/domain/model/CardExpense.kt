package com.example.cero.domain.model

import java.time.LocalDateTime

data class CardExpense(
    val id: String,
    val cardId: String,
    val concept: String,
    val amount: Double,
    val createdAt: LocalDateTime,
    val isMsi: Boolean = false,
    val installmentCount: Int? = null,
    val monthlyInstallmentAmount: Double = 0.0,
    val financingId: String? = null
)
