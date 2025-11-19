package com.example.navigation_test

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.example.navigation_test.custom.Main
import com.example.navigation_test.decompose.MainComponent
import com.example.navigation_test.decompose.MainComponentImpl
import com.example.navigation_test.decompose.SecondaryScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBarContentCommon(
    modifier: Modifier = Modifier,
    label: String,
    onSettings: () -> Unit,
    onLogout: () -> Unit
) {
    TopAppBar(
        title = { Text(text = label) },
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        actions = {
            IconButton(onClick = onSettings) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings navigation"
                )
            }
            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Log out"
                )
            }
        }
    )
}

@Composable
fun MainBottomBarContentCommon(
    modifier: Modifier = Modifier,
    currentTab: SecondaryScreen,
    onTabChange: (SecondaryScreen) -> Unit
) {
    BottomAppBar(
        modifier = modifier.clipToBounds(),
        containerColor = Color(color = 0xFF171C26)
    ) {
        TabButton(
            label = SecondaryScreen.Tab1.name,
            selected = currentTab == SecondaryScreen.Tab1,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
        ) { onTabChange(SecondaryScreen.Tab1) }
        TabButton(
            label = SecondaryScreen.Tab2.name,
            selected = currentTab == SecondaryScreen.Tab2,
            selectedIcon = Icons.Filled.Favorite,
            unselectedIcon = Icons.Outlined.Favorite,
        ) { onTabChange(SecondaryScreen.Tab2) }
        TabButton(
            label = SecondaryScreen.Tab3.name,
            selected = currentTab == SecondaryScreen.Tab3,
            selectedIcon = Icons.AutoMirrored.Filled.List,
            unselectedIcon = Icons.AutoMirrored.Outlined.List,
        ) { onTabChange(SecondaryScreen.Tab3) }
    }
}


@OptIn(ExperimentalAnimationApi::class, ExperimentalDecomposeApi::class)
@Composable
fun MainScreenScaffold(component: MainComponent) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showPanels by rememberSaveable { mutableStateOf(value = true) }

    val stack = component.stack.subscribeAsState()
    val active by remember {
        derivedStateOf {
            stack.value.active.instance
        }
    }
    val socketState by component.socketState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = active is SecondaryScreen.WebSocketTab, key2 = socketState.second) {
        // Если перешли на другую вкладку - скрываем активный snackbar
        if (active !is SecondaryScreen.WebSocketTab) {
            snackbarHostState.currentSnackbarData?.dismiss()
            return@LaunchedEffect
        }

        // Если мы на вкладке WebSocket
        val currentStatus = socketState.second
        val isUnread = !socketState.first

        // Показываем snackbar только для непрочитанных статусов
        if (isUnread) {
            launch {
                // Ждем минимальное время показа
                delay(timeMillis = 1_200)

                // Помечаем статус как прочитанный, только если он все еще актуален
                component.invertFlag(state = currentStatus)
            }

            // Сначала скрываем предыдущий snackbar (если есть)
            snackbarHostState.currentSnackbarData?.dismiss()

            // Показываем новый snackbar
            snackbarHostState.showSnackbar(
                message = "Status $currentStatus",
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .heightIn(min = PANELS_OFFSET_DP.dp)
                    .fillMaxWidth()
            ) {
                AnimatedVisibility(visible = showPanels) {
                    MainTopBarContent(
                        label = active.toString(),
                        onSettings = component::navigateSettings,
                        onLogout = component::navigateAuth
                    )
                }
            }
        },
        bottomBar = {
            Box(Modifier.heightIn(min = PANELS_OFFSET_DP.dp)) {
                val startWeight by animateFloatAsState(
                    targetValue = active.position.toFloat(),
                    animationSpec = tween(durationMillis = 800)
                )
                AnimatedVisibility(visible = showPanels) {
                    MainBottomBarContentCommon(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                shape = RoundedCornerShape(
                                    topStart = PANELS_OFFSET_DP.dp,
                                    topEnd = PANELS_OFFSET_DP.dp
                                )
                            ),
                        currentTab = active,
                        onTabChange = {
                            when (it) {
                                SecondaryScreen.Tab1 -> component.navigateTab1()
                                SecondaryScreen.Tab2 -> component.navigateTab2()
                                SecondaryScreen.Tab3 -> component.navigateTab3()
                            }

                        }
                    )
                    AnimatedTabIndicator3(startWeight = startWeight, totalWeight = 3f)
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { contentPaddings ->
        Main(component = component) { actualScreen ->
            TabContainerContent(
                currentTab = actualScreen,
                contentPaddings = contentPaddings
            ) { showPanels = it }
        }
    }
}


@Composable
inline fun TabContainerContentCommon(
    modifier: Modifier = Modifier,
    currentTab: SecondaryScreen,
    contentPadding: PaddingValues,
    crossinline showPanels: (Boolean) -> Unit
) {
    Box(modifier = modifier) {
        when (currentTab) {
            SecondaryScreen.Tab1 -> {
                val layoutDirection = LocalLayoutDirection.current
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(
                            top = contentPadding.calculateTopPadding(),
                            bottom = contentPadding.calculateBottomPadding()
                        ),
                        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Indicators imitation",
                            modifier = Modifier.padding(
                                start = contentPadding.calculateStartPadding(layoutDirection),
                                end = contentPadding.calculateEndPadding(layoutDirection)
                            )
                        )
                        LazyRow(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .height(16.dp + 45.dp)
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(
                                start = contentPadding.calculateStartPadding(layoutDirection) + 8.dp,
                                top = 0.dp,
                                end = contentPadding.calculateEndPadding(layoutDirection) + 8.dp,
                                bottom = 0.dp,
                            ),
                            horizontalArrangement = Arrangement.spacedBy(space = 4.dp)
                        ) {
                            items(
                                count = 20,
                                key = { it }) {
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(shape = RoundedCornerShape(size = 8.dp))
                                        .background(color = Color.Blue)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = contentPadding.calculateStartPadding(layoutDirection) + 8.dp,
                                    end = contentPadding.calculateEndPadding(layoutDirection) + 8.dp
                                ),
                            horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
                        ) {
                            Button(
                                onClick = {},
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(size = 8.dp)
                            ) {
                                Text(text = "Btn 1")
                            }
                            Button(
                                onClick = {},
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(size = 8.dp)
                            ) {
                                Text(text = "Btn 2")
                            }
                            Button(
                                onClick = {},
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(size = 8.dp)
                            ) {
                                Text(text = "Btn 3")
                            }
                        }
                    }
                }
            }

            SecondaryScreen.Tab2, SecondaryScreen.Tab3 -> {
                TabContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = contentPadding)
                ) { showPanels(it) }
            }
        }
    }
}

