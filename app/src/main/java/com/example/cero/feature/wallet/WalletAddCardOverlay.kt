package com.example.cero.feature.wallet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cero.domain.model.UiPerformanceMode
import androidx.compose.foundation.text.KeyboardOptions as FoundationKeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@Composable
internal fun AddCardFab(
    performanceMode: UiPerformanceMode,
    reduceEffects: Boolean,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = tween(
            durationMillis = if (performanceMode == UiPerformanceMode.LOW || reduceEffects) 160 else 320,
            easing = FastOutSlowInEasing
        ),
        label = "add-fab-rotation"
    )

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = Color(0xFF9B5D35),
        contentColor = Color(0xFFFFF7ED)
    ) {
        Text(
            text = "+",
            modifier = Modifier.graphicsLayer { rotationZ = rotation },
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
internal fun AddCardOverlay(
    uiState: WalletUiState,
    performanceMode: UiPerformanceMode,
    reduceEffects: Boolean,
    onDismiss: () -> Unit,
    onShortNameChanged: (String) -> Unit,
    onBankNameChanged: (String) -> Unit,
    onBrandChanged: (CardBrandOption) -> Unit,
    onCreditLimitChanged: (String) -> Unit,
    onAvailableLimitChanged: (String) -> Unit,
    onPaymentDayChanged: (String) -> Unit,
    onHasClosingDayChanged: (Boolean) -> Unit,
    onClosingDayChanged: (String) -> Unit,
    onSaveCard: () -> Unit
) {
    AnimatedVisibility(
        visible = uiState.isAddCardVisible,
        enter = if (performanceMode == UiPerformanceMode.LOW || reduceEffects) {
            fadeIn(animationSpec = tween(160))
        } else {
            fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.96f)
        },
        exit = if (performanceMode == UiPerformanceMode.LOW || reduceEffects) {
            fadeOut(animationSpec = tween(120))
        } else {
            fadeOut(animationSpec = tween(180)) + scaleOut(targetScale = 0.96f)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x990F0A07))
                .clickable(onClick = onDismiss)
                .padding(horizontal = 18.dp, vertical = 22.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.86f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    ),
                shape = RoundedCornerShape(34.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F3EA))
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (uiState.addCardMode == AddCardMode.EDIT) "Edita tu tarjeta" else "Agrega una tarjeta",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D1D14)
                            )
                            Text(
                                text = if (uiState.addCardMode == AddCardMode.EDIT) {
                                    "Ajusta solo lo necesario y manten tu registro local al dia."
                                } else {
                                    "Captura solo lo necesario para arrancar con calculos reales."
                                },
                                color = Color(0xFF6A5548)
                            )
                        }
                    }

                    item {
                        AddCardPreviewCard(
                            preview = uiState.addCardForm.toPreviewCardUiModel(),
                            performanceMode = performanceMode
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.addCardForm.shortName,
                            onValueChange = onShortNameChanged,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Nombre corto*") },
                            placeholder = { Text("Nu, BBVA Azul, Santander") },
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.addCardForm.bankName,
                            onValueChange = onBankNameChanged,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Banco (opcional)") },
                            placeholder = { Text("BBVA, Nu, Banamex") },
                            singleLine = true
                        )
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Marca (opcional)",
                                color = Color(0xFF6A5548),
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                FilterChip(
                                    selected = uiState.addCardForm.brand == CardBrandOption.NONE,
                                    onClick = { onBrandChanged(CardBrandOption.NONE) },
                                    label = { Text("Sin marca") }
                                )
                                FilterChip(
                                    selected = uiState.addCardForm.brand == CardBrandOption.VISA,
                                    onClick = { onBrandChanged(CardBrandOption.VISA) },
                                    label = { Text("Visa") }
                                )
                                FilterChip(
                                    selected = uiState.addCardForm.brand == CardBrandOption.MASTERCARD,
                                    onClick = { onBrandChanged(CardBrandOption.MASTERCARD) },
                                    label = { Text("Mastercard") }
                                )
                            }
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = uiState.addCardForm.creditLimit,
                                onValueChange = onCreditLimitChanged,
                                modifier = Modifier.weight(1f),
                                label = { Text("Limite total*") },
                                singleLine = true,
                                keyboardOptions = FoundationKeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                            OutlinedTextField(
                                value = uiState.addCardForm.availableLimit,
                                onValueChange = onAvailableLimitChanged,
                                modifier = Modifier.weight(1f),
                                label = { Text("Disponible*") },
                                singleLine = true,
                                keyboardOptions = FoundationKeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.addCardForm.paymentDay,
                            onValueChange = onPaymentDayChanged,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Dia de pago*") },
                            placeholder = { Text("Ejemplo: 15") },
                            supportingText = { Text("Usa el dia del mes en el que normalmente pagas esta tarjeta.") },
                            singleLine = true,
                            keyboardOptions = FoundationKeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Agregar fecha de corte",
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF2D1D14)
                                    )
                                    Text(
                                        text = "Es opcional. Si no la sabes, puedes continuar sin ella.",
                                        color = Color(0xFF6A5548),
                                        fontSize = 13.sp
                                    )
                                }
                                Switch(
                                    checked = uiState.addCardForm.hasClosingDay,
                                    onCheckedChange = onHasClosingDayChanged
                                )
                            }

                            if (uiState.addCardForm.hasClosingDay) {
                                OutlinedTextField(
                                    value = uiState.addCardForm.closingDay,
                                    onValueChange = onClosingDayChanged,
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Dia de corte") },
                                    placeholder = { Text("Ejemplo: 8") },
                                   // supportingText = { Text("Aqui conviene un dia del mes, no un calendario completo.") },
                                    singleLine = true,
                                    keyboardOptions = FoundationKeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }
                        }
                    }

                    uiState.addCardForm.errorMessage?.let { error ->
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFFBE4E1))
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = error,
                                    color = Color(0xFF991B1B),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
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
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (uiState.addCardForm.canSubmit) Color(0xFF9B5D35) else Color(0xFFCDB7A2)
                                    )
                                    .clickable(enabled = uiState.addCardForm.canSubmit, onClick = onSaveCard)
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (uiState.addCardMode == AddCardMode.EDIT) "Guardar cambios" else "Guardar tarjeta",
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
}

@Composable
private fun AddCardPreviewCard(
    preview: WalletCardUiModel,
    performanceMode: UiPerformanceMode
) {
    Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (performanceMode == UiPerformanceMode.LOW) 180.dp else 194.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Brush.horizontalGradient(listOf(Color(preview.accentStart), Color(preview.accentEnd))))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (preview.bankName.isBlank()) "Tu banco" else preview.bankName,
                            color = Color.White.copy(alpha = 0.78f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = preview.name,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = preview.brand.ifBlank { "Sin marca" },
                        color = Color.White.copy(alpha = 0.92f),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "****  ****  ****  ${preview.lastDigits.ifBlank { "0000" }}",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Disponible",
                            color = Color.White.copy(alpha = 0.78f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = preview.availableLimitText,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = preview.paymentDayText,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = preview.closingDayText ?: "Sin fecha de corte",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}
