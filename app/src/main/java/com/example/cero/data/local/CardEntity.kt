package com.example.cero.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val bankName: String?,
    val brand: String?,
    val lastDigits: String,
    val creditLimit: Double,
    val availableLimit: Double,
    val paymentDay: Int,
    val closingDay: Int?,
    val monthlyInstallmentPayment: Double,
    val pendingInstallments: Int,
    val paidMsi: Int
)
