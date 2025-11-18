package com.example.navigation_test

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AuthScreenScaffold(onLogin: () -> Unit) {
    Scaffold(containerColor = Color.Transparent) { contentPaddings ->
        AuthScreenContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = contentPaddings),
            onLogin = onLogin
        )
    }
}
