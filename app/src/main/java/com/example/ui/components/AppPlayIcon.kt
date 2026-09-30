package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.PureWhite
import com.example.ui.theme.YellowPrimary

/**
 * Official Ennovelas Logo & Play Icon:
 * - Rounded Square / Box (not a circle)
 * - Deep Blue Background (#0652AD)
 * - Vibrant Golden Yellow Border (#FBD502)
 * - Pure White Centered Play Triangle (#FFFFFF)
 */
@Composable
fun AppPlayIcon(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    backgroundColor: Color = BluePrimary,
    borderColor: Color = YellowPrimary,
    iconColor: Color = PureWhite,
    cornerRadiusRatio: Float = 0.28f // Rounded corners
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val width = this.size.width
            val height = this.size.height
            val strokeWidth = (width * 0.085f).coerceAtLeast(1.5f)
            val cornerRadius = CornerRadius(width * cornerRadiusRatio, height * cornerRadiusRatio)

            // 1. Fill Deep Blue Rounded Box
            drawRoundRect(
                color = backgroundColor,
                topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                size = Size(width - strokeWidth, height - strokeWidth),
                cornerRadius = cornerRadius
            )

            // 2. Golden Yellow Outer Border
            drawRoundRect(
                color = borderColor,
                topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                size = Size(width - strokeWidth, height - strokeWidth),
                cornerRadius = cornerRadius,
                style = Stroke(width = strokeWidth)
            )

            // 3. Centered Pure White Play Triangle
            val centerX = width / 2f
            val centerY = height / 2f
            val triSize = width * 0.36f

            val triPath = Path().apply {
                val left = centerX - triSize * 0.40f
                val right = centerX + triSize * 0.52f
                val top = centerY - triSize * 0.48f
                val bottom = centerY + triSize * 0.48f
                moveTo(left, top)
                lineTo(right, centerY)
                lineTo(left, bottom)
                close()
            }

            drawPath(
                path = triPath,
                color = iconColor
            )
        }
    }
}
