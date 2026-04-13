package com.example.cero.data.repository

import com.example.cero.domain.model.CardAccount
import com.example.cero.domain.model.WalletSnapshot
import com.example.cero.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalWalletRepository @Inject constructor() : WalletRepository {

    override fun observeWalletSnapshot(): Flow<WalletSnapshot> = flowOf(
        WalletSnapshot(
            totalDebt = 18650.0,
            monthlyCommitment = 2430.0,
            availableToSpend = 1570.0,
            canSpendMore = true,
            cards = listOf(
                CardAccount(
                    id = "bbva-oro",
                    displayName = "BBVA Oro",
                    brand = "Visa",
                    lastDigits = "4821",
                    creditLimit = 25000.0,
                    usedLimit = 12400.0,
                    monthlyInstallmentPayment = 1180.0,
                    pendingInstallments = 8
                ),
                CardAccount(
                    id = "nu-morado",
                    displayName = "Nu",
                    brand = "Mastercard",
                    lastDigits = "1904",
                    creditLimit = 18000.0,
                    usedLimit = 6250.0,
                    monthlyInstallmentPayment = 750.0,
                    pendingInstallments = 5
                ),
                CardAccount(
                    id = "santander-likeu",
                    displayName = "Santander LikeU",
                    brand = "Visa",
                    lastDigits = "7702",
                    creditLimit = 12000.0,
                    usedLimit = 0.0,
                    monthlyInstallmentPayment = 500.0,
                    pendingInstallments = 2
                )
            )
        )
    )
}
