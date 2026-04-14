package com.example.cero.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cero.domain.usecase.DetectUiPerformanceModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val detectUiPerformanceModeUseCase: DetectUiPerformanceModeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val detectedMode = detectUiPerformanceModeUseCase()
            delay(950)
            _uiState.update { current ->
                current.copy(
                    isReady = true,
                    performanceMode = detectedMode
                )
            }
        }
    }
}
