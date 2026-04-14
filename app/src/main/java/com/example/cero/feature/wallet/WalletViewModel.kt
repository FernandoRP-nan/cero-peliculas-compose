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
    private val isCardSelectorVisible = MutableStateFlow(false)
    private val selectedCardId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<WalletUiState> =
        observeWalletSnapshotUseCase()
            .combine(isWalletOpen) { snapshot, open ->
                snapshot to open
            }
            .combine(isCardSelectorVisible) { (snapshot, open), selectorVisible ->
                Triple(snapshot, open, selectorVisible)
            }
            .combine(selectedCardId) { (snapshot, open, selectorVisible), selectedId ->
                snapshot.toUiState(
                    isWalletOpen = open,
                    isCardSelectorVisible = selectorVisible,
                    selectedCardId = selectedId
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = WalletUiState()
            )

    fun onWalletPressed() {
        isWalletOpen.update { current ->
            val next = !current
            if (!next) {
                isCardSelectorVisible.value = false
            }
            next
        }
    }

    fun onPreviewCardPressed(cardId: String) {
        if (!isWalletOpen.value) return
        selectedCardId.value = cardId
        isCardSelectorVisible.value = true
    }

    fun onDismissCardSelector() {
        isCardSelectorVisible.value = false
    }

    fun onCardSelected(cardId: String) {
        selectedCardId.value = cardId
        isCardSelectorVisible.value = false
    }
}
