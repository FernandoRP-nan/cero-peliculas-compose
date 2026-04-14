package com.example.cero.domain.usecase

import com.example.cero.domain.repository.DeviceProfileRepository
import javax.inject.Inject

class DetectUiPerformanceModeUseCase @Inject constructor(
    private val repository: DeviceProfileRepository
) {
    suspend operator fun invoke() = repository.detectUiPerformanceMode()
}
