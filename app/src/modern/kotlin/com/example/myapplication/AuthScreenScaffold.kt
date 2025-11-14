package com.example.myapplication

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AuthScreenScaffold(onLogin: () -> Unit) {
    Scaffold { contentPaddings ->
        AuthScreenContent(
            modifier = Modifier
                .fillMaxSize()
                .drawBackgroundSlice()
                .padding(paddingValues = contentPaddings),
            onLogin = onLogin
        )
    }
}
