package com.example.navigation_test.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner

interface BackComponent<T : Any> : BackHandlerOwner {
    val stack: Value<ChildStack<*, T>>
}

interface RootComponent : RootComponentCommon, BackComponent<PrimaryScreen> {
    fun navigateBack()
}

internal class RootComponentImpl(componentContext: ComponentContext) :
    RootComponentCommonImpl(componentContext), RootComponent {
    override fun navigateBack() = navigation.pop()
}
