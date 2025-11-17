package com.example.myapplication.custom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatableV2
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.example.myapplication.AppContainer
import com.example.myapplication.AppContent
import com.example.myapplication.decompose.BackComponent
import com.example.myapplication.decompose.RootComponent
import com.example.myapplication.decompose.SecondaryScreen
import com.arkivanov.decompose.extensions.compose.stack.Children as DecomposeChildren

@Composable
fun App(
    modifier: Modifier = Modifier,
    component: RootComponent
) {
    AppContainer {
        Children(
            component = component,
            modifier = modifier
        ) {
            AppContent(component = component, child = it)
        }
    }
}


@Composable
inline fun Main(
    component: BackComponent<SecondaryScreen>,
    crossinline content: @Composable () -> Unit
) {
    Children(component = component) {
        content()
    }
}


@OptIn(ExperimentalDecomposeApi::class)
@Composable
inline fun <T : Any> Children(
    component: BackComponent<T>,
    modifier: Modifier = Modifier,
    crossinline content: @Composable (Child.Created<Any, T>) -> Unit
) {
    DecomposeChildren(
        stack = component.stack,
        modifier = modifier,
        animation = predictiveBackAnimation(
            backHandler = component.backHandler,
            fallbackAnimation = stackAnimation(animator = fade() + scale()),
            selector = { backEvent, _, _ -> androidPredictiveBackAnimatableV2(initialBackEvent = backEvent) },
            onBack = component::navigateBack,
        )
    ) { content(it) }
}
