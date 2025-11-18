package com.example.navigation_test.custom_lazy_column

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StickyHeaderLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    stickyHeaderTopPadding: Dp = 0.dp,
    content: StickyLazyListScope.() -> Unit
) {
    val scope = remember { StickyLazyListScopeImpl().apply { content() } }
    val visibleItemsInfo = state.layoutInfo.visibleItemsInfo
    val headerHeights = remember { mutableMapOf<Any, Int>() }

    Box(modifier) {
        LazyColumn(
            state = state,
            contentPadding = contentPadding,
            modifier = Modifier.matchParentSize()
        ) {
            items(
                count = scope.items.size,
                key = { index -> scope.items[index].key },
                contentType = { index -> scope.items[index].contentType }
            ) { index ->
                scope.items[index].content()
            }
        }

        if (visibleItemsInfo.isNotEmpty()) {
            val density = LocalDensity.current

            val stickyTopPx = density.run { stickyHeaderTopPadding.roundToPx() }

            // ПРАВИЛЬНО вычисляем позицию залипания - это просто отступ от верха экрана
            val targetStickyY = stickyTopPx

            // Находим все сегменты в данных
            val allSegments = findAllSegments(scope.items)

            // Для каждого видимого сегмента рисуем заголовок
            allSegments.forEach { segment ->
                val segmentItems = visibleItemsInfo.filter { it.index in segment.range }
                if (segmentItems.isNotEmpty()) {
                    val headerKey = segment.range.first
                    val headerHeight = headerHeights[headerKey] ?: 60
                    val segmentBounds = calculateSegmentBounds(visibleItemsInfo, segment.range)

                    val headerY = calculateHeaderPosition(
                        targetStickyY = targetStickyY,
                        segment = segment,
                        segmentBounds = segmentBounds,
                        headerHeight = headerHeight,
                        allSegments = allSegments,
                        visibleItems = visibleItemsInfo
                    )

                    Box(
                        modifier = Modifier
                            .offset(y = headerY.dp)
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                headerHeights[headerKey] = coordinates.size.height
                            }
                    ) {
                        segment.headerContent()
                    }
                }
            }
        }
    }
}

private data class Segment(
    val headerContent: @Composable () -> Unit,
    val range: IntRange
)

private data class SegmentBounds(val top: Int, val bottom: Int)

private fun findAllSegments(allItems: List<StickyItem>): List<Segment> {
    val segments = mutableListOf<Segment>()
    var currentHeader: (@Composable () -> Unit)? = null
    var segmentStart = 0

    allItems.forEachIndexed { index, item ->
        if (item.headerContent != currentHeader) {
            // Завершаем предыдущий сегмент
            if (currentHeader != null) {
                segments.add(Segment(currentHeader, segmentStart until index))
            }
            // Начинаем новый сегмент
            currentHeader = item.headerContent
            segmentStart = index
        }
    }

    // Завершаем последний сегмент
    if (currentHeader != null) {
        segments.add(Segment(currentHeader, segmentStart until allItems.size))
    }

    return segments
}

private fun findVisibleSegments(
    visibleItems: List<LazyListItemInfo>,
    allItems: List<StickyItem>
): List<Pair<@Composable () -> Unit, IntRange>> {
    val segments = mutableListOf<Pair<@Composable () -> Unit, IntRange>>()
    var currentHeader: (@Composable () -> Unit)? = null
    var segmentStart = -1

    visibleItems.forEach { visibleItem ->
        val item = allItems.getOrNull(visibleItem.index) ?: return@forEach

        if (item.headerContent != currentHeader) {
            // Завершаем предыдущий сегмент
            if (currentHeader != null && segmentStart != -1) {
                segments.add(currentHeader to (segmentStart until visibleItem.index))
            }
            // Начинаем новый сегмент
            currentHeader = item.headerContent
            segmentStart = visibleItem.index
        }
    }

    // Завершаем последний сегмент
    if (currentHeader != null && segmentStart != -1) {
        val lastIndex = visibleItems.last().index
        segments.add(currentHeader!! to (segmentStart..lastIndex))
    }

    return segments
}

private fun calculateSegmentBounds(
    visibleItems: List<LazyListItemInfo>,
    segmentRange: IntRange
): SegmentBounds {
    val segmentItems = visibleItems.filter { it.index in segmentRange }
    val top = segmentItems.minOfOrNull { it.offset } ?: 0
    val bottom = segmentItems.maxOfOrNull { it.offset + it.size } ?: 0
    return SegmentBounds(top, bottom)
}

private fun calculateHeaderPosition(
    targetStickyY: Int,
    segment: Segment,
    segmentBounds: SegmentBounds,
    headerHeight: Int,
    allSegments: List<Segment>,
    visibleItems: List<LazyListItemInfo>
): Float {
    // Находим следующий сегмент
    val currentSegmentIndex = allSegments.indexOf(segment)
    val nextSegment =
        if (currentSegmentIndex + 1 < allSegments.size) allSegments[currentSegmentIndex + 1] else null

    val nextSegmentTop = nextSegment?.let { nextSeg ->
        visibleItems.find { it.index == nextSeg.range.first }?.offset
    }

    return if (nextSegmentTop != null) {
        // Есть следующий сегмент - логика с выталкиванием
        calculatePositionWithNextSegment(
            targetStickyY = targetStickyY,
            segmentBounds = segmentBounds,
            headerHeight = headerHeight,
            nextSegmentTop = nextSegmentTop
        )
    } else {
        // Нет следующего сегмента - простая логика
        calculateSimplePosition(
            targetStickyY = targetStickyY,
            segmentBounds = segmentBounds,
            headerHeight = headerHeight
        )
    }
}

private fun calculateSimplePosition(
    targetStickyY: Int,
    segmentBounds: SegmentBounds,
    headerHeight: Int
): Float {
    // 1. Если targetY выше сегмента - заголовок вверху сегмента
    if (targetStickyY <= segmentBounds.top) {
        return segmentBounds.top.toFloat()
    }

    // 2. Если targetY минус высота заголовка ниже сегмента - заголовок внизу сегмента
    if (targetStickyY + headerHeight >= segmentBounds.bottom) {
        return (segmentBounds.bottom - headerHeight).toFloat()
    }

    // 3. Иначе - заголовок на позиции targetY
    return targetStickyY.toFloat()
}

private fun calculatePositionWithNextSegment(
    targetStickyY: Int,
    segmentBounds: SegmentBounds,
    headerHeight: Int,
    nextSegmentTop: Int
): Float {
    // Сначала применяем базовую логику позиционирования
    val basePosition = calculateSimplePosition(targetStickyY, segmentBounds, headerHeight)

    // Если базовая позиция задевает следующий заголовок - выталкиваем
    val headerBottomAtBase = basePosition + headerHeight
    if (headerBottomAtBase > nextSegmentTop) {
        return (nextSegmentTop - headerHeight).toFloat()
    }

    return basePosition
}
