package com.example.cero.feature.coffe.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cero.feature.coffe.data.MockIngredientProvider.BaseIngredients
import com.example.cero.feature.coffe.data.MockIngredientProvider.MilkOptions
import com.example.cero.feature.coffe.data.MockIngredientProvider.SugarOptions
import com.example.cero.feature.coffe.presentation.state.AppState.AppState
import com.example.cero.feature.coffe.domain.model.IngredientCategory.IngredientCategory
import com.example.cero.feature.coffe.domain.model.OrderState.OrderState
import com.example.cero.feature.coffe.presentation.animations.GeometricCafeBackground.GeometricCafeBackground
import com.example.cero.feature.coffe.presentation.animations.MorphingCoffeeCup.MorphingCoffeeCup
import com.example.cero.feature.coffe.presentation.components.ActiveIngredientChip
import com.example.cero.feature.coffe.presentation.components.AnimatedSizeSelector
import com.example.cero.feature.coffe.presentation.components.DraggableIngredient
import com.example.cero.feature.coffe.presentation.components.SpatialPopover
import com.example.cero.feature.coffe.presentation.state.DragState.DragState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


// ==========================================
// SECTION 4: MAIN SCREEN ORCHESTRATION
// ==========================================

/**
 * Top-level view orchestrating the entire lifecycle of a coffee order.
 * Connects the data models to the spatial drag-and-drop mechanics.
 */
