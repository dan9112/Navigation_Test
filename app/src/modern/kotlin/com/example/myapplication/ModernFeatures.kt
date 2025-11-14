package com.example.myapplication

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color


// --- Модификатор для рисования кусочка фона ---
internal fun Modifier.drawBackgroundSlice(offsetY: Float = 0f) = composed {
    val screenSize = rememberScreenSizeInPx()

    then(
        Modifier.drawBehind {
            // Линейный градиент на весь экран
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(color = 0xFF89CFF0), Color(color = 0xFFB19CD9)),
                    startY = -offsetY,
                    endY = screenSize.height - offsetY
                ),
                topLeft = Offset.Zero,
                size = screenSize
            )

            // Радиальный градиент поверх
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(color = 0x33FF0000), Color(color = 0x3300FF00)),
                    center = Offset(x = screenSize.width / 2, y = screenSize.height / 2),
//                    radius = screenSize.width / 2
                ),
                topLeft = Offset.Zero,
                size = screenSize
            )
        }
    )
}
