package com.example.cero.feature.coffe.presentation.state.DragState

import androidx.compose.ui.geometry.Offset
import com.example.cero.feature.coffe.domain.model.ingredient.Ingredient

// ==========================================
// SECTION 1: CORE DOMAIN, STATE & MOCK DATA
// ==========================================


/**
 * Defines the physics-based drag states for ingredient interaction.
 * Provides granular tracking of an ingredient's origin and current floating point.
 */
sealed class DragState {
    data object None : DragState()
    data class Dragging(
        val ingredient: Ingredient,
        val initialPosition: Offset,
        val currentPosition: Offset
    ) : DragState()
    data class Returning(
        val ingredient: Ingredient,
        val initialPosition: Offset
    ) : DragState()
}