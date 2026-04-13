package com.example.cero.feature.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cero.domain.usecase.ObserveWalletSnapshotUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class WalletViewModel @Inject constructor(
    observeWalletSnapshotUseCase: ObserveWalletSnapshotUseCase
) : ViewModel() {

    private val isWalletOpen = MutableStateFlow(false)

    val uiState: StateFlow<WalletUiState> =
        observeWalletSnapshotUseCase()
            .combine(isWalletOpen) { snapshot, open ->
                snapshot.toUiState(isWalletOpen = open)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = WalletUiState()
            )

    fun onWalletPressed() {
        isWalletOpen.update { current -> !current }
    }
}
