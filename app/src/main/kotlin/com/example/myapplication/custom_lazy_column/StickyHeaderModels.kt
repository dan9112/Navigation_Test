package com.example.myapplication.custom_lazy_column

import androidx.compose.runtime.Composable
// Основной элемент списка
data class StickyLazyItem(
    val key: Any,
    val contentType: Any?,
    val content: @Composable () -> Unit,
    val headerKey: Any?,
    val headerContent: (@Composable () -> Unit)?
)
/*sealed interface StickyLazyItem {
    val key: Any
    val contentType: Any?
    val content: @Composable () -> Unit

    data class Common(
        override val key: Any,
        override val contentType: Any?,
        override val content: @Composable () -> Unit,
        val headerKey: Any?,
        val headerContent: (@Composable () -> Unit)?
    ) : StickyLazyItem

    data class Header(
        override val key: Any,
        override val contentType: Any?,
        override val content: @Composable () -> Unit,
    ) : StickyLazyItem
}*/

// Информация о диапазоне заголовка
data class HeaderRange(
    val key: Any,
    val range: IntRange,
    val content: @Composable () -> Unit
)
