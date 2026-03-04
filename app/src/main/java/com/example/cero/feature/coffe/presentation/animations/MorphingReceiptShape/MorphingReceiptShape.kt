package com.example.cero.feature.coffe.presentation.animations.MorphingReceiptShape

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

class MorphingReceiptShape(private val cornerRadiusPx: Float, private val jaggedProgress: Float) :
    Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        if (jaggedProgress == 0f) {
            return Outline.Rounded(
                RoundRect(
                    Rect(Offset.Zero, size),
                    CornerRadius(cornerRadiusPx, cornerRadiusPx)
                )
            )
        }

        val path = Path().apply {
            moveTo(0f, cornerRadiusPx)
            quadraticBezierTo(0f, 0f, cornerRadiusPx, 0f)
            lineTo(size.width - cornerRadiusPx, 0f)
            quadraticBezierTo(size.width, 0f, size.width, cornerRadiusPx)
            lineTo(size.width, size.height)

            val toothWidth = with(density) { 6.dp.toPx() }
            val toothHeight = with(density) { 4.dp.toPx() } * jaggedProgress
            val numTeeth = (size.width / toothWidth).toInt()
            for (i in 0 until numTeeth) {
                val x = size.width - (i * toothWidth)
                lineTo(x - (toothWidth / 2), size.height - toothHeight)
                lineTo(x - toothWidth, size.height)
            }
            lineTo(0f, cornerRadiusPx)
            close()
        }
        return Outline.Generic(path)
    }
}