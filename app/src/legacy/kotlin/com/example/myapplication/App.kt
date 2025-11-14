package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun App(modifier: Modifier = Modifier) {
    AppCommon(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(color = 0xFF89CFF0), Color(color = 0xFFB19CD9)),
                    )
                )
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(color = 0x33FF0000), Color(color = 0x3300FF00)),
                    )
                )
        ) { it() }
    }
}
