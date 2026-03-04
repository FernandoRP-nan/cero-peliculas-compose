package com.example.cero.feature.coffe.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.example.cero.feature.coffe.domain.model.CoffeeSize.CoffeeSize
import com.example.cero.feature.coffe.domain.model.ingredient.Ingredient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


// ==========================================
// SECTION 2: REUSABLE UI COMPONENTS
// ==========================================

/**
 * An interactive, draggable ingredient element.
 * Computes its true spatial center for pixel-perfect physics snap-backs.
 */
@Composable
fun DraggableIngredient(
    ingredient: Ingredient,
    customLabel: String? = null,
    sizeDp: Dp = 72.dp,
    isLightText: Boolean = false,
    onTap: () -> Unit = {},
    onDragStart: (Ingredient, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    isEnabled: Boolean
) {
    var boxCenter by remember { mutableStateOf(Offset.Zero) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .pointerInput(isEnabled) {
                if (!isEnabled) return@pointerInput
                detectTapGestures(onTap = { onTap() })
            }
            .pointerInput(isEnabled) {
                if (!isEnabled) return@pointerInput
                detectDragGestures(
                    onDragStart = { _ ->
                        onDragStart(ingredient, boxCenter)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .size(sizeDp)
                .onGloballyPositioned { coords ->
                    val windowOffset = coords.positionInWindow()
                    val size = coords.size
                    boxCenter = Offset(
                        x = windowOffset.x + size.width / 2f,
                        y = windowOffset.y + size.height / 2f
                    )
                }
                .shadow(
                    if (sizeDp == 72.dp) 12.dp else 4.dp,
                    CircleShape,
                    ambientColor = ingredient.bgColor.copy(alpha = 0.5f),
                    spotColor = ingredient.bgColor
                )
                .background(ingredient.bgColor, CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(ingredient.icon, fontSize = if (sizeDp == 72.dp) 28.sp else 22.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = customLabel ?: ingredient.displayName,
            fontSize = if (sizeDp == 72.dp) 12.sp else 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isLightText) Color.White.copy(alpha = 0.9f) else Color(0xFF555555)
        )
    }
}

/**
 * A dynamic popover that reveals sub-variants of an ingredient.
 * Coordinates alpha handoffs flawlessly with the DragOverlay engine.
 */
@Composable
fun SpatialPopover(
    isVisible: Boolean,
    variants: List<Ingredient>,
    isCheckout: Boolean,
    draggedIngredient: Ingredient?,
    onTap: (Ingredient) -> Unit,
    onDragStart: (Ingredient, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(
            initialScale = 0.0f,
            transformOrigin = TransformOrigin(0.5f, 1f),
            animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium)
        ) + fadeIn(tween(150)),
        exit = scaleOut(
            targetScale = 0.0f,
            transformOrigin = TransformOrigin(0.5f, 1f),
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ) + fadeOut(tween(200))
    ) {
        Row(
            modifier = Modifier
                .shadow(
                    24.dp,
                    RoundedCornerShape(40.dp),
                    spotColor = Color(0xFF3E2723).copy(alpha = 0.25f)
                )
                .background(Color(0xFFFAF9F6).copy(alpha = 0.98f), RoundedCornerShape(40.dp))
                .border(1.dp, Color(0xFFD7CCC8).copy(alpha = 0.8f), RoundedCornerShape(40.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            variants.forEachIndexed { index, variant ->
                var itemVisible by remember { mutableStateOf(false) }

                LaunchedEffect(isVisible) {
                    if (isVisible) {
                        delay(150L + (index * 120L))
                        itemVisible = true
                    } else {
                        itemVisible = false
                    }
                }

                val isBeingDragged = variant == draggedIngredient

                val itemScale by animateFloatAsState(
                    targetValue = if (itemVisible) 1f else 0.4f,
                    animationSpec = if (itemVisible) spring(
                        0.55f,
                        Spring.StiffnessMediumLow
                    ) else tween(150),
                    label = "scale"
                )
                val itemAlpha by animateFloatAsState(
                    targetValue = if (!itemVisible) 0f else if (isBeingDragged) 0f else 1f,
                    animationSpec = if (isBeingDragged || !itemVisible) tween(150) else tween(0),
                    label = "alpha"
                )
                val itemOffsetY by animateDpAsState(
                    targetValue = if (itemVisible) 0.dp else 32.dp,
                    animationSpec = if (itemVisible) spring(
                        0.5f,
                        Spring.StiffnessMediumLow
                    ) else tween(150),
                    label = "offset"
                )

                Box(modifier = Modifier.graphicsLayer {
                    scaleX = itemScale; scaleY = itemScale; alpha = itemAlpha; translationY =
                    itemOffsetY.toPx()
                }) {
                    DraggableIngredient(
                        ingredient = variant,
                        sizeDp = 56.dp,
                        onTap = { onTap(variant) },
                        onDragStart = onDragStart,
                        onDrag = onDrag,
                        onDragEnd = onDragEnd,
                        isEnabled = !isCheckout
                    )
                }
            }
        }
    }
}

/**
 * Top animated segment selector determining the physical dimensions of the cup.
 */
@Composable
fun AnimatedSizeSelector(
    selectedSize: CoffeeSize,
    onSizeSelected: (CoffeeSize) -> Unit,
    modifier: Modifier = Modifier
) {
    val sizes = CoffeeSize.entries
    val selectedIndex = sizes.indexOf(selectedSize)

    val indicatorOffset by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "indicatorOffset"
    )

    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White,
        shadowElevation = 12.dp,
        modifier = modifier
            .padding(horizontal = 24.dp)
            .height(64.dp)
            .fillMaxWidth()
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        ) {
            val segmentWidth = maxWidth / sizes.size
            val leftEdge = indicatorOffset.coerceAtLeast(0f)
            val rightEdge = (indicatorOffset + 1f).coerceAtMost(sizes.size.toFloat())
            val pillWidth = ((rightEdge - leftEdge).coerceAtLeast(0f)) * segmentWidth
            val pillOffsetX = leftEdge * segmentWidth

            Box(
                modifier = Modifier
                    .width(pillWidth)
                    .fillMaxHeight()
                    .offset(x = pillOffsetX)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF2C1E16),
                                Color(0xFF140A05)
                            )
                        ), RoundedCornerShape(50)
                    )
            )

            Row(modifier = Modifier.fillMaxSize()) {
                sizes.forEach { size ->
                    val isSelected = selectedSize == size
                    val textScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1f,
                        animationSpec = spring(
                            dampingRatio = 0.5f,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "scale"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFFFFD54F) else Color(0xFFA1887F),
                        animationSpec = tween(300),
                        label = "color"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onSizeSelected(size) }),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = size.name,
                            color = textColor,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            fontSize = 13.sp,
                            modifier = Modifier.scale(textScale)
                        )
                    }
                }
            }
        }
    }
}

