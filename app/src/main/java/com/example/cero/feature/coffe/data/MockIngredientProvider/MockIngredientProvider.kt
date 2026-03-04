package com.example.cero.feature.coffe.data.MockIngredientProvider

import androidx.compose.ui.graphics.Color
import com.example.cero.feature.coffe.domain.model.IngredientCategory.IngredientCategory
import com.example.cero.feature.coffe.domain.model.ingredient.Ingredient

// ==========================================
// SECTION 1: CORE DOMAIN, STATE & MOCK DATA
// ==========================================

// Data Providers (Mock Database)
val SugarOptions = listOf(
    Ingredient(
        IngredientCategory.SUGAR,
        "Plain",
        0.0,
        "🥄",
        Color(0xFFFFFFFF)
    ),
    Ingredient(
        IngredientCategory.SUGAR,
        "Brown",
        0.0,
        "🤎",
        Color(0xFFD7CCC8)
    ),
    Ingredient(
        IngredientCategory.SUGAR,
        "Stevia",
        0.0,
        "🌿",
        Color(0xFFE8F5E9),
        Color(0xFF2E7D32)
    )
)

val MilkOptions = listOf(
    Ingredient(
        IngredientCategory.MILK,
        "Whole",
        0.0,
        "🥛",
        Color(0xFFF5F5F7),
        Color(0xFF333333)
    ),
    Ingredient(
        IngredientCategory.MILK,
        "Oat",
        0.50,
        "🌾",
        Color(0xFFFFF8E1),
        Color(0xFFF57F17)
    ),
    Ingredient(
        IngredientCategory.MILK,
        "Almond",
        0.50,
        "🥜",
        Color(0xFFEFEBE9),
        Color(0xFF5D4037)
    ),
    Ingredient(
        IngredientCategory.MILK,
        "Coconut",
        0.50,
        "🥥",
        Color(0xFFFAFAFA),
        Color(0xFF4E342E)
    )
)

val EspressoOptions = listOf(
    Ingredient(
        IngredientCategory.ESPRESSO,
        "Espresso",
        1.20,
        "☕",
        Color(0xFF3E2723),
        Color(0xFFD7CCC8)
    )
)

val BaseIngredients = listOf(SugarOptions.first(), MilkOptions.first(), EspressoOptions.first())

