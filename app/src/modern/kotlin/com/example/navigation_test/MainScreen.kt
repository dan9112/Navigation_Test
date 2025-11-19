package com.example.navigation_test

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.example.navigation_test.decompose.SecondaryScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBarContent(label: String, onSettings: () -> Unit, onLogout: () -> Unit) {
    MainTopBarContentCommon(
        modifier = Modifier
            .clip(
                shape = RoundedCornerShape(
                    bottomStart = PANELS_OFFSET_DP.dp,
                    bottomEnd = PANELS_OFFSET_DP.dp
                )
            )
            .drawBackgroundSlice()
            .statusBarsPadding()
            .height(TOP_BAR_HEIGHT_DP.dp)
            .clipToBounds(),
        label = label,
        onSettings = onSettings,
        onLogout = onLogout
    )
}

@Composable
fun TabContainerContent(
    currentTab: SecondaryScreen,
    contentPaddings: PaddingValues,
    showPanels: (Boolean) -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current
    TabContainerContentCommon(
        modifier = Modifier
            .fillMaxSize()
            .drawBackgroundSlice(),
        currentTab = currentTab,
        contentPadding = PaddingValues(
            start = contentPaddings.calculateStartPadding(layoutDirection),
            top = contentPaddings.calculateTopPadding() - PANELS_OFFSET_DP.dp,
            end = contentPaddings.calculateEndPadding(layoutDirection),
            bottom = contentPaddings.calculateBottomPadding() - PANELS_OFFSET_DP.dp
        ),
        showPanels = showPanels
    )
}
