package com.example.navigation_test

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import com.arkivanov.decompose.Child
import com.example.navigation_test.ThemeTemplate.ThemeValue
import com.example.navigation_test.decompose.PrimaryScreen
import com.example.navigation_test.decompose.RootComponent

internal const val TOP_BAR_HEIGHT_DP = 50
internal const val BOTTOM_BAR_HEIGHT_DP = 50
internal const val PANELS_OFFSET_DP = 16

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        var darkScheme by rememberSaveable { mutableStateOf<Boolean?>(value = null) }

        val dark = isSystemInDarkTheme()

        val themeTemplate = remember(key1 = dark, key2 = darkScheme) {
            ThemeTemplateImpl(
                themeFlag = when (darkScheme) {
                    true -> ThemeValue.Dark
                    false -> ThemeValue.Light
                    null -> ThemeValue.AsSystem
                },
                changeThemeValue = {
                    darkScheme = when (it) {
                        ThemeValue.AsSystem -> null
                        ThemeValue.Dark -> true
                        ThemeValue.Light -> false
                    }
                }
            ) { dark }
        }

        val density = LocalDensity.current
        val windowInfo = LocalWindowInfo.current

        val screenSize = windowInfo
            .containerSize
            .run {
                density.run {
                    DpSize(width = width.toDp(), height = height.toDp())
                }
            }

        CompositionLocalProvider(
            LocalScreenSize provides screenSize,
            LocalThemeTemplate provides themeTemplate
        ) {
            content()
        }
    }
}


// --- Основное приложение ---
@Composable
fun AppContainer(content: @Composable () -> Unit) {
    val themeTemplate = LocalThemeTemplate.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .run {
                if (!themeTemplate.isDark) {
                    background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(color = 0xFFBDCAD0), Color(color = 0xFFA4BBCA)),
                        )
                    )
                } else {
                    background(
                        color = Color(color = 0xFF050C19)
                    ).background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(color = 0xFF607481), Color(color = 0x00050C19))
                        )
                    )
                }
            }
    ) {
        if (!themeTemplate.isDark) {
            Image(
                painter = painterResource(id = R.drawable.radial_background),
                contentDescription = "Background radial effect",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }
        content()
    }
}

@Composable
fun AppContent(
    component: RootComponent,
    child: Child.Created<Any, PrimaryScreen>
) {
    when (val child = child.instance) {
        PrimaryScreen.Auth -> AuthScreenScaffold {
            component.navigateTabs()
        }

        PrimaryScreen.Settings -> SettingsScreenScaffold {
            component.navigateBack()
        }

        is PrimaryScreen.TabScreen -> MainScreenScaffold(component = child.component)
    }
}
