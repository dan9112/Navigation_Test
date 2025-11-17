package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity


// --- Модификатор для рисования кусочка фона ---
@Composable
internal fun Modifier.drawBackgroundSlice(offsetY: Float = 0f): Modifier {
    val screenSize = LocalScreenSize.current
    val density = LocalDensity.current
    val screenSizePx = density.run {
        screenSize.toSize()
    }

    return drawBehind {
        // Линейный градиент на весь экран
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(color = 0xFF89CFF0), Color(color = 0xFFB19CD9)),
                startY = -offsetY,
                endY = screenSizePx.height - offsetY
            ),
            topLeft = Offset.Zero,
            size = screenSizePx
        )

        // Радиальный градиент поверх
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color(color = 0x33FF0000), Color(color = 0x3300FF00)),
                center = Offset(x = screenSizePx.width / 2, y = screenSizePx.height / 2),
//                    radius = screenSize.width / 2
            ),
            topLeft = Offset.Zero,
            size = screenSizePx
        )
    }
}