// --- Контент вкладок ---
@Composable
fun TabContent(modifier: Modifier = Modifier, showPanels: (Boolean) -> Unit) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.spacedBy(space = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { showPanels(false) }) { Text(text = "Hide Panels") }
            Button(onClick = { showPanels(true) }) { Text(text = "Show Panels") }
        }
    }
}

// --- Кнопки вкладок ---
@Composable
private inline fun RowScope.TabButton(
    label: String,
    selected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    crossinline onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = { onClick() },
        icon = {
            Icon(
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = "Nav bar icon"
            )
        },
        modifier = Modifier.weight(1f),
        label = { Text(text = label) }
    )
}

// --- Индикатор для 3 вкладок ---
@Composable
private fun AnimatedTabIndicator3(
    startWeight: Float,
    totalWeight: Float,
    indicatorWeight: Float = 1f
) {
    val layoutDirection = LocalLayoutDirection.current
    val contentPadding = BottomAppBarDefaults.ContentPadding
    Row(
        Modifier
            .fillMaxWidth()
            .height(4.dp)
            .padding(
                start = contentPadding.calculateStartPadding(layoutDirection),
                end = contentPadding.calculateEndPadding(layoutDirection)
            )
    ) {
        if (startWeight > 0f) Spacer(modifier = Modifier.weight(startWeight))
        Box(
            Modifier
                .fillMaxHeight()
                .weight(indicatorWeight)
                .background(
                    color = Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(2.dp)
                )
        )
        if (startWeight < totalWeight - indicatorWeight) Spacer(
            modifier = Modifier.weight(
                totalWeight - indicatorWeight - startWeight
            )
        )
    }
}

class SecondaryScreenParameterProvider : PreviewParameterProvider<SecondaryScreen> {
    override val values = sequenceOf(
        SecondaryScreen.Tab1, SecondaryScreen.Tab2, SecondaryScreen.Tab3
    )
}

@PreviewFontScale
@PreviewLightDark
@PreviewDynamicColors
@PreviewScreenSizes
@Preview(
    name = "Medium phone tall cutout",
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,cutout=tall"
)
@Preview(
    name = "Medium phone punch hole cutout",
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,cutout=punch_hole"
)
@Preview(
    name = "Medium phone corner cutout",
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,cutout=corner"
)
@Preview(
    name = "Medium phone double cutout",
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,cutout=double"
)
@Composable
private fun PreviewMain(
    @PreviewParameter(provider = SecondaryScreenParameterProvider::class) screen: SecondaryScreen
) {
    val componentContext = DefaultComponentContext(
        lifecycle = LifecycleRegistry()
    )
    AppTheme {
        MainScreenScaffold(
            component = MainComponentImpl(
                componentContext = componentContext,
                startScreen = screen,
                mainContext = Dispatchers.Default,
                logOut = {},
                toSettings = {}
            )
        )
    }
}
