package com.example.cero.domain.repository

import com.example.cero.domain.model.UiPerformanceMode

interface DeviceProfileRepository {
    suspend fun detectUiPerformanceMode(): UiPerformanceMode
}
