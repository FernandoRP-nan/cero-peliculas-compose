package com.example.cero.feature.wallet

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cero.domain.model.UiPerformanceMode
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.text.KeyboardOptions as FoundationKeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

private val addExpenseCurrencyFormatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

@Composable
internal fun AddExpenseScreen(
    uiState: WalletUiState,
    performanceMode: UiPerformanceMode,
    onBackPressed: () -> Unit,
    onAddExpensePressed: () -> Unit,
    onDismissAddExpense: () -> Unit,
    onMovementFilterModeChanged: (MovementFilterMode) -> Unit,
    onWeekDaySelected: (String) -> Unit,
    onExpenseConceptChanged: (String) -> Unit,
    onExpenseAmountChanged: (String) -> Unit,
    onExpenseModeChanged: (AddExpenseEntryMode) -> Unit,
    onExpenseIsMsiChanged: (Boolean) -> Unit,
    onExpenseInstallmentCountChanged: (String) -> Unit,
    onSaveExpense: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable(onClick = onBackPressed)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "<",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.size(14.dp))

                    Column {
                        Text(
                            text = "Agregar gastos",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Cada gasto baja el disponible actual de esta tarjeta.",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.68f)
                        )
                    }
                }
            }

            uiState.expenseCard?.let { card ->
                item {
                    ExpenseCardHero(card = card, performanceMode = performanceMode)
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Movimientos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    MovementFilterToggle(
                        selectedMode = uiState.movementFilterMode,
                        performanceMode = performanceMode,
                        onModeSelected = onMovementFilterModeChanged
                    )
                    AnimatedVisibility(visible = uiState.movementFilterMode == MovementFilterMode.WEEK) {
                        WeekDaySelector(
                            chips = uiState.weekDayChips,
                            onDaySelected = onWeekDaySelected
                        )
                    }
                }
            }

            if (uiState.expenseGroups.isEmpty()) {
                item {
                    Card(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Todavia no hay movimientos en esta tarjeta",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Usa el boton flotante para registrar un gasto o un pago.",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f)
                            )
                        }
                    }
                }
            } else {
                items(uiState.expenseGroups, key = { it.dateLabel }) { group ->
                    ExpenseDayGroup(group = group)
                }
            }
        }

        FloatingActionButton(
            onClick = onAddExpensePressed,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp),
            containerColor = Color(0xFF9B5D35),
            contentColor = Color(0xFFFFF7ED)
        ) {
            Text(
                text = "+",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }

        AddExpenseOverlay(
            uiState = uiState,
            performanceMode = performanceMode,
            onDismiss = onDismissAddExpense,
            onExpenseModeChanged = onExpenseModeChanged,
            onConceptChanged = onExpenseConceptChanged,
            onAmountChanged = onExpenseAmountChanged,
            onIsMsiChanged = onExpenseIsMsiChanged,
            onInstallmentCountChanged = onExpenseInstallmentCountChanged,
            onSaveExpense = onSaveExpense
        )
    }
}

@Composable
private fun ExpenseCardHero(
    card: WalletCardUiModel,
    performanceMode: UiPerformanceMode
) {
    Card(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (performanceMode == UiPerformanceMode.LOW) 176.dp else 196.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(30.dp))
                .background(Brush.horizontalGradient(listOf(Color(card.accentStart), Color(card.accentEnd))))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = card.brand.ifBlank { "Local" },
                        color = Color.White.copy(alpha = 0.74f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = card.name,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ExpenseCardMetric(title = "Disponible", value = card.availableLimitText)
                    ExpenseCardMetric(title = "MSI al mes", value = card.monthlyPaymentText)
                }
            }
        }
    }
}

@Composable
private fun ExpenseCardMetric(
    title: String,
    value: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            color = Color.White.copy(alpha = 0.74f),
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MovementFilterToggle(
    selectedMode: MovementFilterMode,
    performanceMode: UiPerformanceMode,
    onModeSelected: (MovementFilterMode) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FilterMotionChip(
            text = "Semana",
            selected = selectedMode == MovementFilterMode.WEEK,
            compact = performanceMode == UiPerformanceMode.LOW,
            onClick = { onModeSelected(MovementFilterMode.WEEK) }
        )
        FilterMotionChip(
            text = "Mes",
            selected = selectedMode == MovementFilterMode.MONTH,
            compact = performanceMode == UiPerformanceMode.LOW,
            onClick = { onModeSelected(MovementFilterMode.MONTH) }
        )
    }
}

