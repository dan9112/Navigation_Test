package com.example.navigation_test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp

@Composable
inline fun AuthScreenContent(modifier: Modifier, crossinline onLogin: () -> Unit) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Authorization", fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { onLogin() }) { Text("Login") }
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
private fun PreviewAuth() {
    AppTheme {
        @Suppress("NewApi") AuthScreenScaffold {}
    }
}
