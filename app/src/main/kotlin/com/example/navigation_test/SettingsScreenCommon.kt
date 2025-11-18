package com.example.navigation_test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.example.navigation_test.ThemeTemplate.ThemeValue

@Composable
inline fun SettingsScreenScaffold(crossinline onBack: () -> Unit) {
    Scaffold(
        topBar = { SettingsTopBar { onBack() } },
        containerColor = Color.Transparent,
        content = { SettingsContent(contentPaddings = it) }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
inline fun SettingsTopBarCommon(modifier: Modifier, crossinline onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        TopAppBar(
            title = { Text("Settings") },
            modifier = modifier,
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            navigationIcon = {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back navigation"
                    )
                }
            }
        )
    }
}

@Composable
fun SettingsContentCommon(modifier: Modifier, contentPaddings: PaddingValues) {
    val layoutDirection = LocalLayoutDirection.current
    val themeTemplate = LocalThemeTemplate.current
    Box(
        modifier = modifier.padding(
            start = contentPaddings.calculateStartPadding(layoutDirection),
            top = (contentPaddings.calculateTopPadding() - PANELS_OFFSET_DP.dp).coerceAtLeast(
                minimumValue = 0.dp
            ),
            end = contentPaddings.calculateEndPadding(layoutDirection),
            bottom = contentPaddings.calculateBottomPadding()
        ),
        contentAlignment = Alignment.Center
    ) {
        @Composable
        fun Variant(themeValue: ThemeValue, label: String) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(size = 8.dp))
                    .selectable(
                        selected = themeTemplate.themeFlag == themeValue,
                        onClick = { themeTemplate.changeThemeValue(newValue = themeValue) }
                    )
                    .padding(all = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = themeTemplate.themeFlag == themeValue,
                    onClick = null
                )
                Text(text = label)
            }
        }

        Column(
            Modifier
                .width(IntrinsicSize.Max)
                .selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            Variant(themeValue = ThemeValue.AsSystem, label = "As system")
            Variant(themeValue = ThemeValue.Light, label = "Light")
            Variant(themeValue = ThemeValue.Dark, label = "Dark")
        }
    }
}


@PreviewFontScale
@PreviewLightDark
@PreviewDynamicColors
@PreviewScreenSizes
@Preview(
    name = "Medium phone tall cutout",
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp"
)
@Preview(
    name = "Medium phone punch hole cutout",
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp"
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
private fun PreviewSettings() {
    AppTheme {
        @Suppress("NewApi") SettingsScreenScaffold {}
    }
}
