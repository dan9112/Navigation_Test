package com.example.navigation_test

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp

@Composable
fun SettingsTopBar(onBack: () -> Unit) {
    SettingsTopBarCommon(
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
        onBack = onBack
    )
}

@Composable
fun SettingsContent(contentPaddings: PaddingValues) {
    SettingsContentCommon(
        modifier = Modifier.fillMaxSize(),
        contentPaddings = contentPaddings
    )
}
