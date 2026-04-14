package com.example.cero.feature.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cero.domain.model.CardAccount
import com.example.cero.domain.model.CardExpense
import com.example.cero.domain.model.CardExpenseType
import com.example.cero.domain.model.UiPerformanceMode
import com.example.cero.domain.model.WalletSnapshot
import com.example.cero.domain.model.firstDueDateFor
import com.example.cero.domain.repository.WalletRepository
import com.example.cero.domain.usecase.ObserveWalletSnapshotUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WalletViewModel @Inject constructor(
    observeWalletSnapshotUseCase: ObserveWalletSnapshotUseCase,
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val currentScreen = MutableStateFlow(WalletScreenDestination.Wallet)
    private val isWalletOpen = MutableStateFlow(false)
    private val isCardSelectorVisible = MutableStateFlow(false)
    private val isAddCardVisible = MutableStateFlow(false)
    private val isAddExpenseVisible = MutableStateFlow(false)
    private val selectedCardId = MutableStateFlow<String?>(null)
    private val actionMenuCardId = MutableStateFlow<String?>(null)
    private val transitioningToExpenseCardId = MutableStateFlow<String?>(null)
    private val quickEntryMode = MutableStateFlow<AddExpenseEntryMode?>(null)
    private val addCardMode = MutableStateFlow(AddCardMode.CREATE)
    private val addCardForm = MutableStateFlow(AddCardFormUiState())
    private val addExpenseForm = MutableStateFlow(AddExpenseFormUiState())
    private val storedCardsState = MutableStateFlow<List<CardAccount>>(emptyList())
    private val movementFilterMode = MutableStateFlow(MovementFilterMode.WEEK)
    private val selectedWeekDayKey = MutableStateFlow<String?>(null)
    private val pendingSelectorScrollToHidden = MutableStateFlow(false)

    val uiState: StateFlow<WalletUiState> =
        observeWalletSnapshotUseCase()
            .combine(currentScreen) { snapshot, screen ->
                WalletPresentationState(snapshot = snapshot, currentScreen = screen)
            }
            .combine(isWalletOpen) { snapshot, open ->
                snapshot.copy(isWalletOpen = open)
            }
            .combine(isCardSelectorVisible) { state, selectorVisible ->
                state.copy(isCardSelectorVisible = selectorVisible)
            }
            .combine(isAddCardVisible) { state, addVisible ->
                state.copy(isAddCardVisible = addVisible)
            }
            .combine(isAddExpenseVisible) { state, addExpenseVisible ->
                state.copy(isAddExpenseVisible = addExpenseVisible)
            }
            .combine(selectedCardId) { state, selectedId ->
                state.copy(selectedCardId = selectedId)
            }
            .combine(actionMenuCardId) { state, menuCardId ->
                state.copy(actionMenuCardId = menuCardId)
            }
            .combine(transitioningToExpenseCardId) { state, transitionCardId ->
                state.copy(transitioningToExpenseCardId = transitionCardId)
            }
            .combine(quickEntryMode) { state, quickMode ->
                state.copy(quickEntryMode = quickMode)
            }
            .combine(addCardMode) { state, mode ->
                state.copy(addCardMode = mode)
            }
            .combine(addCardForm) { state, form ->
                state.copy(addCardForm = form)
            }
            .combine(addExpenseForm) { state, form ->
                state.copy(addExpenseForm = form)
            }
            .combine(walletRepository.observeStoredCards()) { state, cards ->
                state.copy(storedCards = cards)
            }
            .combine(movementFilterMode) { state, mode ->
                state.copy(movementFilterMode = mode)
            }
            .combine(selectedWeekDayKey) { state, selectedDay ->
                state.copy(selectedWeekDayKey = selectedDay)
            }
            .combine(walletRepository.observeCardExpenses()) { state, expenses ->
                state.copy(expenses = expenses)
            }
            .combine(pendingSelectorScrollToHidden) { state, pendingScroll ->
                state.snapshot.toUiState(
                    currentScreen = state.currentScreen,
                    isWalletOpen = state.isWalletOpen,
                    isCardSelectorVisible = state.isCardSelectorVisible,
                    isAddCardVisible = state.isAddCardVisible,
                    isAddExpenseVisible = state.isAddExpenseVisible,
                    selectedCardId = state.selectedCardId,
                    actionMenuCardId = state.actionMenuCardId,
                    transitioningToExpenseCardId = state.transitioningToExpenseCardId,
                    quickEntryMode = state.quickEntryMode,
                    addCardMode = state.addCardMode,
                    addCardForm = state.addCardForm,
                    addExpenseForm = state.addExpenseForm,
                    movementFilterMode = state.movementFilterMode,
                    selectedWeekDayKey = state.selectedWeekDayKey,
                    extraCards = emptyList(),
                    editedCards = emptyMap(),
                    expensesByCard = state.expenses.groupBy { it.cardId },
                    pendingSelectorScrollToHidden = pendingScroll
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = WalletUiState()
            )

    init {
        viewModelScope.launch {
            walletRepository.observeStoredCards().collect { cards ->
                storedCardsState.value = cards
            }
        }
    }

    fun onWalletPressed() {
        isWalletOpen.update { current ->
            val next = !current
            if (!next) {
                isCardSelectorVisible.value = false
                actionMenuCardId.value = null
                quickEntryMode.value = null
            }
            next
        }
    }

    fun onPreviewCardPressed(cardId: String) {
        if (!isWalletOpen.value) return
        selectedCardId.value = cardId
        quickEntryMode.value = null
        isCardSelectorVisible.value = true
        actionMenuCardId.value = null
        quickEntryMode.value = null
        pendingSelectorScrollToHidden.value = false
    }

    fun onDismissCardSelector() {
        isCardSelectorVisible.value = false
        actionMenuCardId.value = null
        pendingSelectorScrollToHidden.value = false
    }

    fun onCardSelected(cardId: String) {
        selectedCardId.value = cardId
        quickEntryMode.value = null
        actionMenuCardId.update { current -> if (current == cardId) null else cardId }
    }

    fun onAddCardPressed() {
        isCardSelectorVisible.value = false
        actionMenuCardId.value = null
        pendingSelectorScrollToHidden.value = false
        quickEntryMode.value = null
        addCardMode.value = AddCardMode.CREATE
        addCardForm.value = AddCardFormUiState()
        isAddCardVisible.value = true
    }

    fun onDismissAddCard() {
        isAddCardVisible.value = false
        addCardMode.value = AddCardMode.CREATE
        addCardForm.value = AddCardFormUiState()
    }

    fun onEditCardPressed(cardId: String) {
        val card = resolveCard(cardId) ?: return
        selectedCardId.value = cardId
        isCardSelectorVisible.value = false
        actionMenuCardId.value = null
        quickEntryMode.value = null
        pendingSelectorScrollToHidden.value = false
        addCardMode.value = AddCardMode.EDIT
        addCardForm.value = card.toAddCardForm()
        isAddCardVisible.value = true
    }

    fun onAddExpensePressed(cardId: String, performanceMode: UiPerformanceMode) {
        launchAddExpenseFlow(
            cardId = cardId,
            entryMode = AddExpenseEntryMode.CHARGE,
            performanceMode = performanceMode
        )
    }

    fun onQuickEntryPressed(mode: AddExpenseEntryMode, performanceMode: UiPerformanceMode) {
        val cardIds = storedCardsState.value.map { it.id }.ifEmpty {
            uiState.value.cards.map { it.id }
        }
        when {
            cardIds.isEmpty() -> onAddCardPressed()
            else -> {
                quickEntryMode.value = mode
                isWalletOpen.value = true
                isCardSelectorVisible.value = true
                actionMenuCardId.value = null
                pendingSelectorScrollToHidden.value = false
            }
        }
    }

    fun onQuickCardSelected(cardId: String, performanceMode: UiPerformanceMode) {
        val mode = quickEntryMode.value ?: return
        launchAddExpenseFlow(cardId = cardId, entryMode = mode, performanceMode = performanceMode)
    }

    private fun launchAddExpenseFlow(
        cardId: String,
        entryMode: AddExpenseEntryMode,
        performanceMode: UiPerformanceMode
    ) {
        val card = resolveCard(cardId) ?: return
        selectedCardId.value = card.id
        actionMenuCardId.value = null
        pendingSelectorScrollToHidden.value = false
        quickEntryMode.value = entryMode
        transitioningToExpenseCardId.value = card.id
        isCardSelectorVisible.value = false
        addExpenseForm.value = AddExpenseFormUiState(mode = entryMode).validated()

        viewModelScope.launch {
            kotlinx.coroutines.delay(
                when (performanceMode) {
                    UiPerformanceMode.LOW -> 140L
                    UiPerformanceMode.BALANCED -> 240L
                    UiPerformanceMode.HIGH -> 360L
                }
            )
            currentScreen.value = WalletScreenDestination.AddExpense
            isAddExpenseVisible.value = true
            transitioningToExpenseCardId.value = null
        }
    }

    fun onBackPressed() {
        when {
            isAddExpenseVisible.value -> {
                isAddExpenseVisible.value = false
                addExpenseForm.value = AddExpenseFormUiState()
            }
            currentScreen.value == WalletScreenDestination.AddExpense -> {
                currentScreen.value = WalletScreenDestination.Wallet
                addExpenseForm.value = AddExpenseFormUiState()
                quickEntryMode.value = null
            }
            isAddCardVisible.value -> onDismissAddCard()
            isCardSelectorVisible.value -> onDismissCardSelector()
            isWalletOpen.value -> onWalletPressed()
        }
    }

    fun onAddExpenseFabPressed() {
        isAddExpenseVisible.value = true
        addExpenseForm.value = AddExpenseFormUiState(
            mode = quickEntryMode.value ?: AddExpenseEntryMode.CHARGE
        ).validated()
    }

    fun onDismissAddExpense() {
        isAddExpenseVisible.value = false
        addExpenseForm.value = AddExpenseFormUiState()
    }

    fun onMovementFilterModeChanged(mode: MovementFilterMode) {
        movementFilterMode.value = mode
        if (mode == MovementFilterMode.MONTH) {
            selectedWeekDayKey.value = null
        }
    }

    fun onWeekDaySelected(dayKey: String) {
        selectedWeekDayKey.value = dayKey
    }

    fun onExpenseModeChanged(mode: AddExpenseEntryMode) {
        addExpenseForm.update { current ->
            current.copy(
                mode = mode,
                paymentAllocationMode = if (mode == AddExpenseEntryMode.PAYMENT) {
                    current.paymentAllocationMode
                } else {
                    PaymentAllocationMode.GENERAL
                },
                isMsi = if (mode == AddExpenseEntryMode.CHARGE) current.isMsi else false,
                installmentCount = if (mode == AddExpenseEntryMode.CHARGE) current.installmentCount else ""
            ).validated()
        }
    }

    fun onPaymentAllocationModeChanged(mode: PaymentAllocationMode) {
        addExpenseForm.update { current ->
            current.copy(paymentAllocationMode = mode).validated()
        }
    }

    fun onExpenseConceptChanged(value: String) {
        addExpenseForm.update { current -> current.copy(concept = value.take(40)).validated() }
    }

    fun onExpenseAmountChanged(value: String) {
        addExpenseForm.update { current -> current.copy(amount = value.filterAllowedNumeric()).validated() }
    }

    fun onExpenseIsMsiChanged(value: Boolean) {
        addExpenseForm.update { current ->
            current.copy(
                isMsi = value,
                installmentCount = if (value) current.installmentCount else ""
            ).validated()
        }
    }

    fun onExpenseInstallmentCountChanged(value: String) {
        addExpenseForm.update { current ->
            current.copy(installmentCount = value.filter { it.isDigit() }.take(2)).validated()
        }
    }

    fun onSaveExpense() {
        val current = addExpenseForm.value.copy(hasAttemptedSubmit = true).validated()
        addExpenseForm.value = current
        if (!current.canSubmit) return

        val cardId = selectedCardId.value ?: return
        val selectedCard = resolveSelectedExpenseCard(cardId) ?: return
        val amount = current.amount.toDoubleOrNull() ?: return
        if (current.mode == AddExpenseEntryMode.CHARGE && amount > selectedCard.availableLimitAmount) {
            addExpenseForm.value = current.copy(errorMessage = WalletLocalization.expenseExceedsAvailable())
            return
        }
        val cardAccount = resolveCard(cardId)
        val createdAt = LocalDateTime.now()
        val msiSchedule = if (current.mode == AddExpenseEntryMode.CHARGE && current.isMsi && cardAccount != null) {
            MsiScheduleSnapshot.from(card = cardAccount, purchaseDate = createdAt.toLocalDate())
        } else {
            null
        }

        val newExpense = CardExpense(
            id = "expense-$cardId-${System.currentTimeMillis()}",
            cardId = cardId,
            concept = current.concept.ifBlank {
                if (current.mode == AddExpenseEntryMode.PAYMENT) {
                    WalletLocalization.manualPayment()
                } else {
                    WalletLocalization.manualCharge()
                }
            },
            amount = amount,
            createdAt = createdAt,
            entryType = if (current.mode == AddExpenseEntryMode.PAYMENT) {
                CardExpenseType.PAYMENT
            } else {
                CardExpenseType.CHARGE
            },
            isMsi = current.mode == AddExpenseEntryMode.CHARGE && current.isMsi,
            installmentCount = if (current.mode == AddExpenseEntryMode.CHARGE) current.installmentCount.toIntOrNull() else null,
            monthlyInstallmentAmount = if (current.mode == AddExpenseEntryMode.CHARGE) current.monthlyInstallmentAmount() else 0.0,
            financingId = if (current.mode == AddExpenseEntryMode.CHARGE && current.isMsi) {
                "msi-$cardId-${System.currentTimeMillis()}"
            } else {
                null
            },
            firstDueDate = msiSchedule?.firstDueDate,
            paymentDaySnapshot = msiSchedule?.paymentDay,
            closingDaySnapshot = msiSchedule?.closingDay,
            paymentAllocationMode = current.paymentAllocationMode.toDomain()
        )
        viewModelScope.launch {
            walletRepository.saveExpense(newExpense)
        }
        isAddExpenseVisible.value = false
        addExpenseForm.value = AddExpenseFormUiState()
        quickEntryMode.value = null
    }

    fun onHiddenCardsPressed() {
        if (!isWalletOpen.value) return
        isCardSelectorVisible.value = true
        pendingSelectorScrollToHidden.value = true
        actionMenuCardId.value = null
    }

    fun onHiddenCardsScrollHandled() {
        pendingSelectorScrollToHidden.value = false
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

        val cardId = if (addCardMode.value == AddCardMode.EDIT) selectedCardId.value else null
        val newCard = current.toCardAccount(existingCardId = cardId)
        val cardToPersist = if (addCardMode.value == AddCardMode.EDIT && cardId != null) {
            mergeCardIdentity(existing = resolveCard(cardId), updated = newCard)
        } else {
            newCard
        }

        viewModelScope.launch {
            walletRepository.saveCard(cardToPersist)
        }
        selectedCardId.value = newCard.id
        isAddCardVisible.value = false
        addCardMode.value = AddCardMode.CREATE
        addCardForm.value = AddCardFormUiState()
        isWalletOpen.value = true
    }

    private fun updateForm(transform: AddCardFormUiState.() -> AddCardFormUiState) {
        addCardForm.update { current -> current.transform() }
    }

    private fun resolveCard(cardId: String): CardAccount? {
        return storedCardsState.value.firstOrNull { it.id == cardId }
            ?: uiState.value.cards.firstOrNull { it.id == cardId }?.toCardAccount()
    }

    private fun resolveSelectedExpenseCard(cardId: String): WalletCardUiModel? {
        return uiState.value.expenseCard?.takeIf { it.id == cardId }
            ?: uiState.value.cards.firstOrNull { it.id == cardId }
    }

    private fun WalletCardUiModel.toCardAccount(): CardAccount {
        return CardAccount(
            id = id,
            displayName = name,
            bankName = bankName.takeIf { it.isNotBlank() },
            brand = brand.takeIf { it.isNotBlank() },
            lastDigits = lastDigits,
            creditLimit = creditLimitAmount,
            availableLimit = availableLimitAmount,
            paymentDay = paymentDayText.filter(Char::isDigit).toIntOrNull() ?: 1,
            closingDay = closingDayText?.filter(Char::isDigit)?.toIntOrNull(),
            monthlyInstallmentPayment = monthlyPaymentAmount,
            pendingInstallments = 0,
            pendingMsiBalance = pendingInstallmentsAmount,
            paidMsi = paidMsiText.filter(Char::isDigit).toIntOrNull() ?: 0
        )
    }

    private fun mergeCardIdentity(existing: CardAccount?, updated: CardAccount): CardAccount {
        return updated.copy(
            lastDigits = existing?.lastDigits.orEmpty(),
            monthlyInstallmentPayment = existing?.monthlyInstallmentPayment ?: 0.0,
            pendingInstallments = existing?.pendingInstallments ?: 0,
            pendingMsiBalance = existing?.pendingMsiBalance ?: 0.0,
            paidMsi = existing?.paidMsi ?: 0
        )
    }
}

private data class WalletPresentationState(
    val snapshot: WalletSnapshot,
    val currentScreen: WalletScreenDestination = WalletScreenDestination.Wallet,
    val isWalletOpen: Boolean = false,
    val isCardSelectorVisible: Boolean = false,
    val isAddCardVisible: Boolean = false,
    val isAddExpenseVisible: Boolean = false,
    val selectedCardId: String? = null,
    val actionMenuCardId: String? = null,
    val transitioningToExpenseCardId: String? = null,
    val quickEntryMode: AddExpenseEntryMode? = null,
    val addCardMode: AddCardMode = AddCardMode.CREATE,
    val addCardForm: AddCardFormUiState = AddCardFormUiState(),
    val addExpenseForm: AddExpenseFormUiState = AddExpenseFormUiState(),
    val movementFilterMode: MovementFilterMode = MovementFilterMode.WEEK,
    val selectedWeekDayKey: String? = null,
    val storedCards: List<CardAccount> = emptyList(),
    val expenses: List<CardExpense> = emptyList()
)

private data class MsiScheduleSnapshot(
    val firstDueDate: LocalDate,
    val paymentDay: Int,
    val closingDay: Int?
) {
    companion object {
        fun from(card: CardAccount, purchaseDate: LocalDate): MsiScheduleSnapshot {
            return MsiScheduleSnapshot(
                firstDueDate = card.firstDueDateFor(purchaseDate),
                paymentDay = card.paymentDay,
                closingDay = card.closingDay
            )
        }
    }
}