@Composable
fun CoffeeOrderScreen() {
    var appState by remember { mutableStateOf(AppState.BREWING) }
    val isBrewing = appState == AppState.BREWING

    var orderState by remember { mutableStateOf(OrderState()) }
    var dragState by remember { mutableStateOf<DragState>(DragState.None) }
    var cupDropZoneBounds by remember { mutableStateOf(Rect.Zero) }
    var popoverState by remember { mutableStateOf<IngredientCategory?>(null) }

    val activeDraggedIngredient = when (val state = dragState) {
        is DragState.Dragging -> state.ingredient
        is DragState.Returning -> state.ingredient
        else -> null
    }

    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {

        // --- 1. AMBIENT BACKGROUND ---
        GeometricCafeBackground(appState = appState, modifier = Modifier.fillMaxSize())

        // --- 2. TOP SIZE SELECTOR ---
        AnimatedVisibility(
            visible = isBrewing,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 64.dp),
            enter = fadeIn(tween(300)) + slideInVertically(initialOffsetY = { -50 }),
            exit = fadeOut(tween(400)) + scaleOut(targetScale = 0.6f) + slideOutVertically(
                targetOffsetY = { 200 })
        ) {
            AnimatedSizeSelector(
                selectedSize = orderState.size,
                onSizeSelected = { orderState = orderState.copy(size = it) }
            )
        }

        // --- 3. ACTIVE INGREDIENTS BADGES ---
        AnimatedVisibility(
            visible = isBrewing && orderState.ingredients.isNotEmpty(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 140.dp)
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            enter = fadeIn(tween(300)) + scaleIn(spring(dampingRatio = 0.55f)) + slideInVertically(
                initialOffsetY = { -30 }),
            exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.8f)
        ) {
            val groupedIngredients = orderState.ingredients.groupingBy { it }.eachCount()
            @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
            androidx.compose.foundation.layout.FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.animateContentSize(
                    spring(
                        dampingRatio = 0.6f,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            ) {
                groupedIngredients.forEach { (ingredient, count) ->
                    androidx.compose.runtime.key(ingredient.displayName) {
                        ActiveIngredientChip(ingredient = ingredient, count = count, onRemove = {
                            val updatedList = orderState.ingredients.toMutableList()
                            updatedList.remove(ingredient)
                            orderState = orderState.copy(ingredients = updatedList)
                        })
                    }
                }
            }
        }

        // --- 4. STAGING AREA (CUP / LOADER / RECEIPT) ---
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-40).dp)
                .onGloballyPositioned { coordinates ->
                    cupDropZoneBounds = coordinates.boundsInRoot()
                }
        ) {
            MorphingCoffeeCup(
                coffeeSize = orderState.size,
                ingredientCount = orderState.ingredients.size,
                appState = appState,
                orderState = orderState
            )
        }

        // --- 5. SCRIM ---
        AnimatedVisibility(
            visible = popoverState != null,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.04f))
                    .pointerInput(Unit) { detectTapGestures(onTap = { popoverState = null }) })
        }

        // --- 6. FLOATING SPATIAL POPOVERS ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 248.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            BaseIngredients.forEach { baseIngredient ->
                val variants = when (baseIngredient.category) {
                    IngredientCategory.SUGAR -> SugarOptions; IngredientCategory.MILK -> MilkOptions; else -> emptyList()
                }

                SpatialPopover(
                    isVisible = popoverState == baseIngredient.category,
                    variants = variants,
                    isCheckout = !isBrewing,
                    draggedIngredient = activeDraggedIngredient,
                    onTap = { variant ->
                        orderState = orderState.copy(ingredients = orderState.ingredients + variant)
                        popoverState = null
                    },
                    onDragStart = { ing, startOffset ->
                        dragState = DragState.Dragging(ing, startOffset, startOffset)
                    },
                    onDrag = { dragAmount ->
                        if (dragState is DragState.Dragging) {
                            val current = dragState as DragState.Dragging
                            dragState =
                                current.copy(currentPosition = current.currentPosition + dragAmount)
                        }
                    },
                    onDragEnd = {
                        if (dragState is DragState.Dragging) {
                            val current = dragState as DragState.Dragging
                            if (cupDropZoneBounds.contains(current.currentPosition)) {
                                orderState =
                                    orderState.copy(ingredients = orderState.ingredients + current.ingredient)
                                dragState = DragState.None
                                popoverState = null
                            } else {
                                dragState =
                                    DragState.Returning(current.ingredient, current.initialPosition)
                            }
                        }
                    }
                )
            }
        }

        // --- 7. MAIN INGREDIENT DOCK ---
        AnimatedVisibility(
            visible = isBrewing,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 144.dp),
            enter = fadeIn(tween(300)) + slideInVertically(initialOffsetY = { 50 }),
            exit = fadeOut(tween(400)) + scaleOut(targetScale = 0.6f) + slideOutVertically(
                targetOffsetY = { -200 })
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                BaseIngredients.forEach { baseIngredient ->
                    val isPopoverOpen = popoverState == baseIngredient.category
                    val buttonScale by animateFloatAsState(
                        if (isPopoverOpen) 0.85f else 1f,
                        spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
                        label = "buttonScale"
                    )
                    val dockCategoryName = when (baseIngredient.category) {
                        IngredientCategory.SUGAR -> "Sugar"; IngredientCategory.MILK -> "Milk"; IngredientCategory.ESPRESSO -> "Espresso"
                    }

                    val isBeingDragged = baseIngredient == activeDraggedIngredient
                    val buttonAlpha by animateFloatAsState(
                        targetValue = if (isBeingDragged) 0f else 1f,
                        animationSpec = if (isBeingDragged) tween(150) else tween(0),
                        label = "buttonAlpha"
                    )

                    Box(
                        modifier = Modifier
                            .scale(buttonScale)
                            .graphicsLayer { alpha = buttonAlpha }
                    ) {
                        DraggableIngredient(
                            ingredient = baseIngredient,
                            customLabel = dockCategoryName,
                            sizeDp = 72.dp,
                            isEnabled = isBrewing,
                            isLightText = false,
                            onTap = {
                                if (baseIngredient.category == IngredientCategory.ESPRESSO) {
                                    orderState =
                                        orderState.copy(ingredients = orderState.ingredients + baseIngredient)
                                } else {
                                    popoverState =
                                        if (popoverState == baseIngredient.category) null else baseIngredient.category
                                }
                            },
                            onDragStart = { ing, startOffset ->
                                popoverState = null
                                dragState = DragState.Dragging(
                                    ingredient = ing,
                                    initialPosition = startOffset,
                                    currentPosition = startOffset
                                )
                            },
                            onDrag = { dragAmount ->
                                if (dragState is DragState.Dragging) {
                                    val current = dragState as DragState.Dragging
                                    dragState =
                                        current.copy(currentPosition = current.currentPosition + dragAmount)
                                }
                            },
                            onDragEnd = {
                                if (dragState is DragState.Dragging) {
                                    val current = dragState as DragState.Dragging
                                    if (cupDropZoneBounds.contains(current.currentPosition)) {
                                        orderState =
                                            orderState.copy(ingredients = orderState.ingredients + current.ingredient)
                                        dragState = DragState.None
                                    } else {
                                        dragState = DragState.Returning(
                                            current.ingredient,
                                            current.initialPosition
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // --- 8. CHECKOUT BUTTON ---
        val currentTotal = remember(orderState) {
            orderState.size.basePrice + orderState.ingredients.sumOf { it.price }
        }

        val buttonText = when (appState) {
            AppState.BREWING -> "SWIPE TO PAY • $${String.format("%.2f", currentTotal)}"
            AppState.PROCESSING -> "CRAFTING ORDER..."
            AppState.RECEIPT -> "BREW ANOTHER"
        }

        PremiumGradientButton(
            text = buttonText,
            onClick = {
                when (appState) {
                    AppState.BREWING -> {
                        appState = AppState.PROCESSING
                        popoverState = null

                        coroutineScope.launch {
                            delay(2500)
                            appState = AppState.RECEIPT
                        }
                    }

                    AppState.PROCESSING -> { /* No-op */
                    }

                    AppState.RECEIPT -> {
                        orderState = OrderState()
                        appState = AppState.BREWING
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp, start = 32.dp, end = 32.dp)
                .fillMaxWidth()
        )

        // --- 9. GLOBAL DRAG OVERLAY ---
        if (dragState !is DragState.None) {
            val isReturning = dragState is DragState.Returning
            val draggedIngredient = when (val state = dragState) {
                is DragState.Dragging -> state.ingredient
                is DragState.Returning -> state.ingredient
                else -> return@Box
            }

            val targetPosition = when (val state = dragState) {
                is DragState.Dragging -> state.currentPosition
                is DragState.Returning -> state.initialPosition
                else -> Offset.Zero
            }

            val superSlowReturnSpec =
                tween<Offset>(durationMillis = 1000, easing = FastOutSlowInEasing)
            val superSlowFloatSpec =
                tween<Float>(durationMillis = 1000, easing = FastOutSlowInEasing)

            val displayPosition by animateOffsetAsState(
                targetValue = targetPosition,
                animationSpec = if (isReturning) superSlowReturnSpec else snap(),
                finishedListener = {
                    if (dragState is DragState.Returning) {
                        dragState = DragState.None
                        popoverState = null
                    }
                },
                label = "dragPosition"
            )

            val overlayScale by animateFloatAsState(
                targetValue = if (isReturning) 1.0f else 1.2f,
                animationSpec = if (isReturning) superSlowFloatSpec else spring(
                    0.6f,
                    Spring.StiffnessMedium
                ),
                label = "overlayScale"
            )

            val overlayRotation by animateFloatAsState(
                targetValue = if (isReturning) 0f else 5f,
                animationSpec = if (isReturning) superSlowFloatSpec else spring(
                    0.6f,
                    Spring.StiffnessMedium
                ),
                label = "overlayRotation"
            )

            val density = LocalDensity.current
            val halfSizePx = with(density) { 36.dp.toPx() }

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (displayPosition.x - halfSizePx).roundToInt(),
                            (displayPosition.y - halfSizePx).roundToInt()
                        )
                    }
                    .size(72.dp)
                    .graphicsLayer {
                        scaleX = overlayScale
                        scaleY = overlayScale
                        rotationZ = overlayRotation
                    }
                    .shadow(if (isReturning) 12.dp else 24.dp, CircleShape)
                    .background(draggedIngredient.bgColor, CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = draggedIngredient.icon, fontSize = 28.sp)
            }
        }
    }
}

/**
 * Aesthetic CTA button supporting an animated inner gradient cycle.
 */
@Composable
fun PremiumGradientButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (isPressed) 0.96f else 1f,
        spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
        label = "buttonScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "buttonGradient")
    val phase by infiniteTransition.animateFloat(
        0f,
        1000f,
        infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Reverse),
        label = "phase"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                if (isPressed) 8.dp else 24.dp,
                RoundedCornerShape(32.dp),
                spotColor = Color(0xFFD4AF37).copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF2C1E16),
                        Color(0xFF0A0502),
                        Color(0xFF3E2723)
                    ), Offset(phase, 0f), Offset(phase + 600f, 300f)
                )
            )
            .border(
                1.5.dp,
                Brush.linearGradient(
                    listOf(
                        Color(0xFFFFD54F).copy(alpha = 0.8f),
                        Color(0xFFFFD54F).copy(alpha = 0.1f),
                        Color(0xFFFFD54F).copy(alpha = 0.5f)
                    ), Offset(0f, 0f), Offset(1000f - phase, 500f)
                ),
                RoundedCornerShape(32.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFFFAF9F6),
            letterSpacing = 1.5.sp
        )
    }
}