@Composable
private fun FilterMotionChip(
    text: String,
    selected: Boolean,
    compact: Boolean,
    onClick: () -> Unit
) {
    val background by animateColorAsState(
        targetValue = if (selected) Color(0xFF9B5D35) else Color(0xFFE8D8C7),
        animationSpec = tween(durationMillis = if (compact) 140 else 220, easing = FastOutSlowInEasing),
        label = "filter-chip-bg"
    )
    val content by animateColorAsState(
        targetValue = if (selected) Color(0xFFFFF7ED) else Color(0xFF5B3A24),
        animationSpec = tween(durationMillis = if (compact) 140 else 220, easing = FastOutSlowInEasing),
        label = "filter-chip-content"
    )

    Box(
        modifier = Modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Text(text = text, color = content, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun WeekDaySelector(
    chips: List<MovementDayChipUiModel>,
    onDaySelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        chips.forEach { chip ->
            val background by animateColorAsState(
                targetValue = if (chip.isSelected) Color(0xFF243B53) else Color(0xFFF3E7D8),
                animationSpec = tween(180),
                label = "week-chip-bg"
            )
            Box(
                modifier = Modifier
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                    .background(background)
                    .clickable { onDaySelected(chip.key) }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = chip.label,
                        color = if (chip.isSelected) Color.White else Color(0xFF5B3A24),
                        fontSize = 12.sp
                    )
                    Text(
                        text = chip.dayNumber,
                        color = if (chip.isSelected) Color.White else Color(0xFF2D1D14),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpenseDayGroup(group: ExpenseDayGroupUiModel) {
    Card(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = group.dateLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${group.expenses.size} movimientos",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f)
                    )
                }
                Text(
                    text = group.totalAmountText,
                    color = Color(0xFF9B5D35),
                    fontWeight = FontWeight.ExtraBold
                )
            }

            group.expenses.forEach { expense ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = expense.concept,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = listOfNotNull(expense.timeLabel, expense.supportingText).joinToString(" · "),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f),
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = expense.amountText,
                        fontWeight = FontWeight.Bold,
                        color = if (expense.isPositive) Color(0xFF166534) else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun AddExpenseOverlay(
    uiState: WalletUiState,
    performanceMode: UiPerformanceMode,
    onDismiss: () -> Unit,
    onExpenseModeChanged: (AddExpenseEntryMode) -> Unit,
    onConceptChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onIsMsiChanged: (Boolean) -> Unit,
    onInstallmentCountChanged: (String) -> Unit,
    onSaveExpense: () -> Unit
) {
    val overlayScrollState = rememberScrollState()
    val expensePreview = remember(uiState.expenseCard, uiState.addExpenseForm) {
        buildExpensePreview(
            card = uiState.expenseCard,
            form = uiState.addExpenseForm
        )
    }

    AnimatedVisibility(
        visible = uiState.isAddExpenseVisible,
        enter = fadeIn(animationSpec = tween(160)) + scaleIn(initialScale = 0.96f),
        exit = fadeOut(animationSpec = tween(120)) + scaleOut(targetScale = 0.96f)
    ) {
        BackHandler(onBack = onDismiss)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x990F0A07))
                .clickable(onClick = onDismiss)
                .padding(horizontal = 18.dp, vertical = 22.dp)
                .imePadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .heightIn(max = if (performanceMode == UiPerformanceMode.LOW) 420.dp else 660.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F3EA))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(overlayScrollState)
                            .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = if (uiState.addExpenseForm.mode == AddExpenseEntryMode.PAYMENT) "Registrar pago" else "Nuevo gasto",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (uiState.addExpenseForm.mode == AddExpenseEntryMode.PAYMENT) {
                                "Un pago libera credito y baja lo que debes."
                            } else {
                                "Un gasto baja disponible. Si es MSI, tambien suma al pago mensual."
                            },
                            color = Color(0xFF6A5548)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FilterMotionChip(
                                text = "Gasto",
                                selected = uiState.addExpenseForm.mode == AddExpenseEntryMode.CHARGE,
                                compact = performanceMode == UiPerformanceMode.LOW,
                                onClick = { onExpenseModeChanged(AddExpenseEntryMode.CHARGE) }
                            )
                            FilterMotionChip(
                                text = "Pago",
                                selected = uiState.addExpenseForm.mode == AddExpenseEntryMode.PAYMENT,
                                compact = performanceMode == UiPerformanceMode.LOW,
                                onClick = { onExpenseModeChanged(AddExpenseEntryMode.PAYMENT) }
                            )
                        }

                        ExpenseLiveSummary(preview = expensePreview)

                        OutlinedTextField(
                            value = uiState.addExpenseForm.concept,
                            onValueChange = onConceptChanged,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(if (uiState.addExpenseForm.mode == AddExpenseEntryMode.PAYMENT) "Referencia" else "Concepto") },
                            placeholder = {
                                Text(
                                    if (uiState.addExpenseForm.mode == AddExpenseEntryMode.PAYMENT) {
                                        "Pago al corte, abono, transferencia"
                                    } else {
                                        "Cafe, super, gasolina"
                                    }
                                )
                            },
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = uiState.addExpenseForm.amount,
                            onValueChange = onAmountChanged,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Monto*") },
                            placeholder = { Text("350") },
                            singleLine = true,
                            keyboardOptions = FoundationKeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )

                        if (uiState.addExpenseForm.mode == AddExpenseEntryMode.CHARGE) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Es compra a MSI",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Quedara identificada para el pago mensual y futuro seguimiento.",
                                        color = Color(0xFF6A5548),
                                        fontSize = 12.sp
                                    )
                                }
                                Switch(
                                    checked = uiState.addExpenseForm.isMsi,
                                    onCheckedChange = onIsMsiChanged
                                )
                            }

                            if (uiState.addExpenseForm.isMsi) {
                                OutlinedTextField(
                                    value = uiState.addExpenseForm.installmentCount,
                                    onValueChange = onInstallmentCountChanged,
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Meses sin intereses") },
                                    placeholder = { Text("Ejemplo: 3, 6, 12") },
                                    singleLine = true,
                                    keyboardOptions = FoundationKeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }
                        }

                        uiState.addExpenseForm.errorMessage?.let { error ->
                            Text(
                                text = error,
                                color = Color(0xFF991B1B),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9F3EA))
                            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp, top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                                .background(Color(0xFFE8D8C7))
                                .clickable(onClick = onDismiss)
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cancelar",
                                color = Color(0xFF5B3A24),
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                                .background(Color(0xFF9B5D35))
                                .clickable(onClick = onSaveExpense)
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (uiState.addExpenseForm.mode == AddExpenseEntryMode.PAYMENT) "Registrar pago" else "Guardar gasto",
                                color = Color(0xFFFFF7ED),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpenseLiveSummary(preview: ExpensePreviewUiModel) {
    Card(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1E4D4))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExpensePreviewMetric(title = "Disponible", value = preview.availableText)
            ExpensePreviewMetric(title = "Debes", value = preview.debtText)
            ExpensePreviewMetric(title = "MSI al mes", value = preview.monthlyText)
        }
    }
}

