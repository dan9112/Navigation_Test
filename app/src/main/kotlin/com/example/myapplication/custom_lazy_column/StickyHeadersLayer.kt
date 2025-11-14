package com.example.myapplication.custom_lazy_column

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun StickyHeadersLayer(
    listState: LazyListState,
    items: List<StickyLazyItem>,
    headerRanges: List<HeaderRange>,
    topPadding: Dp,
    contentPadding: PaddingValues
) {
    val layoutInfo = listState.layoutInfo
    val visibleItemsInfo = layoutInfo.visibleItemsInfo

    if (visibleItemsInfo.isEmpty()) return

    val localDensity = LocalDensity.current

    val contentTopPadding = localDensity.run {
        contentPadding
            .calculateTopPadding()
            .roundToPx()
    }
    val contentBottomPadding = localDensity.run {
        contentPadding
            .calculateBottomPadding()
            .roundToPx()
    }
    val stickyTopPx = localDensity.run { topPadding.roundToPx() }

    // Вычисляем позиции для всех активных заголовков
    val headerPositions = remember(visibleItemsInfo, headerRanges) {
        calculateHeaderPositions(
            visibleItemsInfo = visibleItemsInfo,
            headerRanges = headerRanges,
            viewportStart = layoutInfo.viewportStartOffset + contentTopPadding,
            viewportEnd = layoutInfo.viewportEndOffset - contentBottomPadding,
            stickyTop = stickyTopPx,
            allItems = items
        )
    }

    // Отрисовываем заголовки на вычисленных позициях
    headerPositions.forEach { (headerRange, yPosition) ->
        // Защита от некорректных позиций
        if (yPosition >= 0f && yPosition < layoutInfo.viewportEndOffset) {
            Box(
                modifier = Modifier
                    .offset(y = yPosition.dp)
                    .fillMaxWidth()
            ) {
                key(headerRange.key) {
                    headerRange.content()
                }
            }
        }
    }
}

private fun calculateHeaderPositions(
    visibleItemsInfo: List<LazyListItemInfo>,
    headerRanges: List<HeaderRange>,
    viewportStart: Int,
    viewportEnd: Int,
    stickyTop: Int,
    allItems: List<StickyLazyItem>
): Map<HeaderRange, Float> {
    val result = mutableMapOf<HeaderRange, Float>()
    val targetStickyPosition = viewportStart + stickyTop

    headerRanges.forEach { headerRange ->
        val headerItems = visibleItemsInfo.filter {
            it.index in headerRange.range
        }

        if (headerItems.isNotEmpty()) {
            val firstVisibleItem = headerItems.minByOrNull { it.offset }!!
            val lastVisibleItem = headerItems.maxByOrNull { it.offset + it.size }!!

            val yPosition = when {
                // Заголовок в начале своей секции - прилипаем к верху
                firstVisibleItem.index == headerRange.range.first &&
                        firstVisibleItem.offset >= targetStickyPosition -> {
                    stickyTop.toFloat()
                }

                // Секция заканчивается - заголовок прилипает к верху
                lastVisibleItem.index == headerRange.range.last &&
                        (lastVisibleItem.offset + lastVisibleItem.size) <= targetStickyPosition -> {
                    stickyTop.toFloat()
                }

                // Заголовок должен скроллиться внутри своей секции
                else -> {
                    val scrollingPosition = calculateScrollingPosition(
                        firstVisibleItem = firstVisibleItem,
                        lastVisibleItem = lastVisibleItem,
                        headerRange = headerRange,
                        targetStickyPosition = targetStickyPosition,
                        viewportEnd = viewportEnd,
                        allItems = allItems,
                        stickyTop = stickyTop
                    )

                    // Дополнительная проверка на валидность позиции
                    if (scrollingPosition.isNaN() || scrollingPosition.isInfinite()) {
                        stickyTop.toFloat()
                    } else {
                        scrollingPosition
                    }
                }
            }

            result[headerRange] = yPosition
        }
    }

    return result
}

private fun calculateScrollingPosition(
    firstVisibleItem: LazyListItemInfo,
    lastVisibleItem: LazyListItemInfo,
    headerRange: HeaderRange,
    targetStickyPosition: Int,
    viewportEnd: Int,
    allItems: List<StickyLazyItem>,
    stickyTop: Int
): Float {
    // Базовое положение - следуем за скроллом
    val rawPosition = (targetStickyPosition - firstVisibleItem.offset).toFloat()

    // Ограничиваем положение, чтобы заголовок не выходил за пределы секции
    val maxPosition = findMaxHeaderPosition(
        headerRange = headerRange,
        lastVisibleItem = lastVisibleItem,
        viewportEnd = viewportEnd,
        allItems = allItems,
        stickyTop = stickyTop
    )

    // Корректное ограничение диапазона
    return when {
        rawPosition < stickyTop -> stickyTop.toFloat()
        rawPosition > maxPosition -> maxPosition
        else -> rawPosition
    }
}

private fun findMaxHeaderPosition(
    headerRange: HeaderRange,
    lastVisibleItem: LazyListItemInfo,
    viewportEnd: Int,
    allItems: List<StickyLazyItem>,
    stickyTop: Int
): Float {
    // Ищем следующий заголовок
    for (i in headerRange.range.last + 1 until allItems.size) {
        val item = allItems[i]
        if (item.headerKey != null) {
            // Когда следующий заголовок приближается, ограничиваем положение текущего
            val nextHeaderApproachThreshold = viewportEnd - 150

            // Вычисляем максимальную позицию и гарантируем, что она не меньше stickyTop
            val calculatedMax = (nextHeaderApproachThreshold - lastVisibleItem.offset).toFloat()
            return calculatedMax.coerceAtLeast(stickyTop.toFloat())
        }
    }
    return Float.MAX_VALUE
}