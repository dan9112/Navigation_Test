package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.decompose.RootComponent

@Composable
fun App(modifier: Modifier = Modifier, component: RootComponent) = AppCommon(modifier = modifier, component = component) { it() }
