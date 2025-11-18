package com.example.navigation_test

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.core.ScrollArea
import com.composables.core.Thumb
import com.composables.core.ThumbVisibility
import com.composables.core.VerticalScrollbar
import com.composables.core.rememberScrollAreaState
import com.composeunstyled.Text
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun <T> ListWithShadows(
    modifier: Modifier = Modifier,
    items: List<T>,
    shadowColor: Color,
    contentPadding: PaddingValues = PaddingValues(),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    overscrollEffect: OverscrollEffect? = rememberOverscrollEffect(),
    itemContent: @Composable LazyItemScope.(T) -> Unit
) = BoxWithConstraints(modifier) {
    val lazyListState = rememberLazyListState()
    val scrollAreaState = rememberScrollAreaState(lazyListState)

    ScrollArea(state = scrollAreaState, modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
            /*.padding(end = 8.dp)*/,
            state = lazyListState,
            contentPadding = contentPadding,
            reverseLayout = reverseLayout,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            flingBehavior = flingBehavior,
            userScrollEnabled = userScrollEnabled,
            overscrollEffect = overscrollEffect
        ) {
            items(items = items, itemContent = itemContent)
        }

        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(8.dp)
                .fillMaxHeight(),
        ) {
            Thumb(
                modifier = Modifier
                    .padding(all = 2.dp)
                    .height(12.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.33f),
                        shape = RoundedCornerShape(percent = 100)
                    ),
                thumbVisibility = ThumbVisibility.HideWhileIdle(
                    enter = fadeIn(),
                    exit = fadeOut(),
                    hideDelay = 800.milliseconds
                )
            )
        }
    }

    var topOffsetPx by remember { mutableFloatStateOf(0f) }
    var bottomOffsetPx by remember { mutableFloatStateOf(0f) }

    val density = LocalDensity.current
    val topPaddingPx = with(density) { contentPadding.calculateTopPadding().toPx() }
    val bottomPaddingPx = with(density) { contentPadding.calculateBottomPadding().toPx() }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo }
            .collect { layoutInfo ->
                if (layoutInfo.totalItemsCount == 0) {
                    topOffsetPx = 0f
                    bottomOffsetPx = 0f
                    return@collect
                }

                val visible = layoutInfo.visibleItemsInfo
                if (visible.isEmpty()) {
                    topOffsetPx = 0f
                    bottomOffsetPx = 0f
                    return@collect
                }

                val firstIndex = lazyListState.firstVisibleItemIndex
                val firstScrollOffset = lazyListState.firstVisibleItemScrollOffset.toFloat()

                val avgItemSizePx = visible.map { it.size }.average().toFloat()
                val absoluteScrollFromStart = firstIndex * avgItemSizePx + firstScrollOffset

                // верхняя тень — после выхода за padding
                topOffsetPx = (absoluteScrollFromStart - topPaddingPx).coerceAtLeast(0f)

                val totalContentHeightPx = avgItemSizePx * layoutInfo.totalItemsCount
                val viewportHeightPx =
                    (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset).toFloat()

                // теперь учитываем нижний padding правильно
                // viewportEndOffset уже включает bottomPaddingPx, поэтому вычитаем его
                val effectiveViewport = viewportHeightPx - bottomPaddingPx
                val visibleEnd = absoluteScrollFromStart + effectiveViewport

                // расстояние от конца видимой области до конца контента
                bottomOffsetPx = (totalContentHeightPx - visibleEnd).coerceAtLeast(0f)
            }
    }

    val shadowHeightPx = with(receiver = LocalDensity.current) { (maxHeight * 0.3f).toPx() }

    // Верхняя тень — проявляется при прокрутке вниз
    Box(
        modifier = Modifier
            .height(maxHeight * 0.3f)
            .fillMaxWidth()
            .align(Alignment.TopCenter)
            .graphicsLayer {
                alpha = (topOffsetPx / shadowHeightPx).coerceIn(0f, 1f)
            }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(shadowColor, Color.Transparent)
                )
            )
    )

    // Нижняя тень — проявляется, если не долистали до конца
    Box(
        modifier = Modifier
            .height(maxHeight * 0.3f)
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .graphicsLayer {
                alpha = (bottomOffsetPx / shadowHeightPx).coerceIn(0f, 1f)
            }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, shadowColor)
                )
            )
    )

    Text(
        text = "Top: $topOffsetPx\nBottom: $bottomOffsetPx",
        modifier = Modifier
            .align(Alignment.Center)
            .background(
                color = Color.Blue.copy(alpha = 0.7f),
                shape = RoundedCornerShape(size = 4.dp)
            )
            .padding(all = 4.dp),
        color = Color.Red
    )
}

@Preview(device = "spec:width=411dp,height=891dp")
@Composable
private fun ListWithShadowsPreview() {
    val localDensity = LocalDensity.current
    ListWithShadows(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(color = 0xFF00BCD4),
                        Color(color = 0xFF2196F3)
                    )
                )
            ),
        items = List(size = 90) { it },
        shadowColor = Color.Black/*.copy(alpha = 0.7f)*/,
        contentPadding = WindowInsets
            .systemBars
            .run {
                localDensity.run {
                    PaddingValues(
                        start = 8.dp,
//                        top = getTop(this).toDp() + 8.dp,
                        end = 8.dp,
//                        bottom = getBottom(this).toDp() + 8.dp
                    )
                }
            }
    ) {
        Text(
            text = if (it%10 ==0) "\n$it\n" else it.toString(),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(color = 0xFFFF9800),
                            Color(color = 0xFFFF5722)
                        )
                    ),
                    shape = RoundedCornerShape(size = 8.dp)
                )
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
    }
}
