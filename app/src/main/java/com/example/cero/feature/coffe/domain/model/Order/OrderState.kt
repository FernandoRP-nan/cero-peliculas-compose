package com.example.cero.feature.coffe.domain.model.OrderState

import com.example.cero.feature.coffe.domain.model.CoffeeSize.CoffeeSize
import com.example.cero.feature.coffe.domain.model.ingredient.Ingredient


// ==========================================
// SECTION 1: CORE DOMAIN, STATE & MOCK DATA
// ==========================================


/** Represents the user's active configuration of their drink. */
data class OrderState(
    val size: CoffeeSize = CoffeeSize.MEDIUM,
    val ingredients: List<Ingredient> = emptyList()
)
