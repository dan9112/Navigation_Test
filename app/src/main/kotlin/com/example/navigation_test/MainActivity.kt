package com.example.navigation_test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.defaultComponentContext
import com.example.navigation_test.custom.App
import com.example.navigation_test.custom_lazy_column.StickyHeaderLazyColumn
import com.example.navigation_test.custom_lazy_column.StickyLazyListScope
import com.example.navigation_test.decompose.RootComponentImpl

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val root = RootComponentImpl(componentContext = defaultComponentContext())

        setContent {
            AppTheme {
                App(component = root)
//            navigation_testTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//
//                    var items by rememberSaveable { mutableStateOf(value = List(PAGE_SIZE) { it }) }
//                    var entireList by rememberSaveable { mutableStateOf(value = false) }
//                    var loadingState by rememberSaveable { mutableStateOf(value = false) }
//                    val coroutineScope = rememberCoroutineScope()
//
//                    Box(modifier = Modifier.padding(paddingValues = innerPadding)) {
//                        LocalTextStyle.current.color
//                        PullRefreshPaginatedList(
//                            entireList = entireList,
//                            loadingState = loadingState,
//                            onRefresh = {
//                                loadingState = true
//                                coroutineScope.launch {
//                                    delay(timeMillis = 1_500)
//                                    items = mutableListOf<Int>().apply {
//                                        val size = Random.nextInt(from = 1, until = PAGE_SIZE + 1)
//                                        entireList =
//                                            if (size < PAGE_SIZE) true else Random.nextBoolean()
//                                        repeat(times = size) {
//                                            add(Random.nextInt())
//                                        }
//                                        sort()
//                                    }
//                                    loadingState = false
//                                }
//                            },
//                            onLoadMore = {
//                                loadingState = true
//                                coroutineScope.launch {
//                                    delay(timeMillis = 1_500)
//                                    items += mutableListOf<Int>().apply {
//                                        val size = Random.nextInt(from = 1, until = PAGE_SIZE + 1)
//                                        entireList =
//                                            if (size < PAGE_SIZE) true else Random.nextBoolean()
//                                        repeat(times = size) {
//                                            add(Random.nextInt())
//                                        }
//                                        sort()
//                                    }
//                                    loadingState = false
//                                }
//                            },
//                            colorStart = Color.Gray,
//                            colorEnd = Color.White
//                        ) {
//
//                            repeat(4) {
//                                item {
//                                    Text(
//                                        text = it.toString(),
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .padding(16.dp),
//                                        color = Color.White
//                                    )
//                                }
//                            }
//
//                            items
//                                .groupBy { it % 2 == 0 }
//                                .onEachIndexed { index, (quality, values) ->
//                                    if (index != 0) item {
//                                        Spacer(modifier = Modifier.height(8.dp))
//                                    }
//                                    stickyHeader {
//                                        Spacer(modifier = Modifier.height(4.dp))
//                                        Text(
//                                            text = when (quality) {
//                                                true -> "Good"
//                                                false -> "Bad"
//                                            },
//                                            modifier = Modifier
//                                                .fillMaxWidth()
//                                                .background(
//                                                    color = Color.Gray.copy(alpha = 0.5f)
//                                                )
//                                                .padding(all = 16.dp),
//                                            color = Color.Red
//                                        )
//                                        Spacer(modifier = Modifier.height(4.dp))
//                                    }
//                                    items(items = values) { item: Int ->
//                                        Text(
//                                            text = item.toString(),
//                                            modifier = Modifier
//                                                .fillMaxWidth()
//                                                .padding(16.dp),
//                                            color = Color.White
//                                        )
//                                    }
//                                }
//                        }
//                    }
//                }
            }
        }
    }

    private companion object {
        const val PAGE_SIZE = 30
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullRefreshPaginatedList(
    entireList: Boolean,
    loadingState: Boolean,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    colorStart: Color = Color.Gray,
    colorEnd: Color = Color.Green,
    content: StickyLazyListScope.() -> Unit
) {
//    val coroutineScope = rememberCoroutineScope()

    // Измеряем текст индикатора
//    val textMeasurer = rememberTextMeasurer()
//    val textLayout = remember {
//        textMeasurer.measure(
//            text = AnnotatedString("Pull to refresh"),
//            style = TextStyle(fontSize = 16.sp)
//        )
//    }
//    val textHeightPx = textLayout.size.height.toFloat()

    // offset контролирует смещение LazyColumn
//    var rawOffset by rememberSaveable { mutableFloatStateOf(value = 0f) }
//    val offset by animateFloatAsState(
//        targetValue = rawOffset,
//        animationSpec = spring(dampingRatio = 0.6f)
//    )

    // Флаги состояния
//    var refreshTriggered by rememberSaveable { mutableStateOf(value = false) }
//    var offsetHiding by rememberSaveable { mutableStateOf(value = false) }

//    val pullState = remember {
//        object : PullToRefreshState {
//            private var _distanceFraction by mutableFloatStateOf(value = 0f)
//            fun setDistanceFraction(value: Float) {
//                _distanceFraction = value
//            }
//
//            override val distanceFraction: Float get() = _distanceFraction
//            override val isAnimating: Boolean get() = false
//            override suspend fun animateToThreshold() {}
//            override suspend fun animateToHidden() {
//                _distanceFraction = 0f
//            }
//
//            override suspend fun snapTo(targetValue: Float) {
//                _distanceFraction = targetValue
//            }
//        }
//    }

//    val threshold = textHeightPx / 2f

//    val nestedScrollConnection = remember {
//        object : NestedScrollConnection {
//            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
//                val atTop = listState.firstVisibleItemIndex == 0 &&
//                        listState.firstVisibleItemScrollOffset == 0
//                val shouldHandlePull = atTop && available.y > 0f && !loadingState && !offsetHiding
//                return if (shouldHandlePull) {
//                    // тянем индикатор
//                    rawOffset = (rawOffset + available.y / 2f).coerceIn(0f, textHeightPx)
//                    pullState.setDistanceFraction(rawOffset / textHeightPx)
//                    Offset(x = 0f, y = available.y) // блокируем scroll для LazyColumn
//                } else if (available.y < 0f && rawOffset > 0f) {
//                    // можно сдвинуть индикатор обратно
//                    rawOffset = (rawOffset + available.y / 2f).coerceAtLeast(0f)
//                    pullState.setDistanceFraction(rawOffset / textHeightPx)
//                    Offset(x = 0f, y = available.y)
//                } else {
//                    Offset.Zero // scroll LazyColumn работает как обычно
//                }
//            }
//
//            override suspend fun onPreFling(available: Velocity): Velocity {
//                if (rawOffset >= threshold && !refreshTriggered && !loadingState) {
//                    // только если тянули достаточно — триггерим refresh
//                    refreshTriggered = true
//                    offsetHiding = true
//                    coroutineScope.launch {
//                        onRefresh()
//                        // анимация скрытия индикатора
//                        rawOffset = 0f
//                        delay(300) // под animateFloatAsState
//                        offsetHiding = false
//                        refreshTriggered = false
//                    }
//                } else if (rawOffset > 0f) {
//                    // просто отпускаем индикатор без refresh
//                    offsetHiding = true
//                    rawOffset = 0f
//                    delay(300)
//                    offsetHiding = false
//                }
//                return Velocity.Zero
//            }
//        }
//    }

    // Пагинация внизу
//    LaunchedEffect(listState.canScrollForward, loadingState, entireList) {
//        val shouldLoadMore =
//            !listState.canScrollForward && !loadingState && !entireList
//        if (shouldLoadMore) onLoadMore()
//    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
//            .offset { IntOffset(x = 0, y = offset.roundToInt()) }
    ) {
        Box(
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
                .align(alignment = Alignment.TopCenter)
                .background(color = Color.Blue)
        )
        Box(
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
                .align(alignment = Alignment.BottomCenter)
                .background(color = Color.Blue)
        )
        StickyHeaderLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            stickyHeaderTopPadding = 50.dp,
            content = content
        )

        if (loadingState) {
            CircularProgressIndicator(modifier = Modifier.align(alignment = Alignment.Center))
        }
    }
}


@Preview
@Composable
private fun TestMultipleGradient1() {
    Box(
        modifier = Modifier
            .size(200.dp)
            .clip(shape = RoundedCornerShape(size = 15.dp))
            .background(
                brush = Brush.verticalGradient(
                    0f to Color.Black,
                    0.4f to Color.Magenta.copy(alpha = 0.2f),
                    1f to Color.DarkGray,
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .background(
                brush = Brush.radialGradient(
                    0f to Color.Yellow,
                    0.34f to Color.Cyan.copy(alpha = 0.65f),
                    0.65f to Color.Red.copy(alpha = 0.1f),
                )
            )
    )
}


@Preview
@Composable
private fun TestMultipleGradient2() {
    Box(
        modifier = Modifier
            .size(200.dp)
            .clip(shape = RoundedCornerShape(size = 15.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        0f to Color.Black,
                        0.4f to Color.Magenta.copy(alpha = 0.2f),
                        1f to Color.DarkGray,
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        0f to Color.Yellow,
                        0.34f to Color.Cyan.copy(alpha = 0.65f),
                        0.65f to Color.Red.copy(alpha = 0.1f),
                    )
                )
        )
    }
}
