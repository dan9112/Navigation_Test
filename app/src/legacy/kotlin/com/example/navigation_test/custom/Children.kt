package com.example.navigation_test.custom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.example.navigation_test.AppContainer
import com.example.navigation_test.AppContent
import com.example.navigation_test.decompose.BackComponent
import com.example.navigation_test.decompose.RootComponent
import com.example.navigation_test.decompose.SecondaryScreen
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
    crossinline content: @Composable (SecondaryScreen) -> Unit
) {
    Children(component = component) {
        content(it.instance)
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
        modifier = modifier
    ) { content(it) }
}
