package com.example.cero.feature.splash

import com.example.cero.domain.model.UiPerformanceMode

data class SplashUiState(
    val isReady: Boolean = false,
    val performanceMode: UiPerformanceMode = UiPerformanceMode.BALANCED
)
