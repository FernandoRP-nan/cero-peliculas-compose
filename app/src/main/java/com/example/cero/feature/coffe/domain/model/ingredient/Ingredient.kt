package com.example.cero.feature.coffe.domain.model.ingredient

import androidx.compose.ui.graphics.Color
import com.example.cero.feature.coffe.domain.model.IngredientCategory.IngredientCategory

// ==========================================
// SECTION 1: CORE DOMAIN, STATE & MOCK DATA
// ==========================================


/** Core domain model representing an add-on item in the coffee application. */
data class Ingredient(
    val category: IngredientCategory,
    val displayName: String,
    val price: Double,
    val icon: String,
    val bgColor: Color,
    val contentColor: Color = Color(0xFF333333)
)