package com.example.myapplication

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBarContent(onSettings: () -> Unit, onLogout: () -> Unit) {
    MainTopBarContentCommon(
        modifier = Modifier
            .clip(
                shape = RoundedCornerShape(
                    bottomStart = PANELS_OFFSET_DP.dp,
                    bottomEnd = PANELS_OFFSET_DP.dp
                )
            )
            .statusBarsPadding()
            .height(TOP_BAR_HEIGHT_DP.dp)
            .clipToBounds(),
        onSettings,
        onLogout
    )
}

@Composable
fun MainBottomBarContent(currentTab: Screen.TabScreen, onTabChange: (Screen.TabScreen) -> Unit) {
    MainBottomBarContentCommon(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(BOTTOM_BAR_HEIGHT_DP.dp)
            .clipToBounds()
            .padding(horizontal = 4.dp),
        currentTab,
        onTabChange
    )
}

@Composable
fun TabContainerContent(
    currentTab: Screen.TabScreen,
    contentPaddings: PaddingValues,
    showPanels: (Boolean) -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current
    TabContainerContentCommon(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = contentPaddings.calculateStartPadding(layoutDirection),
                top = contentPaddings.calculateTopPadding() - PANELS_OFFSET_DP.dp,
                end = contentPaddings.calculateEndPadding(layoutDirection),
                bottom = contentPaddings.calculateBottomPadding() - PANELS_OFFSET_DP.dp
            ),
        currentTab,
        showPanels
    )
}
