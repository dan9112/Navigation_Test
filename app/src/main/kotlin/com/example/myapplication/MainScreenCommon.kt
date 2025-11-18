package com.example.myapplication

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
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
import com.example.myapplication.custom.Main
import com.example.myapplication.decompose.MainComponent
import com.example.myapplication.decompose.MainComponentImpl
import com.example.myapplication.decompose.SecondaryScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBarContentCommon(
    modifier: Modifier = Modifier,
    onSettings: () -> Unit,
    onLogout: () -> Unit
) {
    TopAppBar(
        title = { Text("Main") },
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
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(space = 2.dp)
    ) {
        TabButton(
            label = "Tab1",
            selected = currentTab == SecondaryScreen.Tab1
        ) { onTabChange(SecondaryScreen.Tab1) }
        TabButton(
            label = "Tab2",
            selected = currentTab == SecondaryScreen.Tab2
        ) { onTabChange(SecondaryScreen.Tab2) }
        TabButton(
            label = "Tab3",
            selected = currentTab == SecondaryScreen.Tab3
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
                        onSettings = component::navigateSettings,
                        onLogout = component::navigateAuth
                    )
                }
            }
        },
        bottomBar = {
            Box(
                Modifier
                    .heightIn(min = PANELS_OFFSET_DP.dp)
                    .clip(
                        shape = RoundedCornerShape(
                            topStart = PANELS_OFFSET_DP.dp,
                            topEnd = PANELS_OFFSET_DP.dp
                        )
                    )
            ) {
                val startWeight by animateFloatAsState(
                    targetValue = active.position.toFloat(),
                    animationSpec = tween(durationMillis = 800)
                )
                AnimatedVisibility(visible = showPanels) {
                    MainBottomBarContent(
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
        Main(component = component) {
            TabContainerContent(
                currentTab = active,
                contentPaddings = contentPaddings
            ) { showPanels = it }
        }
    }
}

@Composable
inline fun TabContainerContentCommon(
    modifier: Modifier = Modifier,
    currentTab: SecondaryScreen,
    crossinline showPanels: (Boolean) -> Unit
) {
    Box(modifier = modifier) {
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = { fadeIn().togetherWith(fadeOut()) }
        ) { tab ->
            TabContent(name = tab.toString()) { showPanels(it) }
        }
    }
}

// --- Контент вкладок ---
@Composable
fun TabContent(name: String, showPanels: (Boolean) -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = name, color = Color.White)
            Spacer(Modifier.height(8.dp))
            Button(onClick = { showPanels(false) }) { Text("Hide Panels") }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { showPanels(true) }) { Text("Show Panels") }
        }
    }
}

// --- Кнопки вкладок ---
@Composable
private fun RowScope.TabButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) { Text(label, color = if (selected) Color.White else Color.LightGray) }
}

// --- Индикатор для 3 вкладок ---
@Composable
private fun AnimatedTabIndicator3(
    startWeight: Float,
    totalWeight: Float,
    indicatorWeight: Float = 1f
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(4.dp)
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
