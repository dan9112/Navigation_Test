package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun App(modifier: Modifier = Modifier) = AppCommon(modifier = modifier) { it() }
