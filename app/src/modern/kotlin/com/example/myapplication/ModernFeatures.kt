package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
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
    val imageBitmap =
        ImageBitmap.imageResource(id = R.drawable.netflix_daredevil_opening_shot_wallpaper)

    return drawBehind {

        val imageRatio = imageBitmap.width.toFloat() / imageBitmap.height
        val screenRatio = screenSizePx.width / screenSizePx.height

        val (srcWidth, srcHeight) = if (imageRatio > screenRatio) {
            // Изображение шире - кроп по бокам
            val srcHeight = imageBitmap.height
            val srcWidth = (imageBitmap.height * screenRatio).roundToInt()
            srcWidth to srcHeight
        } else {
            // Изображение выше - кроп сверху и снизу
            val srcWidth = imageBitmap.width
            val srcHeight = (imageBitmap.width / screenRatio).roundToInt()
            srcWidth to srcHeight
        }

        val srcOffsetX = (imageBitmap.width - srcWidth) / 2
        val srcOffsetY = (imageBitmap.height - srcHeight) / 2

        drawImage(
            image = imageBitmap,
            srcOffset = IntOffset(x = srcOffsetX, y = srcOffsetY - offsetY.roundToInt()),
            srcSize = IntSize(srcWidth, srcHeight),
            dstSize = IntSize(
                width = screenSizePx.width.roundToInt(),
                height = screenSizePx.height.roundToInt()
            )
        )
    }
}
