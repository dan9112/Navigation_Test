package com.example.navigation_test.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

interface RootComponentCommon {
    val stack: Value<ChildStack<*, PrimaryScreen>>

    fun navigateTabs()
    fun navigateSettings()
    fun navigateAuth()
}

abstract class RootComponentCommonImpl(private val componentContext: ComponentContext) :
    RootComponentCommon,
    ComponentContext by componentContext {
    protected val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, PrimaryScreen>> = childStack(
            source = navigation,
            serializer = Config.serializer(), // Or null to disable navigation state saving
            initialConfiguration = Config.Auth,
            handleBackButton = true, // Pop the back stack on back button press
            childFactory = ::createChild,
        )

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): PrimaryScreen = when (config) {
        Config.Auth -> PrimaryScreen.Auth
        Config.Settings -> PrimaryScreen.Settings
        Config.TabConfig -> PrimaryScreen.TabScreen(component = tabs(componentContext))
    }

    private fun tabs(componentContext: ComponentContext): MainComponent = MainComponentImpl(
        componentContext = componentContext,
        logOut = ::navigateAuth,
        toSettings = ::navigateSettings
    )

    override fun navigateTabs() = navigation.navigate { stack ->
        stack.filterNot { it == Config.Auth || it == Config.TabConfig } + Config.TabConfig
    }

    override fun navigateAuth() = navigation.navigate { listOf(Config.Auth) }

    override fun navigateSettings() = navigation.navigate { stack ->
        stack + Config.Settings
    }

    @Serializable
    protected sealed interface Config {
        @Serializable
        data object Auth : Config

        @Serializable
        data object Settings : Config

        @Serializable
        data object TabConfig : Config
    }
}
