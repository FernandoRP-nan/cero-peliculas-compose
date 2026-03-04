package com.example.cero.feature.coffe.presentation.animations.MorphingCoffeeCup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cero.feature.coffe.presentation.state.AppState.AppState
import com.example.cero.feature.coffe.domain.model.CoffeeSize.CoffeeSize
import com.example.cero.feature.coffe.domain.model.OrderState.OrderState
import com.example.cero.feature.coffe.presentation.animations.MorphingReceiptShape.MorphingReceiptShape
import com.example.cero.feature.coffe.presentation.animations.SpatialDotsLoader.SpatialDotsLoader
import kotlinx.coroutines.launch


// ==========================================
// SECTION 3: COMPLEX VISUALS & MORPHING CANVAS
// ==========================================


/**
 * Complex central staging hub drawing the 3D rendered cup, spatial loaders, and receipt matrix.
 * Implements severe rendering optimizations via hoisted Path resets and cached Brush instances.
 */
@Composable
fun MorphingCoffeeCup(
    coffeeSize: CoffeeSize,
    ingredientCount: Int,
    appState: AppState,
    orderState: OrderState,
    modifier: Modifier = Modifier
) {
    val targetWidth = when (appState) {
        AppState.BREWING -> when (coffeeSize) {
            CoffeeSize.SMALL -> 120.dp; CoffeeSize.MEDIUM -> 150.dp; CoffeeSize.LARGE -> 180.dp
        }

        AppState.PROCESSING -> 140.dp
        AppState.RECEIPT -> 320.dp
    }

    val targetHeight = when (appState) {
        AppState.BREWING -> when (coffeeSize) {
            CoffeeSize.SMALL -> 180.dp; CoffeeSize.MEDIUM -> 240.dp; CoffeeSize.LARGE -> 300.dp
        }

        AppState.PROCESSING -> 100.dp
        AppState.RECEIPT -> 480.dp
    }

    val animatedWidth by animateDpAsState(
        targetWidth,
        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "width"
    )
    val animatedHeight by animateDpAsState(
        targetHeight,
        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "height"
    )

    val paperAlpha by animateFloatAsState(
        targetValue = if (appState == AppState.RECEIPT) 1f else 0f,
        animationSpec = if (appState == AppState.RECEIPT) tween(500) else tween(0),
        label = "paperAlpha"
    )

    val cornerRadius by animateFloatAsState(
        targetValue = when (appState) {
            AppState.BREWING -> 0f; AppState.PROCESSING -> 200f; AppState.RECEIPT -> 24f
        },
        animationSpec = tween(400), label = "corner"
    )

    val shadowElevationDp by animateDpAsState(
        targetValue = if (appState == AppState.RECEIPT) 24.dp else 0.dp,
        animationSpec = if (appState == AppState.RECEIPT) tween(400) else tween(0),
        label = "shadow"
    )

    val jaggedProgress by animateFloatAsState(
        if (appState == AppState.RECEIPT) 1f else 0f,
        tween(400),
        label = "jaggedEdge"
    )

    val contentAlpha by animateFloatAsState(
        if (appState == AppState.BREWING) 1f else 0f,
        tween(300),
        label = "contentAlpha"
    )

    val scale = remember { Animatable(1f) }
    val splashAlpha = remember { Animatable(0f) }

    LaunchedEffect(ingredientCount) {
        if (ingredientCount > 0 && appState == AppState.BREWING) {
            launch {
                scale.animateTo(1.04f, spring(stiffness = Spring.StiffnessMedium))
                scale.animateTo(
                    1f,
                    spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch { splashAlpha.snapTo(0.5f); splashAlpha.animateTo(0f, tween(500)) }
        }
    }

    val cupBodyPath = remember { Path() }
    val sleevePath = remember { Path() }
    val domePath = remember { Path() }
    val domeBottomPath = remember { Path() }

    val cupBodyBrush = remember {
        Brush.horizontalGradient(
            0.0f to Color(0xFFE0E0E0),
            0.15f to Color(0xFFFFFFFF),
            0.45f to Color(0xFFD6D6D6),
            0.80f to Color(0xFF9E9E9E),
            1.0f to Color(0xFF757575)
        )
    }
    val sleeveBrush = remember {
        Brush.horizontalGradient(
            0.0f to Color(0xFF2C2F33),
            0.15f to Color(0xFF454A4E),
            0.45f to Color(0xFF1E2124),
            0.80f to Color(0xFF0D0F10),
            1.0f to Color(0xFF050607)
        )
    }
    val bandingBrush = remember {
        Brush.horizontalGradient(
            0.0f to Color(0xFF8D6E63),
            0.15f to Color(0xFFFFF59D),
            0.45f to Color(0xFFA1887F),
            0.80f to Color(0xFF4E342E),
            1.0f to Color(0xFF21110C)
        )
    }
    val lidRimBrush = remember {
        Brush.horizontalGradient(
            0.0f to Color(0xFFE0E0E0),
            0.15f to Color(0xFFFFFFFF),
            0.45f to Color(0xFFD6D6D6),
            0.80f to Color(0xFF9E9E9E),
            1.0f to Color(0xFF757575)
        )
    }
    val domeGradientBrush = remember {
        Brush.horizontalGradient(
            0.0f to Color(0xFFF5F5F5),
            0.15f to Color(0xFFFFFFFF),
            0.45f to Color(0xFFE0E0E0),
            0.80f to Color(0xFFAFAFAF),
            1.0f to Color(0xFF8A8A8A)
        )
    }

    Box(
        modifier = modifier
            .size(width = animatedWidth, height = animatedHeight)
            .scale(scale.value)
            .graphicsLayer {
                shadowElevation = shadowElevationDp.toPx()
                shape = MorphingReceiptShape(cornerRadius, jaggedProgress)
                clip = appState == AppState.RECEIPT
                ambientShadowColor = Color.Black.copy(alpha = 0.05f * paperAlpha)
                spotShadowColor = Color.Black.copy(alpha = 0.15f * paperAlpha)
            }
            .background(
                color = Color(0xFFFAF9F6).copy(alpha = paperAlpha),
                shape = MorphingReceiptShape(cornerRadius, jaggedProgress)
            ),
        contentAlignment = Alignment.Center
    ) {
        // --- LAYER 1: 3D CANVAS CUP ---
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = contentAlpha }) {
            if (appState != AppState.BREWING) return@Canvas
            val canvasWidth = size.width
            val canvasHeight = size.height
            val lidHeight = canvasHeight * 0.12f
            val cupBodyTop = lidHeight * 0.85f
            val cupBodyHeight = canvasHeight - cupBodyTop
            val bottomTaperRatio = 0.72f
            val bottomWidth = canvasWidth * bottomTaperRatio
            val leftXBottom = (canvasWidth - bottomWidth) / 2f
            val rightXBottom = leftXBottom + bottomWidth

            drawOval(
                Brush.radialGradient(
                    listOf(
                        Color(0x80000000),
                        Color(0x20000000),
                        Color.Transparent
                    ), center = Offset((canvasWidth / 2f) + 15f, canvasHeight)
                ),
                topLeft = Offset(leftXBottom - 30f, canvasHeight - 15f),
                size = Size(bottomWidth + 80f, 30f)
            )

            // OPTIMIZATION: Recycle the path instances
            cupBodyPath.apply {
                reset()
                moveTo(0f, cupBodyTop)
                lineTo(canvasWidth, cupBodyTop)
                lineTo(rightXBottom, canvasHeight)
                lineTo(leftXBottom, canvasHeight)
                close()
            }

            drawPath(cupBodyPath, cupBodyBrush)

            val baseLiquidFill = when (coffeeSize) {
                CoffeeSize.SMALL -> 0.45f; CoffeeSize.MEDIUM -> 0.60f; CoffeeSize.LARGE -> 0.75f
            }
            val liquidHeight =
                cupBodyHeight * (baseLiquidFill + (ingredientCount * 0.05f)).coerceAtMost(0.9f)
            val liquidTopY = canvasHeight - liquidHeight

            clipPath(cupBodyPath) {
                drawRect(
                    Brush.horizontalGradient(
                        0.0f to Color(0xFF3E2723),
                        0.15f to Color(0xFF5D4037),
                        0.45f to Color(0xFF26140E),
                        0.80f to Color(0xFF140905),
                        1.0f to Color(0xFF0A0402)
                    ), topLeft = Offset(0f, liquidTopY), size = Size(canvasWidth, liquidHeight)
                )
                // Note: explicit bounds vertical gradients must be declared during the draw cycle
                drawRect(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color(0x22000000),
                            Color(0x99120602)
                        ), startY = liquidTopY, endY = canvasHeight
                    ), topLeft = Offset(0f, liquidTopY), size = Size(canvasWidth, liquidHeight)
                )
                drawOval(
                    color = Color(0xFF050201).copy(alpha = 0.5f),
                    topLeft = Offset(0f, liquidTopY - 6f),
                    size = Size(canvasWidth, 12f)
                )
            }

            val sleeveHeight = cupBodyHeight * 0.40f
            val sleeveTopY = cupBodyTop + (cupBodyHeight - sleeveHeight) / 2.5f
            val sleeveBottomY = sleeveTopY + sleeveHeight

            fun getXAtY(y: Float): Pair<Float, Float> {
                val progress = (y - cupBodyTop) / cupBodyHeight
                return (leftXBottom * progress) to (canvasWidth + (rightXBottom - canvasWidth) * progress)
            }
            val (sLeftT, sRightT) = getXAtY(sleeveTopY)
            val (sLeftB, sRightB) = getXAtY(sleeveBottomY)

            sleevePath.apply {
                reset()
                moveTo(sLeftT, sleeveTopY)
                lineTo(sRightT, sleeveTopY)
                lineTo(sRightB, sleeveBottomY)
                lineTo(sLeftB, sleeveBottomY)
                close()
            }

            drawPath(sleevePath, sleeveBrush)

            val brandingLineY = sleeveBottomY - (sleeveHeight * 0.1f)
            val (bLeft, bRight) = getXAtY(brandingLineY)
            drawLine(
                brush = bandingBrush,
                start = Offset(bLeft, brandingLineY),
                end = Offset(bRight, brandingLineY),
                strokeWidth = 3f
            )

            drawRect(
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.25f),
                        Color.Transparent
                    )
                ), topLeft = Offset(0f, cupBodyTop), size = Size(canvasWidth, 16f)
            )

            val lidRimHeight = lidHeight * 0.35f
            val lidRimY = lidHeight - lidRimHeight
            drawRoundRect(
                Color.Black.copy(alpha = 0.15f),
                topLeft = Offset(-3f, lidRimY + 4f),
                size = Size(canvasWidth + 6f, lidRimHeight),
                cornerRadius = CornerRadius(6f, 6f)
            )
            drawRoundRect(
                brush = lidRimBrush,
                topLeft = Offset(-4f, lidRimY),
                size = Size(canvasWidth + 8f, lidRimHeight),
                cornerRadius = CornerRadius(6f, 6f)
            )

            domePath.apply {
                reset()
                moveTo(canvasWidth * 0.08f, lidRimY)
                lineTo(canvasWidth * 0.18f, 0f)
                lineTo(canvasWidth * 0.82f, 0f)
                lineTo(canvasWidth * 0.92f, lidRimY)
                close()
            }
            drawPath(domePath, domeGradientBrush)

            domeBottomPath.apply {
                reset()
                moveTo(canvasWidth * 0.08f, lidRimY)
                lineTo(canvasWidth * 0.92f, lidRimY)
                lineTo(canvasWidth * 0.88f, lidRimY - 6f)
                lineTo(canvasWidth * 0.12f, lidRimY - 6f)
                close()
            }
            drawPath(
                domeBottomPath,
                Brush.verticalGradient(
                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.2f)),
                    startY = lidRimY - 6f,
                    endY = lidRimY
                )
            )
            drawOval(
                Brush.radialGradient(
                    listOf(Color(0xFF050505), Color(0xFF222222)),
                    center = Offset(canvasWidth * 0.5f, lidHeight * 0.10f)
                ),
                topLeft = Offset(canvasWidth * 0.42f, lidHeight * 0.05f),
                size = Size(canvasWidth * 0.16f, lidHeight * 0.12f)
            )
        }

        // --- LAYER 2: SPATIAL LOADER ---
        AnimatedVisibility(
            visible = appState == AppState.PROCESSING,
            enter = fadeIn(tween(delayMillis = 200, durationMillis = 300)),
            exit = fadeOut(tween(200))
        ) {
            SpatialDotsLoader()
        }

        // --- LAYER 3: RECEIPT CONTENT ---
        AnimatedVisibility(
            visible = appState == AppState.RECEIPT,
            enter = fadeIn(tween(delayMillis = 300, durationMillis = 400)),
            exit = fadeOut(tween(100))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val receiptFont = androidx.compose.ui.text.font.FontFamily.Monospace
                val inkColor = Color(0xFF2B2B2B)
                val lightInk = Color(0xFF666666)

                Text(
                    "C A F E  C O M P O S E",
                    fontFamily = receiptFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = inkColor
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "123 Jetpack Lane",
                    fontFamily = receiptFont,
                    fontSize = 10.sp,
                    color = lightInk
                )
                Text(
                    "Terminal #04 • Cashier: Jet",
                    fontFamily = receiptFont,
                    fontSize = 10.sp,
                    color = lightInk
                )
                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        "DATE: 10/24/2026",
                        fontFamily = receiptFont,
                        fontSize = 10.sp,
                        color = lightInk
                    )
                    Text(
                        "TIME: 08:42 AM",
                        fontFamily = receiptFont,
                        fontSize = 10.sp,
                        color = lightInk
                    )
                }
                Spacer(Modifier.height(12.dp))

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                ) {
                    drawLine(
                        lightInk,
                        Offset(0f, 0f),
                        Offset(size.width, 0f),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            floatArrayOf(10f, 10f),
                            0f
                        )
                    )
                }
                Spacer(Modifier.height(12.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        "QTY ITEM",
                        fontFamily = receiptFont,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = inkColor
                    )
                    Text(
                        "PRICE",
                        fontFamily = receiptFont,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = inkColor
                    )
                }
                Spacer(Modifier.height(8.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        "1   ${orderState.size.name} COFFEE",
                        fontFamily = receiptFont,
                        fontSize = 14.sp,
                        color = inkColor
                    )
                    Text(
                        "$${String.format("%.2f", orderState.size.basePrice)}",
                        fontFamily = receiptFont,
                        fontSize = 14.sp,
                        color = inkColor
                    )
                }
                Spacer(Modifier.height(4.dp))

                if (orderState.ingredients.isNotEmpty()) {
                    val groupedIngredients = orderState.ingredients.groupingBy { it }.eachCount()
                    groupedIngredients.forEach { (ingredient, count) ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "      + ${count}x ${ingredient.displayName.uppercase()}",
                                fontFamily = receiptFont,
                                fontSize = 12.sp,
                                color = lightInk
                            )
                            Text(
                                "$${String.format("%.2f", ingredient.price * count)}",
                                fontFamily = receiptFont,
                                fontSize = 12.sp,
                                color = lightInk
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                ) {
                    drawLine(
                        lightInk,
                        Offset(0f, 0f),
                        Offset(size.width, 0f),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            floatArrayOf(
                                10f,
                                10f
                            ), 0f
                        )
                    )
                }
                Spacer(Modifier.height(12.dp))

                // OPTIMIZATION: Math derived during drawing Phase.
                // Uses values that are static given the orderState dependency above.
                val subtotal = orderState.size.basePrice + orderState.ingredients.sumOf { it.price }
                val tax = subtotal * 0.08
                val total = subtotal + tax

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "SUBTOTAL",
                        fontFamily = receiptFont,
                        fontSize = 12.sp,
                        color = lightInk
                    )
                    Text(
                        "$${String.format("%.2f", subtotal)}",
                        fontFamily = receiptFont,
                        fontSize = 12.sp,
                        color = lightInk
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "TAX (8%)",
                        fontFamily = receiptFont,
                        fontSize = 12.sp,
                        color = lightInk
                    )
                    Text(
                        "$${String.format("%.2f", tax)}",
                        fontFamily = receiptFont,
                        fontSize = 12.sp,
                        color = lightInk
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        "TOTAL",
                        fontFamily = receiptFont,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = inkColor
                    )
                    Text(
                        "$${String.format("%.2f", total)}",
                        fontFamily = receiptFont,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = inkColor
                    )
                }

                Spacer(Modifier.weight(1f))
                Text(
                    "CARD - APPROVED",
                    fontFamily = receiptFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkColor
                )
                Spacer(Modifier.height(16.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .height(44.dp)
                    ) {
                        val bitmask =
                            "10110011010110111001011010011101011011100101101011100101101110010110"
                        val moduleWidth = size.width / bitmask.length
                        bitmask.forEachIndexed { index, bit ->
                            if (bit == '1') drawRect(
                                color = inkColor,
                                topLeft = Offset(index * moduleWidth, 0f),
                                size = Size(moduleWidth + 0.5f, size.height)
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(0.75f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("0", fontFamily = receiptFont, fontSize = 10.sp, color = inkColor)
                        Text("12345", fontFamily = receiptFont, fontSize = 10.sp, color = inkColor)
                        Text("67890", fontFamily = receiptFont, fontSize = 10.sp, color = inkColor)
                        Text("5", fontFamily = receiptFont, fontSize = 10.sp, color = inkColor)
                    }
                }
            }
        }
    }
}