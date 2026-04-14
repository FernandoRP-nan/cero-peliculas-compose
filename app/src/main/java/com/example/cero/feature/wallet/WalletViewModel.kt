package com.example.cero.feature.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cero.domain.model.CardAccount
import com.example.cero.domain.model.WalletSnapshot
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
    private val isAddCardVisible = MutableStateFlow(false)
    private val selectedCardId = MutableStateFlow<String?>(null)
    private val addCardForm = MutableStateFlow(AddCardFormUiState())
    private val extraCards = MutableStateFlow<List<CardAccount>>(emptyList())

    val uiState: StateFlow<WalletUiState> =
        observeWalletSnapshotUseCase()
            .combine(isWalletOpen) { snapshot, open ->
                WalletPresentationState(
                    snapshot = snapshot,
                    isWalletOpen = open
                )
            }
            .combine(isCardSelectorVisible) { state, selectorVisible ->
                state.copy(isCardSelectorVisible = selectorVisible)
            }
            .combine(isAddCardVisible) { state, addVisible ->
                state.copy(isAddCardVisible = addVisible)
            }
            .combine(selectedCardId) { state, selectedId ->
                state.copy(selectedCardId = selectedId)
            }
            .combine(addCardForm) { state, form ->
                state.copy(addCardForm = form)
            }
            .combine(extraCards) { state, localCards ->
                state.snapshot.toUiState(
                    isWalletOpen = state.isWalletOpen,
                    isCardSelectorVisible = state.isCardSelectorVisible,
                    isAddCardVisible = state.isAddCardVisible,
                    selectedCardId = state.selectedCardId,
                    addCardForm = state.addCardForm,
                    extraCards = localCards
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

    fun onAddCardPressed() {
        isCardSelectorVisible.value = false
        isAddCardVisible.value = true
    }

    fun onDismissAddCard() {
        isAddCardVisible.value = false
        addCardForm.value = AddCardFormUiState()
    }

    fun onShortNameChanged(value: String) {
        updateForm { copy(shortName = value).validated() }
    }

    fun onBankNameChanged(value: String) {
        updateForm { copy(bankName = value).validated() }
    }

    fun onBrandChanged(value: CardBrandOption) {
        updateForm { copy(brand = value).validated() }
    }

    fun onCreditLimitChanged(value: String) {
        updateForm { copy(creditLimit = value.filterAllowedNumeric()).validated() }
    }

    fun onAvailableLimitChanged(value: String) {
        updateForm { copy(availableLimit = value.filterAllowedNumeric()).validated() }
    }

    fun onPaymentDayChanged(value: String) {
        updateForm { copy(paymentDay = value.filter { it.isDigit() }.take(2)).validated() }
    }

    fun onHasClosingDayChanged(value: Boolean) {
        updateForm {
            copy(
                hasClosingDay = value,
                closingDay = if (value) closingDay else ""
            ).validated()
        }
    }

    fun onClosingDayChanged(value: String) {
        updateForm { copy(closingDay = value.filter { it.isDigit() }.take(2)).validated() }
    }

    fun onSaveCard() {
        val current = addCardForm.value.copy(hasAttemptedSubmit = true).validated()
        addCardForm.value = current
        if (!current.canSubmit) return

        val newCard = current.toCardAccount()
        extraCards.update { listOf(newCard) + it }
        selectedCardId.value = newCard.id
        isAddCardVisible.value = false
        addCardForm.value = AddCardFormUiState()
        isWalletOpen.value = true
    }

    private fun updateForm(transform: AddCardFormUiState.() -> AddCardFormUiState) {
        addCardForm.update { current -> current.transform() }
    }
}

private data class WalletPresentationState(
    val snapshot: WalletSnapshot,
    val isWalletOpen: Boolean,
    val isCardSelectorVisible: Boolean = false,
    val isAddCardVisible: Boolean = false,
    val selectedCardId: String? = null,
    val addCardForm: AddCardFormUiState = AddCardFormUiState()
)

private fun AddCardFormUiState.validated(): AddCardFormUiState {
    val credit = creditLimit.toDoubleOrNull()
    val available = availableLimit.toDoubleOrNull()
    val payment = paymentDay.toIntOrNull()
    val closing = closingDay.toIntOrNull()

    val error = when {
        shortName.isBlank() -> "Ponle un nombre corto a tu tarjeta"
        credit == null || credit <= 0.0 -> "Agrega un limite total valido"
        available == null || available < 0.0 -> "Agrega el disponible actual"
        available > credit -> "El disponible no puede ser mayor al limite"
        payment == null || payment !in 1..31 -> "El dia de pago debe estar entre 1 y 31"
        hasClosingDay && (closing == null || closing !in 1..31) -> "El dia de corte debe estar entre 1 y 31"
        else -> null
    }

    return copy(
        canSubmit = error == null,
        errorMessage = if (hasAttemptedSubmit) error else null
    )
}

private fun String.filterAllowedNumeric(): String {
    var hasDot = false
    return buildString {
        for (char in this@filterAllowedNumeric) {
            when {
                char.isDigit() -> append(char)
                char == '.' && !hasDot -> {
                    hasDot = true
                    append(char)
                }
            }
        }
    }
}
