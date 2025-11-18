package com.example.navigation_test

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt


// --- Модификатор для рисования кусочка фона ---
@Composable
internal fun Modifier.drawBackgroundSlice(offsetY: Float = 0f): Modifier {
    val screenSize = LocalScreenSize.current
    val density = LocalDensity.current
    val screenSizePx = density.run { screenSize.toSize() }
    val imageBitmap = ImageBitmap.imageResource(id = R.drawable.radial_background)

    val themeTemplate = LocalThemeTemplate.current

    return drawBehind {
        if (!themeTemplate.isDark) {
            // Линейный градиент на весь экран
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFbdcad0), Color(0xFFA4BBCA)),
                    startY = -offsetY,
                    endY = screenSizePx.height - offsetY
                ),
                size = screenSizePx
            )
            // Радиальный градиент поверх
            drawImage(
                image = imageBitmap,
                dstOffset = IntOffset(0, -offsetY.roundToInt()),
                dstSize = IntSize(screenSizePx.width.roundToInt(), screenSizePx.height.roundToInt())
            )
        } else {
            drawRect(color = Color(color = 0xFF050C19))
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(color = 0xFF607481), Color(color = 0x00050C19)),
                    startY = -offsetY,
                    endY = screenSizePx.height - offsetY
                ),
                size = screenSizePx
            )
        }
    }
}
