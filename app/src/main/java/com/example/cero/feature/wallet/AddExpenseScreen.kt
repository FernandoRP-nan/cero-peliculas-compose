package com.example.cero.feature.wallet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.text.KeyboardOptions as FoundationKeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@Composable
internal fun AddExpenseScreen(
    uiState: WalletUiState,
    performanceMode: UiPerformanceMode,
    onBackPressed: () -> Unit,
    onAddExpensePressed: () -> Unit,
    onDismissAddExpense: () -> Unit,
    onExpenseConceptChanged: (String) -> Unit,
    onExpenseAmountChanged: (String) -> Unit,
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
                Text(
                    text = "Movimientos por dia",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
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
                                text = "Todavia no hay gastos en esta tarjeta",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Usa el boton flotante para registrar el primero.",
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
                        fontWeight = FontWeight.Bold
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
    onConceptChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onIsMsiChanged: (Boolean) -> Unit,
    onInstallmentCountChanged: (String) -> Unit,
    onSaveExpense: () -> Unit
) {
    AnimatedVisibility(
        visible = uiState.isAddExpenseVisible,
        enter = fadeIn(animationSpec = tween(160)) + scaleIn(initialScale = 0.96f),
        exit = fadeOut(animationSpec = tween(120)) + scaleOut(targetScale = 0.96f)
    ) {
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
                    .verticalScroll(rememberScrollState())
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Nuevo gasto",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Registralo rapido para recalcular tu disponible.",
                        color = Color(0xFF6A5548)
                    )

                    OutlinedTextField(
                        value = uiState.addExpenseForm.concept,
                        onValueChange = onConceptChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Concepto") },
                        placeholder = { Text("Cafe, super, gasolina") },
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "¿Es compra a MSI?",
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Si activas esto, el gasto quedara identificado como MSI.",
                                color = Color(0xFF6A5548),
                                fontSize = 12.sp
                            )
                            Switch(
                                checked = uiState.addExpenseForm.isMsi,
                                onCheckedChange = onIsMsiChanged
                            )
                        }

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

                    uiState.addExpenseForm.errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = Color(0xFF991B1B),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                text = "Guardar gasto",
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
