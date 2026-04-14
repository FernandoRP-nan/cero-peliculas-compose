package com.example.cero.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_expenses")
data class CardExpenseEntity(
    @PrimaryKey val id: String,
    val cardId: String,
    val concept: String,
    val amount: Double,
    val createdAt: String,
    val entryType: String,
    val isMsi: Boolean,
    val installmentCount: Int?,
    val monthlyInstallmentAmount: Double,
    val financingId: String?,
    val firstDueDate: String?,
    val paymentDaySnapshot: Int?,
    val closingDaySnapshot: Int?,
    val paymentAllocationMode: String
)