@Composable
private fun RowScope.ExpensePreviewMetric(
    title: String,
    value: String
) {
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            color = Color(0xFF6A5548),
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = Color(0xFF2D1D14),
            fontWeight = FontWeight.Bold
        )
    }
}

private data class ExpensePreviewUiModel(
    val availableText: String,
    val debtText: String,
    val monthlyText: String
)

private fun buildExpensePreview(
    card: WalletCardUiModel?,
    form: AddExpenseFormUiState
): ExpensePreviewUiModel {
    if (card == null) {
        return ExpensePreviewUiModel(
            availableText = addExpenseCurrencyFormatter.format(0),
            debtText = addExpenseCurrencyFormatter.format(0),
            monthlyText = addExpenseCurrencyFormatter.format(0)
        )
    }

    val amount = form.amount.toDoubleOrNull() ?: 0.0
    val isCharge = form.mode == AddExpenseEntryMode.CHARGE
    val projectedAvailable = when {
        amount <= 0.0 -> card.availableLimitAmount
        isCharge -> (card.availableLimitAmount - amount).coerceAtLeast(0.0)
        else -> (card.availableLimitAmount + amount).coerceAtMost(card.creditLimitAmount)
    }
    val projectedDebt = when {
        amount <= 0.0 -> card.usedLimitAmount
        isCharge -> card.usedLimitAmount + amount
        else -> (card.usedLimitAmount - amount).coerceAtLeast(0.0)
    }
    val projectedMonthly = card.monthlyPaymentAmount + if (isCharge && form.isMsi) {
        form.previewMonthlyInstallmentAmount()
    } else {
        0.0
    }

    return ExpensePreviewUiModel(
        availableText = addExpenseCurrencyFormatter.format(projectedAvailable),
        debtText = addExpenseCurrencyFormatter.format(projectedDebt),
        monthlyText = addExpenseCurrencyFormatter.format(projectedMonthly)
    )
}

private fun AddExpenseFormUiState.previewMonthlyInstallmentAmount(): Double {
    val amountValue = amount.toDoubleOrNull() ?: return 0.0
    val installments = installmentCount.toIntOrNull() ?: return 0.0
    if (!isMsi || installments <= 0) return 0.0
    return amountValue / installments
}
