package com.example.navigation_test.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

interface MainComponentCommon {
    val stack: Value<ChildStack<*, SecondaryScreen>>
    val socketState: StateFlow<Pair<Boolean, Int>>

    fun invertFlag(state: Int)

    fun navigateTab1()
    fun navigateTab2()
    fun navigateTab3()
    fun navigateSettings()
    fun navigateAuth()
}

abstract class MainComponentCommonImpl(
    private val componentContext: ComponentContext,
    startScreen: SecondaryScreen = SecondaryScreen.Tab1,
    mainContext: CoroutineContext = Dispatchers.Main,
    private val logOut: () -> Unit,
    private val toSettings: () -> Unit
) : MainComponentCommon, ComponentContext by componentContext {
    protected val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, SecondaryScreen>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(), // Or null to disable navigation state saving
            initialConfiguration = when (startScreen) {
                SecondaryScreen.Tab1 -> Config.Tab1
                SecondaryScreen.Tab2 -> Config.Tab2
                SecondaryScreen.Tab3 -> Config.Tab3
            },
            handleBackButton = true, // Pop the back stack on back button press
            childFactory = ::createChild,
        )

    private val _socketState = MutableStateFlow<Pair<Boolean, Int>>(value = false to -1)
    override val socketState = _socketState.asStateFlow()

    val scope = coroutineScope(context = mainContext + SupervisorJob())

    init {
        scope.launch {
            while (true) {
                delay(timeMillis = Random.nextLong(from = 3_300, until = 5_200))
                _socketState.value = false to Random.nextInt()
            }
        }
    }

    override fun invertFlag(state: Int) = _socketState.update { current ->
        val (read, value) = current
        if (value == state && !read) true to state else current
    }

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ) = when (config) {
        Config.Tab1 -> SecondaryScreen.Tab1
        Config.Tab2 -> SecondaryScreen.Tab2
        Config.Tab3 -> SecondaryScreen.Tab3
    }

    override fun navigateTab1() = navigateTab(tab = Config.Tab1)
    override fun navigateTab2() = navigateTab(tab = Config.Tab2)
    override fun navigateTab3() = navigateTab(tab = Config.Tab3)

    private fun navigateTab(tab: Config) = navigation.navigate { stack ->
        stack.filterNot { it == tab } + tab
    }

    override fun navigateAuth() = logOut()
    override fun navigateSettings() = toSettings()

    @Serializable
    protected sealed interface Config {
        @Serializable
        data object Tab1 : Config

        @Serializable
        data object Tab2 : Config

        @Serializable
        data object Tab3 : Config
    }
}
