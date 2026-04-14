package com.example.cero.feature.wallet

import java.util.Locale

internal object WalletLocalization {
    private val isEnglish: Boolean
        get() = Locale.getDefault().language.startsWith("en")

    fun walletName() = if (isEnglish) "Your wallet" else "Tu cartera"
    fun walletHelper() = if (isEnglish) "Tap to open and review your cards" else "Toca para abrir y revisar tus tarjetas"
    fun monthlySuffix() = if (isEnglish) "/mo" else "/mes"
    fun spendingMessage(canSpendMore: Boolean) = when {
        isEnglish && canSpendMore -> "You can still spend more, but keep a safe margin"
        isEnglish -> "Better avoid another installment purchase for now"
        canSpendMore -> "Si puedes gastar mas, pero con margen cuidado"
        else -> "Por ahora mejor no cargues otra compra a MSI"
    }
    fun paymentDay(day: Int) = if (isEnglish) "Pay on day $day" else "Pago el dia $day"
    fun closingDay(day: Int) = if (isEnglish) "Statement closes on day $day" else "Corte el dia $day"
    fun noClosingDay() = if (isEnglish) "No closing date" else "Sin fecha de corte"
    fun previewCardName() = if (isEnglish) "New card" else "Nueva tarjeta"
    fun addPaymentDay() = if (isEnglish) "Add payment day" else "Agrega dia de pago"
    fun addClosingDay() = if (isEnglish) "Add closing day" else "Agrega dia de corte"
    fun paymentConcept(concept: String, msiOnly: Boolean) = when {
        isEnglish && msiOnly -> "Installment payment: $concept"
        isEnglish -> "Payment: $concept"
        msiOnly -> "Pago MSI: $concept"
        else -> "Pago: $concept"
    }
    fun paymentSupporting(msiOnly: Boolean) = when {
        isEnglish && msiOnly -> "Payment directed to installments"
        isEnglish -> "General payment"
        msiOnly -> "Pago dirigido a MSI"
        else -> "Pago general"
    }
    fun msiConcept(concept: String) = if (isEnglish) "$concept (Installments)" else "$concept (MSI)"
    fun msiSupportingText(installments: Int, monthlyAmount: String) = if (isEnglish) {
        "$installments installments · $monthlyAmount/mo"
    } else {
        "$installments MSI · $monthlyAmount/mes"
    }
    fun remainingInstallments(count: Int) = when {
        isEnglish && count == 1 -> "1 month"
        isEnglish -> "$count months"
        count == 1 -> "1 mes"
        else -> "$count meses"
    }
    fun paidInstallments(count: Int) = when {
        isEnglish && count == 1 -> "1 month paid"
        isEnglish -> "$count months paid"
        count == 1 -> "1 mes pagado"
        else -> "$count meses pagados"
    }
    fun localBrand() = if (isEnglish) "Local" else "Local"
    fun manualPayment() = if (isEnglish) "Manual payment" else "Pago manual"
    fun manualCharge() = if (isEnglish) "Manual expense" else "Gasto manual"
    fun expenseExceedsAvailable() = if (isEnglish) "That expense exceeds the current available amount" else "Ese gasto rebasa el disponible actual"
    fun shortNameRequired() = if (isEnglish) "Add a short name for your card" else "Ponle un nombre corto a tu tarjeta"
    fun validCreditLimit() = if (isEnglish) "Add a valid total limit" else "Agrega un limite total valido"
    fun validAvailable() = if (isEnglish) "Add the current available amount" else "Agrega el disponible actual"
    fun availableNotGreaterThanCredit() = if (isEnglish) "Available amount cannot be higher than the limit" else "El disponible no puede ser mayor al limite"
    fun validPaymentDay() = if (isEnglish) "Payment day must be between 1 and 31" else "El dia de pago debe estar entre 1 y 31"
    fun validClosingDay() = if (isEnglish) "Closing day must be between 1 and 31" else "El dia de corte debe estar entre 1 y 31"
    fun validAmount() = if (isEnglish) "Add a valid amount" else "Agrega un monto valido"
    fun validInstallmentsRange() = if (isEnglish) "Choose between 2 and 24 installments" else "Pon de 2 a 24 meses para este MSI"
}