/**
 * A beautiful active pill indicating an applied ingredient.
 * Designed with extensive internal padding boundaries to prevent layout clipping during bounce dynamics.
 */
@Composable
fun ActiveIngredientChip(
    ingredient: Ingredient,
    count: Int,
    onRemove: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val chipScale = remember { Animatable(0f) }
    val chipAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { chipAlpha.animateTo(1f, tween(150)) }
        launch {
            chipScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.5f,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val currentScale = chipScale.value * if (isPressed) 0.90f else 1f

    val perfectPillShape = RoundedCornerShape(percent = 50)

    val fancyTextBrush = remember {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF3E2723),
                Color(0xFFD4AF37)
            ),
            start = Offset(0f, 0f),
            end = Offset(50f, 50f)
        )
    }

    Box(modifier = Modifier.padding(horizontal = 4.dp, vertical = 14.dp)) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = currentScale
                    scaleY = currentScale
                    alpha = chipAlpha.value
                    rotationZ = (1f - chipScale.value) * 12f
                }
                .shadow(
                    elevation = 14.dp,
                    shape = perfectPillShape,
                    ambientColor = ingredient.bgColor.copy(alpha = 0.3f),
                    spotColor = ingredient.bgColor.copy(alpha = 0.5f)
                )
                .background(ingredient.bgColor, perfectPillShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.8f),
                            Color.White.copy(alpha = 0.1f),
                            Color.Black.copy(alpha = 0.15f)
                        )
                    ),
                    shape = perfectPillShape
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.2f)
                        )
                    ),
                    shape = perfectPillShape
                )
                .clip(perfectPillShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        if (count <= 1) {
                            coroutineScope.launch {
                                launch { chipAlpha.animateTo(0f, tween(150)) }
                                chipScale.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = 0.8f,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                                onRemove()
                            }
                        } else {
                            onRemove()
                        }
                    }
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 18.dp, vertical = 10.dp)
                    .animateContentSize(
                        spring(
                            dampingRatio = 0.6f,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = ingredient.icon, fontSize = 24.sp)

                AnimatedVisibility(
                    visible = count > 1,
                    enter = scaleIn(spring(dampingRatio = 0.6f)) + expandHorizontally(),
                    exit = scaleOut(spring(dampingRatio = 0.6f)) + shrinkHorizontally()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = " x",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            style = androidx.compose.ui.text.TextStyle(brush = fancyTextBrush),
                            modifier = Modifier.padding(start = 6.dp)
                        )

                        AnimatedContent(
                            targetState = count,
                            transitionSpec = {
                                if (targetState > initialState) {
                                    (slideInVertically { height -> height } + fadeIn()) togetherWith
                                            (slideOutVertically { height -> -height } + fadeOut())
                                } else {
                                    (slideInVertically { height -> -height } + fadeIn()) togetherWith
                                            (slideOutVertically { height -> height } + fadeOut())
                                }
                            },
                            label = "countRoll"
                        ) { targetCount ->
                            Text(
                                text = "$targetCount",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                style = androidx.compose.ui.text.TextStyle(brush = fancyTextBrush)
                            )
                        }
                    }
                }
            }
        }
    }
}

