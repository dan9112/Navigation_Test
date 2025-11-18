package com.example.navigation_test.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

interface MainComponent : MainComponentCommon, BackComponent<SecondaryScreen>

class MainComponentImpl(
    componentContext: ComponentContext,
    startScreen: SecondaryScreen = SecondaryScreen.Tab1,
    mainContext: CoroutineContext = Dispatchers.Main,
    logOut: () -> Unit,
    toSettings: () -> Unit
) : MainComponentCommonImpl(componentContext, startScreen, mainContext, logOut, toSettings),
    MainComponent {
    override fun navigateBack() = navigation.pop()
}
