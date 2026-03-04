package com.example.cero.feature.coffe.domain.model.CoffeeSize

// ==========================================
// SECTION 1: CORE DOMAIN, STATE & MOCK DATA
// ==========================================

/** Represents the physical size of the coffee cup and its base impact on price. */
enum class CoffeeSize(val basePrice: Double) {
    SMALL(3.50), MEDIUM(4.50), LARGE(5.50)
}
